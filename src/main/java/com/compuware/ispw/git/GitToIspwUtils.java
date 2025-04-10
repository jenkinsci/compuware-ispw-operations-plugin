/**
* THESE MATERIALS CONTAIN CONFIDENTIAL INFORMATION AND TRADE SECRETS OF BMC SOFTWARE, INC. YOU SHALL MAINTAIN THE MATERIALS AS
* CONFIDENTIAL AND SHALL NOT DISCLOSE ITS CONTENTS TO ANY THIRD PARTY EXCEPT AS MAY BE REQUIRED BY LAW OR REGULATION. USE,
* DISCLOSURE, OR REPRODUCTION IS PROHIBITED WITHOUT THE PRIOR EXPRESS WRITTEN PERMISSION OF BMC SOFTWARE, INC.
*
* ALL BMC SOFTWARE PRODUCTS LISTED WITHIN THE MATERIALS ARE TRADEMARKS OF BMC SOFTWARE, INC. ALL OTHER COMPANY PRODUCT NAMES
* ARE TRADEMARKS OF THEIR RESPECTIVE OWNERS.
*
* (c) Copyright 2022, 2025 BMC Software, Inc.
*/
package com.compuware.ispw.git;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.gitclient.GitClient;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCertificateCredentials;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.compuware.ispw.restapi.Constants;
import com.compuware.ispw.restapi.util.RestApiUtils;
import com.compuware.jenkins.common.configuration.CpwrGlobalConfiguration;
import com.compuware.jenkins.common.utils.CommonConstants;
import com.google.common.collect.Iterables;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Computer;
import hudson.model.Item;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.git.Branch;
import hudson.plugins.git.GitChangeLogParser;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.Revision;
import hudson.plugins.git.util.BuildData;
import hudson.remoting.VirtualChannel;
import hudson.scm.ChangeLogSet;
import hudson.scm.SCM;
import hudson.scm.SCMRevisionState;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import jenkins.model.Jenkins;

public class GitToIspwUtils
{

	public static ListBoxModel buildStandardCredentialsIdItems(@AncestorInPath Jenkins context,
			@QueryParameter String credentialsId, @AncestorInPath Item project)
	{		
		List<StandardCredentials> credentials = CredentialsProvider.lookupCredentials(StandardCredentials.class,
				project, ACL.SYSTEM, Collections.<DomainRequirement>emptyList());

		StandardListBoxModel model = new StandardListBoxModel();

		model.add(new Option(StringUtils.EMPTY, StringUtils.EMPTY, false));

		for (StandardCredentials credential : credentials) {
			boolean isSelected = false;

			if (credentialsId != null) {
				isSelected = credentialsId.matches(credential.getId());
			}

			String description = Util.fixEmptyAndTrim(credential.getDescription());

			if (credential instanceof StandardUsernamePasswordCredentials) {
				StandardUsernamePasswordCredentials standardUsernamePasswordCredentials = (StandardUsernamePasswordCredentials) credential;
				model.add(new Option(
						standardUsernamePasswordCredentials.getUsername()
								+ (description != null ? " (" + description + ")" : StringUtils.EMPTY),
						standardUsernamePasswordCredentials.getId(), isSelected));
			} else if (credential instanceof StandardCertificateCredentials) {
				StandardCertificateCredentials certificateCredentials = (StandardCertificateCredentials) credential;
				model.add(new Option(
						certificateCredentials.getId()
								+ (description != null ? " (" + description + ")" : StringUtils.EMPTY),
						certificateCredentials.getId(), isSelected));
			}
		}

		return model;
	}

	public static ListBoxModel buildContainerPrefItems(@AncestorInPath Jenkins context, @QueryParameter String containerPref,
			@AncestorInPath Item project)
	{
		ListBoxModel model = new ListBoxModel();

		model.add(new Option(GitToIspwConstants.CONTAINER_PREF_PER_COMMIT, GitToIspwConstants.CONTAINER_PREF_PER_COMMIT));
		model.add(new Option(GitToIspwConstants.CONTAINER_PREF_PER_BRANCH, GitToIspwConstants.CONTAINER_PREF_PER_BRANCH));
		model.add(new Option(GitToIspwConstants.CONTAINER_PREF_CUSTOM, GitToIspwConstants.CONTAINER_PREF_CUSTOM));

		return model;
	}

