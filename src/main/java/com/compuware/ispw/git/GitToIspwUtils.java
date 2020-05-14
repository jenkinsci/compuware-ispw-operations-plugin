package com.compuware.ispw.git;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.compuware.ispw.restapi.Constants;
import com.compuware.ispw.restapi.util.RestApiUtils;
import com.compuware.jenkins.common.configuration.CpwrGlobalConfiguration;
import com.compuware.jenkins.common.utils.CommonConstants;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Computer;
import hudson.model.Item;
import hudson.model.Run;
import hudson.remoting.VirtualChannel;
import hudson.scm.ChangeLogSet;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import jenkins.model.Jenkins;

public class GitToIspwUtils
{

	public static ListBoxModel buildStandardCredentialsIdItems(@AncestorInPath Jenkins context,
			@QueryParameter String credentialsId, @AncestorInPath Item project)
	{
		List<StandardUsernamePasswordCredentials> creds = CredentialsProvider.lookupCredentials(
				StandardUsernamePasswordCredentials.class, project, ACL.SYSTEM, Collections.<DomainRequirement> emptyList());

		StandardListBoxModel model = new StandardListBoxModel();

		model.add(new Option(StringUtils.EMPTY, StringUtils.EMPTY, false));

		for (StandardUsernamePasswordCredentials c : creds)
		{
			boolean isSelected = false;

			if (credentialsId != null)
			{
				isSelected = credentialsId.matches(c.getId());
			}

			String description = Util.fixEmptyAndTrim(c.getDescription());
			model.add(new Option(c.getUsername() + (description != null ? " (" + description + ")" : StringUtils.EMPTY), //$NON-NLS-1$ //$NON-NLS-2$
					c.getId(), isSelected));
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
			String nodeName = envVars.get(Constants.ENV_VAR_NODENAME);
			if (nodeName.contentEquals(Constants.ENV_VAR_MASTER))
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
	 * @param isPrintHelpOnly
	 *            only print help
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
		
		logger.println(String.format("toHash=%s, fromHash=%s, ref=%s, refId=%s", toHash, fromHash, ref, refId));
		RestApiUtils.assertNotNull(logger, refMap,
				"refMap is null. Failed to mapping refId: %s to refMap. Please refine your branch mapping to match the branch name or ID in order to find correct refId.",
				refId);
		
		logger.println("Mapping refId: " + refId + " to refMap=" + refMap.toString());
		
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
					publishStep.getRuntimeConfig(), publishStep.getStream(), publishStep.getApp(), ispwLevel,
					containerPref, containerDesc, publishStep.getGitRepoUrl(),
					publishStep.getGitCredentialsId(), ref, refId, fromHash, toHash, publishStep.getIspwConfigPath());
		}
		catch (AbortException e)
		{
			logger.println(e);
			
			if (RestApiUtils.isIspwDebugMode())
			{
				e.printStackTrace(logger);
			}
			
			success = false;
		}

		if (!success)
		{
			if (fromHash.trim().isEmpty())
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
	
	/*
	 (non-Javadoc) 
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
}
