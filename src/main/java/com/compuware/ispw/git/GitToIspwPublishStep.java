package com.compuware.ispw.git;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import com.compuware.ispw.restapi.Constants;
import com.compuware.ispw.restapi.util.RestApiUtils;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.Cause;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.ChangeLogSet;
import hudson.scm.EditType;
import hudson.scm.ChangeLogSet.AffectedFile;
import hudson.scm.ChangeLogSet.Entry;
import hudson.util.ListBoxModel;
import jenkins.branch.BranchIndexingCause;
import jenkins.model.Jenkins;

/**
* GIT to ISPW integration pipeline
* 
 * @author Sam Zhou
*
*/
public class GitToIspwPublishStep extends AbstractStepImpl implements IGitToIspwPublish
{
	// GIT related
	private String gitRepoUrl = DescriptorImpl.gitRepoUrl;
	private String gitCredentialsId = DescriptorImpl.gitCredentialsId;

	// ISPW related
	private String connectionId = DescriptorImpl.connectionId;
	private String credentialsId = DescriptorImpl.credentialsId;
	private String runtimeConfig = DescriptorImpl.runtimeConfig;
	private String stream = DescriptorImpl.stream;
	private String app = DescriptorImpl.app;
	private String ispwConfigPath = DescriptorImpl.ispwConfigPath;

	// Branch mapping
	private String branchMapping = DescriptorImpl.branchMapping;

	@DataBoundConstructor
	public GitToIspwPublishStep()
	{
	}

	public static final class Execution extends AbstractSynchronousNonBlockingStepExecution<Integer>
	{
		private static final long serialVersionUID = 1L;

		@Inject
		private transient GitToIspwPublishStep step;

		@StepContextParameter
		private transient Run<?, ?> run;
		@StepContextParameter
		private transient TaskListener listener;