	public static Map<String, RefMap> parse(String branchMapping)
	{
		Map<String, RefMap> map = new HashMap<String, RefMap>();

		String[] lines = branchMapping.split("\n"); //$NON-NLS-1$
		for (String line : lines)
		{
			line = StringUtils.trimToEmpty(line);

			if (line.startsWith("#")) //$NON-NLS-1$
			{
				continue;
			}

			int indexOfArrow = line.indexOf("=>"); //$NON-NLS-1$
			if (indexOfArrow != -1)
			{
				String pattern = StringUtils.trimToEmpty(line.substring(0, indexOfArrow));
				String ispwLevel = StringUtils.EMPTY;
				String containerPref = GitToIspwConstants.CONTAINER_PREF_PER_COMMIT;
				String containerDesc = StringUtils.EMPTY;

				String rest = line.substring(indexOfArrow + 2);
				StringTokenizer tokenizer = new StringTokenizer(rest, ",");
				if (tokenizer.hasMoreTokens())
				{
					ispwLevel = StringUtils.trimToEmpty(tokenizer.nextToken());
				}

				if (tokenizer.hasMoreElements())
				{
					containerPref = StringUtils.trimToEmpty(tokenizer.nextToken());
				}

				if (tokenizer.hasMoreElements())
				{
					containerDesc = StringUtils.trimToEmpty(tokenizer.nextToken());
				}

				RefMap refMap = new RefMap(ispwLevel, containerPref, containerDesc);
				map.put(pattern, refMap);
			}
		}

		return map;
	}

	/**
	 * Gets the ref, refId, fromHash, and toHash environment variables and trims them to empty.
	 * 
	 * @param envVars
	 *            the EnvVars for Jenkins
	 */
	public static void trimEnvironmentVariables(EnvVars envVars)
	{
		String toHash = envVars.get(GitToIspwConstants.VAR_TO_HASH, null);
		String fromHash = envVars.get(GitToIspwConstants.VAR_FROM_HASH, null);
		String ref = envVars.get(GitToIspwConstants.VAR_REF, null);
		String refId = envVars.get(GitToIspwConstants.VAR_REF_ID, null);

		envVars.put(GitToIspwConstants.VAR_TO_HASH, StringUtils.trimToEmpty(toHash));
		envVars.put(GitToIspwConstants.VAR_FROM_HASH, StringUtils.trimToEmpty(fromHash));
		envVars.put(GitToIspwConstants.VAR_REF, StringUtils.trimToEmpty(ref));
		envVars.put(GitToIspwConstants.VAR_REF_ID, StringUtils.trimToEmpty(refId));
	}