		@Override
		protected Integer run() throws Exception
		{
			boolean firstBuild = false;
			WorkflowRun workflowRun = null;
			WorkflowRun previousRun = null;
			PrintStream logger = listener.getLogger();

			EnvVars envVars = getContext().get(hudson.EnvVars.class);
			GitToIspwUtils.trimEnvironmentVariables(envVars);
			           
            FilePath workspace = getContext().get(FilePath.class);
                        
			List<? extends ChangeLogSet<? extends Entry>> changeSets = GitToIspwUtils.getChangeSets(run, logger);
			String branchName = envVars.get("BRANCH_NAME", StringUtils.EMPTY); 

			boolean calculatLog = false;
			if (run instanceof WorkflowRun)
			{
				workflowRun = (WorkflowRun) run;
				previousRun = workflowRun.getPreviousBuild();
			}
			if (run instanceof WorkflowRun && (changeSets == null || changeSets.isEmpty()))
			{
				//If we decide only recalculate based on whether the previous build failed, we need to comment out
				//the following condition
				if (previousRun != null /*&& !hudson.model.Result.SUCCESS.equals(previousRun.getResult())*/)
				{
					if (GitToIspwUtils.isReCalculateChangesRequired(workflowRun, listener))
					{
						if (RestApiUtils.isIspwDebugMode())
						{
							logger.println(
									"GitToIspwPublishStep: Calculate the changelog when re-running the last build."); //$NON-NLS-1$
						}
						calculatLog = true;
					}
				}
			}
			
			FilePath buildParmPath = GitToIspwUtils.getFilePathInVirtualWorkspace(envVars, Constants.BUILD_PARAM_FILE_NAME);
        	
			if (buildParmPath.exists()) {
        		logger.println("Remove the old build parm files." + buildParmPath.getName()); //$NON-NLS-1$
        		buildParmPath.delete();
        	}
			
			//For multibranch pipeline only
			if ((changeSets != null || calculatLog) && !branchName.isEmpty())
			{
				HashSet<String> changedPathSet = new HashSet<String>();
				String paths = null;
				
				logger.println("Branch name: " + branchName);
				Iterator<? extends ChangeLogSet<? extends Entry>> itrChangeSets = changeSets.iterator();

				if (calculatLog)
				{
					if (RestApiUtils.isIspwDebugMode())
					{
						logger.println("GitToIspwPublishStep: Calculate the change log. "); //$NON-NLS-1$
					}

					itrChangeSets = GitToIspwUtils.calculateGitSCMChanges(run, workspace, listener, envVars).iterator();
				}
				
				if (!itrChangeSets.hasNext())
				{
					if (previousRun == null)
					{
						List<Cause> causes = run.getCauses();
						for (int i = 0; i < causes.size(); i++) 
						{
							//for the first build, there's no change sets so need to figure out
							//the changes via another way
							if (causes.get(i) instanceof BranchIndexingCause)
							{
								firstBuild = true;
								break;
							}
						}
					}
					if (!firstBuild)
					{
						logger.println("No changed files were detected.");
						return 0; //return as success
					}
				}
				
				while (itrChangeSets.hasNext())
				{
					ChangeLogSet<? extends Entry> changeLogSets = itrChangeSets.next();
					Iterator<? extends Entry> itrChangeSet = changeLogSets.iterator();
					while (itrChangeSet.hasNext())
					{
						Entry changeLogSet = itrChangeSet.next();
						logger.println("Commit ID = " + changeLogSet.getCommitId());
						
						Collection<? extends AffectedFile> affectedFiles = changeLogSet.getAffectedFiles();
						for (AffectedFile affectedFile : affectedFiles)
						{
							String affectedPath = affectedFile.getPath();
							if (affectedFile.getEditType() == EditType.DELETE)
							{
								affectedPath = "|" + changeLogSet.getCommitId() + "|" + affectedPath;
							}
							changedPathSet.add(affectedPath);
							
							if (RestApiUtils.isIspwDebugMode())
							{
								logger.println("GitToIspwPublishStep: add a file path - " + affectedPath); //$NON-NLS-1$
							}
						}
					}

					logger.println("ChangedPathSet = " + changedPathSet);
					
					if (!changedPathSet.isEmpty())
					{
						Set<String> pathSet = changedPathSet;
						paths = StringUtils.join(pathSet, ":");
					}
					else
					{
						logger.println("No changed files were detected.");
						return 0; //return as success
					}

					if (!firstBuild)
					{
						// provide a list of paths in the var_to_hash, and set var_from_hash to
						// GitToIspwConstants.VAR_FROM_HASH_TYPE_CHANGESET (indicator this is for
						// multibranch project)
						envVars.put(GitToIspwConstants.VAR_TO_HASH, paths);
						envVars.put(GitToIspwConstants.VAR_FROM_HASH, GitToIspwConstants.VAR_FROM_HASH_TYPE_CHANGESET);
						envVars.put(GitToIspwConstants.VAR_REF, branchName);
					}
				}
			}
			
			// Another method to get changes if there's no change set and it's the first build which only does
			// the branch indexing
			if (firstBuild)
			{
				// -2 indicates the first build of a new branch - can't depend on a change set
				// so have to do different logic in cli, which checks for the -2.
				envVars.put(GitToIspwConstants.VAR_TO_HASH, "-2");
				envVars.put(GitToIspwConstants.VAR_FROM_HASH, "-2");
				envVars.put(GitToIspwConstants.VAR_REF, branchName);
			}
	
			Map<String, RefMap> map = GitToIspwUtils.parse(step.branchMapping);
			if (RestApiUtils.isIspwDebugMode())
			{
				logger.println("map=" + map);
			}
			
			String refId = envVars.get(GitToIspwConstants.VAR_REF_ID, null);
			
			BranchPatternMatcher matcher = new BranchPatternMatcher(map, logger);
			String matchTo = null;
			RefMap refMap = null;
			if (StringUtils.isBlank(refId))
			{
				logger.println("Using branch name for branch pattern match: " + branchName);
				matchTo = StringUtils.trimToNull(branchName);
			}
			else
			{
				logger.println("Using refid for branch pattern match: " + refId);
				matchTo = StringUtils.trimToNull(refId);
			}
			RestApiUtils.assertNotNull(logger, matchTo, "Cannot match on the branch name or refId in the branch mapping. Both are null or empty.");
			
			refMap = matcher.match(matchTo);
			RestApiUtils.assertNotNull(logger, refMap,
					"Cannot find a branch pattern that matches the branch %s.  Please adjust your branch mapping.", matchTo);
			
			Launcher launcher = getContext().get(Launcher.class);
			if(GitToIspwUtils.callCli(launcher, run, logger, envVars, refMap, step))
			{
				return 0;
			}
			else
			{
				throw new AbortException("An error occurred while synchronizing source to ISPW");
			}
		}
	}



	@Extension
	public static final class DescriptorImpl extends AbstractStepDescriptorImpl
	{

		// GIT related
		public static final String gitRepoUrl = StringUtils.EMPTY;
		public static final String gitCredentialsId = StringUtils.EMPTY;

		// ISPW related
		public static final String connectionId = StringUtils.EMPTY;
		public static final String credentialsId = StringUtils.EMPTY;
		public static final String runtimeConfig = StringUtils.EMPTY;
		public static final String stream = StringUtils.EMPTY;
		public static final String app = StringUtils.EMPTY;
		public static final String ispwConfigPath = StringUtils.EMPTY;