	/**
	 * Get file path in virtual workspace
	 * 
	 * @param envVars
	 *            the jenkins env
	 * @param fileName
	 *            the file inside virtual workspace
	 * @return the file path
	 */
	public static FilePath getFilePathInVirtualWorkspace(EnvVars envVars, String fileName)
	{
		FilePath filePath = null;

		try
		{
			String workspacePath = envVars.get(Constants.ENV_VAR_WORKSPACE);
			if(workspacePath == null)
			{
				return null;
			}
			
			String nodeName = envVars.get(Constants.ENV_VAR_NODENAME);
			if (nodeName.contentEquals(Constants.ENV_VAR_MASTER) || nodeName.contentEquals(Constants.ENV_VAR_BUILT_IN_NODE))
			{
				FilePath wsPath = new FilePath(new File(workspacePath));
				filePath = new FilePath(wsPath, fileName);
			}
			else
			{
				Jenkins jenkins = Jenkins.getInstanceOrNull();
				if (jenkins == null)
				{
					throw new AbortException(
							"The Jenkins instance " + nodeName + " has not been started or was already shut down.");
				}
				else
				{
					Computer computer = jenkins.getComputer(nodeName);
					if (computer != null)
					{
						FilePath wsPath = new FilePath(computer.getChannel(), workspacePath);
						filePath = new FilePath(wsPath, fileName);
					}
					else
					{
						throw new AbortException("Unable to access the Jenkins instance " + nodeName);
					}
				}
			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		return filePath;
	}
	
	/**
	 * Calls the IspwCLI and returns whether the execution was successful. Any exceptions thrown by the executor are caught and
	 * returned as a boolean.
	 * 
	 * @param launcher
	 *            the launcher
	 * @param build
	 *            the Jenkins Run
	 * @param logger
	 *            the logger
	 * @param envVars
	 *            the environment variables including ref, refId, fromHash, and toHash
	 * @param refMap
	 *            the ref map
	 * @param publishStep
	 *            publish step
	 * @return a boolean to indicate success
	 * @throws InterruptedException the exception
	 * @throws IOException the exception
	 */
	public static boolean callCli(Launcher launcher, Run<?, ?> build, PrintStream logger, EnvVars envVars, RefMap refMap,
			IGitToIspwPublish publishStep) throws InterruptedException, IOException
	{
		CpwrGlobalConfiguration globalConfig = CpwrGlobalConfiguration.get();
		RestApiUtils.assertNotNull(logger, globalConfig, "Jenkins:launcher cannot be null");

		VirtualChannel vChannel = launcher.getChannel();
		RestApiUtils.assertNotNull(logger, vChannel, "Jenkins:vChannel cannot be null");

		String toHash = envVars.get(GitToIspwConstants.VAR_TO_HASH, null);
		String fromHash = envVars.get(GitToIspwConstants.VAR_FROM_HASH, null);
		String ref = envVars.get(GitToIspwConstants.VAR_REF, null);
		String refId = envVars.get(GitToIspwConstants.VAR_REF_ID, null);
		
		if (RestApiUtils.isIspwDebugMode())
		{
			logger.println(String.format("toHash=%s, fromHash=%s, ref=%s, refId=%s", toHash, fromHash, ref, refId));
		}
		RestApiUtils.assertNotNull(logger, refMap,
				"refMap is null. Failed to mapping refId: %s to refMap. Please refine your branch mapping to match the branch name or ID in order to find correct refId.",
				refId);
		
		logger.println("Matched on the following mapping: " + refMap.toString());
		
		Properties remoteProperties = vChannel.call(new RemoteSystemProperties());
		String remoteFileSeparator = remoteProperties.getProperty(CommonConstants.FILE_SEPARATOR_PROPERTY_KEY);
		
		String workspacePath = envVars.get(Constants.ENV_VAR_WORKSPACE);
		String topazCliWorkspace = workspacePath + remoteFileSeparator + CommonConstants.TOPAZ_CLI_WORKSPACE;
		
		logger.println("TopazCliWorkspace: " + topazCliWorkspace); //$NON-NLS-1$
		
		String osFile = launcher.isUnix()
				? GitToIspwConstants.SCM_DOWNLOADER_CLI_SH
				: GitToIspwConstants.SCM_DOWNLOADER_CLI_BAT;
		String cliScriptFile = globalConfig.getTopazCLILocation(launcher) + remoteFileSeparator + osFile;
		logger.println("CLI Script File: " + cliScriptFile); //$NON-NLS-1$
		String cliScriptFileRemote = new FilePath(vChannel, cliScriptFile).getRemote();
		logger.println("CLI Script File Remote: " + cliScriptFileRemote); //$NON-NLS-1$

		FilePath workDir = new FilePath(vChannel, build.getRootDir().toString());
		workDir.mkdirs();

		if (RestApiUtils.isIspwDebugMode())
		{
			String buildTag = envVars.get("BUILD_TAG"); //$NON-NLS-1$
			logger.println("Getting buildTag =" + buildTag);
		}

		boolean success = true;
		CliExecutor cliExecutor = new CliExecutor(logger, build, launcher, envVars, workspacePath, topazCliWorkspace,
				globalConfig, cliScriptFileRemote, workDir);
		try
		{
			String ispwLevel = StringUtils.EMPTY;
			String containerDesc = StringUtils.EMPTY;
			String containerPref = StringUtils.EMPTY;
			
			//we've assert refMap is not null
			ispwLevel = refMap.getIspwLevel();
			containerDesc = refMap.getContainerDesc();
			containerPref = refMap.getContainerPref();
			
			success = cliExecutor.execute(publishStep.getConnectionId(), publishStep.getCredentialsId(),
					publishStep.getRuntimeConfig(), publishStep.getStream(), publishStep.getApp(), publishStep.getSubAppl(), ispwLevel,
					containerPref, containerDesc, publishStep.getGitRepoUrl(),
					publishStep.getGitCredentialsId(), ref, refId, fromHash, toHash, publishStep.getIspwConfigPath());
		}
		catch (AbortException e)
		{
			logger.println(e.getMessage());
			
			if (RestApiUtils.isIspwDebugMode())
			{
				e.printStackTrace(logger);
			}
			
			success = false;
		}

		if (!success)
		{
			if (fromHash.trim().isEmpty() || toHash.trim().contentEquals("-2"))
			{
				logger.println("Failure: Synchronization failed.");
			}
			else if (fromHash.contentEquals("-1"))
			{
				logger.println("Failure: Synchronization for " + toHash.trim().replaceAll(":",  ", "));
			}
			else
			{
				logger.println("Failure: Synchronization for push ending with commit " + toHash.trim());
			}
		}
		
		return success;
	}
	
	/**
	 * Get the changed log 
	 * @param run  
	 *           the Jenkins build
	 * @param logger 
	 *           logging the message
	 * @return list of ChangeLogSet
	 */
	public static List<? extends ChangeLogSet<? extends ChangeLogSet.Entry>> getChangeSets(Run<?, ?> run, PrintStream logger)
	{
		if (run instanceof AbstractBuild)
		{
			//freestyle project
			return Collections.singletonList(((AbstractBuild<?, ?>) run).getChangeSet());
		}
		else if (run instanceof WorkflowRun)
		{
			//pipeline
			return ((WorkflowRun) run).getChangeSets();
		}

		return null;
	}
	
	/**
	 * check if the same revision is used by requested build and its previous build
	 * @param run 
	 *           the requested build
	 * @param gitSCM 
	 *           the Git SCM used by the build
	 * @param logger 
	 *           logger for logging the message
	 * @return true or false
	 */
	public static boolean isSameRevisionUsedbyLastBuild(WorkflowRun run, GitSCM gitSCM, PrintStream logger)
	{
		boolean result = true;
		
		Revision curRevision = getRevision(run, gitSCM);

		WorkflowRun prevBuild = run.getPreviousBuild();

		if (prevBuild != null) {
			Revision preBuildRevision = getRevision(prevBuild, gitSCM);

			if (curRevision != null && preBuildRevision != null) {
				result = isSameRevision(curRevision, preBuildRevision) && !prevBuild.isBuilding();

				if (RestApiUtils.isIspwDebugMode()) {
					Branch branch = Iterables.getFirst(curRevision.getBranches(), null);

					if (result) {
						logger.println(
								"The same revision " + curRevision.getSha1String() + " for branch " + branch.getName() //$NON-NLS-1$ //$NON-NLS-2$
										+ " is used for computing the changelog for the Git source "); //$NON-NLS-1$
					}
				}
			}
		}

		return result;
	}
	
	/**
	 * Check if the same revision is used between two runs for a multibranch pipeline project
	 * @param firstRevision  
	 *             first revision to compare
	 * @param secondBuildRevision 
	 *             second revision to compare
	 * @return true or false
	 */
	public static boolean isSameRevision(Revision firstRevision, Revision secondBuildRevision)
	{
		if (firstRevision != null && secondBuildRevision != null && firstRevision.getBranches() != null
				&& secondBuildRevision.getBranches() != null)
		{
			if (firstRevision.getSha1String() != null
					&& firstRevision.getSha1String().equals(secondBuildRevision.getSha1String()))
			{
				return true;
			}
		}

		return false;
	}
	
	/**
	 * get the Git revision of the specific build of the multibranch pipeline project
	 * @param run 	the current build of the multibranch pipeline project
	 * @param gitSCM  the Git SCM of the multibranch pipeline project
	 * @return Revision returns the Git revision of the Jenkins build.
	 */
	public static Revision getRevision(WorkflowRun run, GitSCM gitSCM) 
	{
		BuildData buildData = gitSCM.getBuildData(run);
		if (buildData != null) {
			return buildData.getLastBuiltRevision();
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param run the workflow run instance
	 * @param listener the task listener
	 * @return true if need recalc
	 */
	public static boolean isReCalculateChangesRequired(WorkflowRun run, TaskListener listener)
	{
		if (run != null)
		{
			WorkflowJob job = run.getParent();
			Collection<? extends SCM> scms = job.getSCMs();

			if (scms != null && !scms.isEmpty())
			{
				SCM thescm = scms.iterator().next();
				WorkflowRun previousBuild = run.getPreviousBuild();
				boolean previousBuildIsNotSuccessful = previousBuild != null && !Result.SUCCESS.equals(previousBuild.getResult());
				if (thescm instanceof GitSCM 
						&& (isSameRevisionUsedbyLastBuild(run, (GitSCM) thescm, listener.getLogger())
								|| previousBuildIsNotSuccessful))
				{
					return true;
				}
			}
		}

		return false;
	}
	
	
	
	@SuppressWarnings("deprecation")
	public static List <CustomGitChangeSetList> calculateGitSCMChanges(Run<?, ?> run, FilePath workspace, TaskListener listener, EnvVars envVars, IGitToIspwPublish publishStep) 
	{
		CustomGitChangeSetList customGitChangeSetList = null;
        List <CustomGitChangeSetList> listChangeLogSet = new ArrayList<CustomGitChangeSetList> ();
		
        PrintStream logger = listener.getLogger();	
        
        if(run != null) {
			WorkflowRun curRun = ((WorkflowRun) run);
			
			WorkflowJob job = curRun.getParent();
			if (job != null)
			{
	
				Collection<? extends SCM> scms = job.getSCMs();
				GitSCM gitScm = null;
				if (scms != null && scms.size() >= 1)
				{
					if (scms.size() == 1)
					{
						SCM thescm = scms.iterator().next();
						if (thescm instanceof GitSCM)
						{
							gitScm = (GitSCM) thescm;
						}
					}
					if (scms.size() > 1)
					{
						for (SCM scm : scms)
						{
							if (scm instanceof GitSCM)
							{
								GitSCM sourceGitScm = (GitSCM) scm;
								if (workspace == null)
								{
									logger.println("Workspace is not available.");
									continue;
								}

								FilePath repoCheckFolder = workspace.child("repo-check");
								try
								{
									if (repoCheckFolder.exists())
									{
										repoCheckFolder.deleteRecursive();
									}
									repoCheckFolder.mkdirs();
								    SCMRevisionState revisionState = SCMRevisionState.NONE;
								    sourceGitScm.checkout(run, new Launcher.LocalLauncher(listener), repoCheckFolder, listener,
								                          null, revisionState); 
								    String fileName = "ispwconfig.yml";
								    String configPath= publishStep.getIspwConfigPath();
								    if(configPath != null && !configPath.isEmpty())
								    {
								    	Path path = Paths.get(configPath);
								    	if(path != null)
								    	{
								    		Path filePath = path.getFileName();	
								    		if(filePath != null)
								    		{
								    			fileName = filePath.toString();
								    		}
								    	} 
								    }
								    FilePath[] configFiles = repoCheckFolder.list("**/"+ fileName);
									if (configFiles.length > 0)
									{
										gitScm = sourceGitScm;
										logger.println("Found mapping file at: " + configFiles[0].getRemote());
										break;
									}
								}
								catch (IOException | InterruptedException e)
								{
									logger.println("Error during checkout: " + e.getMessage());
								}
							}
						}

						if (gitScm == null)
						{
							logger.println("No repository found with ispwconfig.yml.");
						}
					}						
	
						if (RestApiUtils.isIspwDebugMode())
						{
							logger.println("Retrieve the GitSCM object " + gitScm.getScmName()); //$NON-NLS-1$
						}
						
						// find the commit to compute the changelog
	
						WorkflowRun theRun = curRun;
						WorkflowRun preRun = theRun.getPreviousBuild();
						Revision revision = null ;
						if (gitScm != null) 
						{
						    revision = getRevision(theRun, gitScm);
						} else 
						{
						    throw new IllegalArgumentException("theRun or gitScm is null");
						}
						if (revision == null) {
						    throw new IllegalStateException("getRevision() returned null");
						}
						
						logger.println("Revision: " + revision.toString()); //$NON-NLS-1$
						
						Revision preRevision = null;
	
						if (preRun != null)
						{
							// if previous run failed, recalculate the changelog starting previous successful run else recalculate changelog from previous build with different revision
							if (preRun.getResult() != null && !Result.SUCCESS.equals(preRun.getResult())) 
							{
								WorkflowRun previousSuccessfulBuild = theRun.getPreviousSuccessfulBuild();
								if (null != previousSuccessfulBuild) {
									logger.println("Since the last build failed, changelog will be calculated from last successful build : "+previousSuccessfulBuild);
									preRevision = getRevision(previousSuccessfulBuild, gitScm);
									if(preRevision != null)
									{
										logger.println("PreRevision: " + preRevision.toString()); //$NON-NLS-1$
									}
								} else {
									//calculate changelog based on first build in case there is no prior successful build
									logger.println("There is no prior successful build for this job.");
									preRevision = getRevision(job.getFirstBuild(), gitScm);
									if(preRevision != null)
									{
										logger.println("PreRevision: " + preRevision.toString()); //$NON-NLS-1$
									}
								}
								revision = getRevision(curRun, gitScm);
							} else {
								preRevision = getRevision(preRun, gitScm);
								if(preRevision != null)
								{
									logger.println("PreRevision: " + preRevision.toString()); //$NON-NLS-1$
								}
								
								while (isSameRevision(revision, preRevision) && !preRun.isBuilding())
								{
									theRun = preRun;
									preRun = theRun.getPreviousBuild();
									
									if (preRun != null)
									{
										revision = getRevision(theRun, gitScm);
										preRevision = getRevision(preRun, gitScm);
									} else {
										break;
									}
								}
							}
						}
	
						if (preRun == null)
						{
							logger.println("Skipping changelog. There is no proper revision for computing the changelog."); //$NON-NLS-1$
							return listChangeLogSet;
						}
						if(revision!=null && preRevision!=null)
						{
							logger.println("Compute the changelog between [ " + revision.toString() + "] and [" + preRevision.toString() //$NON-NLS-1$ //$NON-NLS-2$
							+ "]."); //$NON-NLS-1$
						}
						else
						{
							logger.println("Revision or Prerevision is null");
						}
						try
						{
							GitClient git = gitScm.createClient(listener, envVars, run, workspace);
	
							StringWriter sw = new StringWriter();
							try 
							{
							    if (preRevision == null || revision == null) 
							    {
							        throw new IllegalStateException("preRevision or revision is null");
							    }
							    git.changelog(preRevision.getSha1String(), revision.getSha1String(), sw);
							} 
							catch (InterruptedException e) 
							{
							    Thread.currentThread().interrupt(); 
							    throw new RuntimeException("Changelog retrieval was interrupted", e);
							}
							
							String logString = sw.toString();
							logger.println("Calculated changed log = \n " + logString); //$NON-NLS-1$
	
							if (logString.trim().length() > 0)
							{
								if (RestApiUtils.isIspwDebugMode())
								{
									logger.println("Calculated changed log = \n " + logString); //$NON-NLS-1$
								}
	
								String[] lines = logString.split("\\r?\\n"); //$NON-NLS-1$
	
								if (RestApiUtils.isIspwDebugMode())
								{
									logger.println("The changed log array length is " + lines.length); //$NON-NLS-1$
								}
	
								logger.println("Start to parse the changelog."); //$NON-NLS-1$
	
								GitChangeLogParser logparser = (GitChangeLogParser) gitScm.createChangeLogParser();
								List<String> logs = new ArrayList<String>(Arrays.asList(lines));
								customGitChangeSetList = new CustomGitChangeSetList(run, null, logparser.parse(logs));
								listChangeLogSet.add(customGitChangeSetList);
							}
						}
						catch (Exception x)
						{
							logger.println("Failed to calculate the changelog."); //$NON-NLS-1$
							if (RestApiUtils.isIspwDebugMode())
							{
								x.printStackTrace(logger);
							}						
						}
					
				}
			} 	
        }
		
		return listChangeLogSet;
	}

	/**
	 * Escapes the string argument passed in to conform with CLI standards
	 * 
	 * @param args
	 *            The command argument
	 * @return The escaped command argument, or the same command argument if no character substitution was done
	 */
	@SuppressWarnings("nls")
	public static String escapeArgument(String args)
	{
		Map<String, String> charactersToReplace = new HashMap<>();
		String escapedArg = args;
		// Add keys to look for within the arg, that will later be replaced by the value 
		charactersToReplace.put("%", "%%");
		Iterator<Entry<String, String>> it = charactersToReplace.entrySet().iterator();

		while (it.hasNext())
		{
			Map.Entry<String, String> pair = it.next();
			escapedArg = args.replaceAll(pair.getKey(), pair.getValue());
		}

		return escapedArg;
	}

}