		// Branch mapping

		public static final String branchMapping = GitToIspwConstants.BRANCH_MAPPING_DEFAULT;

		public static final String containerDesc = StringUtils.EMPTY;
		public static final String containerPref = StringUtils.EMPTY;

		public DescriptorImpl()
		{
			super(Execution.class);
		}

		@Override
		public String getDisplayName()
		{
			return "Git to ISPW Integration";
		}

		@Override
		public String getFunctionName()
		{
			return "gitToIspwIntegration";
		}

		// GIT
		public ListBoxModel doFillGitCredentialsIdItems(@AncestorInPath Jenkins context,
				@QueryParameter String gitCredentialsId, @AncestorInPath Item project)
		{
			return GitToIspwUtils.buildStandardCredentialsIdItems(context, gitCredentialsId, project);
		}

		// ISPW
		public ListBoxModel doFillConnectionIdItems(@AncestorInPath Jenkins context, @QueryParameter String connectionId,
				@AncestorInPath Item project)
		{
			return RestApiUtils.buildConnectionIdItems(context, connectionId, project);
		}

		public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Jenkins context, @QueryParameter String credentialsId,
				@AncestorInPath Item project)
		{
			return GitToIspwUtils.buildStandardCredentialsIdItems(context, credentialsId, project);
		}

	}

	@Initializer(before = InitMilestone.PLUGINS_STARTED)
	public static void xStreamCompatibility()
	{
	}

	/**
	 * @return the gitRepoUrl
	 */
	public String getGitRepoUrl()
	{
		return gitRepoUrl;
	}

	/**
	 * @param gitRepoUrl
	 *            the gitRepoUrl to set
	 */
	@DataBoundSetter
	public void setGitRepoUrl(String gitRepoUrl)
	{
		this.gitRepoUrl = gitRepoUrl;
	}

	/**
	 * @return the gitCredentialsId
	 */
	public String getGitCredentialsId()
	{
		return gitCredentialsId;
	}

	/**
	 * @param gitCredentialsId
	 *            the gitCredentialsId to set
	 */
	@DataBoundSetter
	public void setGitCredentialsId(String gitCredentialsId)
	{
		this.gitCredentialsId = gitCredentialsId;
	}

	/**
	 * @return the connectionId
	 */
	public String getConnectionId()
	{
		return connectionId;
	}

	/**
	 * @param connectionId
	 *            the connectionId to set
	 */
	@DataBoundSetter
	public void setConnectionId(String connectionId)
	{
		this.connectionId = connectionId;
	}

	/**
	 * @return the credentialsId
	 */
	public String getCredentialsId()
	{
		return credentialsId;
	}

	/**
	 * @param credentialsId
	 *            the credentialsId to set
	 */
	@DataBoundSetter
	public void setCredentialsId(String credentialsId)
	{
		this.credentialsId = credentialsId;
	}

	/**
	 * @return the runtimeConfig
	 */
	public String getRuntimeConfig()
	{
		return runtimeConfig;
	}

	/**
	 * @param runtimeConfig
	 *            the runtimeConfig to set
	 */
	@DataBoundSetter
	public void setRuntimeConfig(String runtimeConfig)
	{
		this.runtimeConfig = runtimeConfig;
	}

	/**
	 * @return the stream
	 */
	public String getStream()
	{
		return stream;
	}

	/**
	 * @param stream
	 *            the stream to set
	 */
	@DataBoundSetter
	public void setStream(String stream)
	{
		this.stream = stream;
	}

	/**
	 * @return the app
	 */
	public String getApp()
	{
		return app;
	}

	/**
	 * @param app
	 *            the app to set
	 */
	@DataBoundSetter
	public void setApp(String app)
	{
		this.app = app;
	}

	/**
	 * @return the branchMapping
	 */
	public String getBranchMapping()
	{
		return branchMapping;
	}

	/**
	 * @param branchMapping
	 *            the branchMapping to set
	 */
	@DataBoundSetter
	public void setBranchMapping(String branchMapping)
	{
		this.branchMapping = branchMapping;
	}

	/**
	 * @return the ispwConfigPath
	 */
	public String getIspwConfigPath()
	{
		return ispwConfigPath;
	}

	/**
	 * @param ispwConfigPath the ispwConfigPath to set
	 */
	@DataBoundSetter
	public void setIspwConfigPath(String ispwConfigPath)
	{
		this.ispwConfigPath = ispwConfigPath;
	}

}
