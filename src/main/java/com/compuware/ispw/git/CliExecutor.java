/**
* THESE MATERIALS CONTAIN CONFIDENTIAL INFORMATION AND TRADE SECRETS OF BMC SOFTWARE, INC. YOU SHALL MAINTAIN THE MATERIALS AS
* CONFIDENTIAL AND SHALL NOT DISCLOSE ITS CONTENTS TO ANY THIRD PARTY EXCEPT AS MAY BE REQUIRED BY LAW OR REGULATION. USE,
* DISCLOSURE, OR REPRODUCTION IS PROHIBITED WITHOUT THE PRIOR EXPRESS WRITTEN PERMISSION OF BMC SOFTWARE, INC.
*
* ALL BMC SOFTWARE PRODUCTS LISTED WITHIN THE MATERIALS ARE TRADEMARKS OF BMC SOFTWARE, INC. ALL OTHER COMPANY PRODUCT NAMES
* ARE TRADEMARKS OF THEIR RESPECTIVE OWNERS.
*
* (c) Copyright 2020 BMC Software, Inc.
*/

package com.compuware.ispw.git;

import java.io.IOException;
import java.io.PrintStream;
import org.apache.commons.lang.StringUtils;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.compuware.ispw.restapi.util.RestApiUtils;
import com.compuware.jenkins.common.configuration.CpwrGlobalConfiguration;
import com.compuware.jenkins.common.configuration.HostConnection;
import com.compuware.jenkins.common.utils.ArgumentUtils;
import com.compuware.jenkins.common.utils.CommonConstants;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.util.ArgumentListBuilder;

/**
 * 
 * A reusable CLI executor for both freestyle and pipeline Jenkins build
 * 
 * @author Sam Zhou
 *
 */
public class CliExecutor
{
	private PrintStream logger;
	private Run<?, ?> run;
	private EnvVars envVars;
	private Launcher launcher;

	private CpwrGlobalConfiguration globalConfig;

	private String targetFolder;
	private String topazCliWorkspace;
	private String gitLocalPath;
	private String cliScriptFileRemote;
	private FilePath workDir;

	private static int COMMAND_LINE_LIMITATION = 2048;
	
	public CliExecutor(PrintStream logger, Run<?, ?> run, Launcher launcher, EnvVars envVars, String gitLocalPath,
			String topazCliWorkspace, CpwrGlobalConfiguration globalConfig, String cliScriptFileRemote, FilePath workDir)

	{
		this.logger = logger;
		this.run = run;
		this.envVars = envVars;
		this.launcher = launcher;
		this.targetFolder = gitLocalPath;
		this.globalConfig = globalConfig;

		this.gitLocalPath = gitLocalPath;
		this.topazCliWorkspace = topazCliWorkspace;

		this.cliScriptFileRemote = cliScriptFileRemote;

		this.workDir = workDir;
	}


	public boolean execute(String connectionId, String credentialsId, String runtimeConfig,
			String stream, String app, String ispwLevel, String containerPref, String containerDesc, 
			String gitRepoUrl, String gitCredentialsId, String ref, String refId,
			String fromHash, String toHash, String ispwConfigPath) throws InterruptedException, IOException
	{
		String gitUserId = StringUtils.EMPTY;
		String gitPassword = StringUtils.EMPTY;
		String userId = StringUtils.EMPTY;
		String password = StringUtils.EMPTY;
		
		HostConnection connection = globalConfig.getHostConnection(connectionId);
		RestApiUtils.assertNotNull(logger, connection, "Unable to connect to host connection for connectionId: %s", connectionId);
		
		String host = ArgumentUtils.escapeForScript(connection.getHost());
		String port = ArgumentUtils.escapeForScript(connection.getPort());

		logger.println(String.format("Using host connection: %s:%s", host, port));
		
		String protocol = connection.getProtocol();
		String codePage = connection.getCodePage();
		String timeout = ArgumentUtils.escapeForScript(connection.getTimeout());

		StandardUsernamePasswordCredentials credentials = globalConfig.getLoginInformation(run.getParent(), credentialsId);
		RestApiUtils.assertNotNull(logger,  credentials, "The host credentials were not able to be obtained.");
		userId = ArgumentUtils.escapeForScript(credentials.getUsername());
		password = ArgumentUtils.escapeForScript(credentials.getPassword().getPlainText());
		if (RestApiUtils.isIspwDebugMode())
		{
			logger.println("host=" + host + ", port=" + port + ", protocol=" + protocol + ", codePage=" + codePage
					+ ", timeout=" + timeout + ", userId=" + userId + ", password=" + password + ", containerPref="
					+ containerPref + ", containerDesc=" + containerDesc);
		}

		StandardUsernamePasswordCredentials gitCredentials = globalConfig.getLoginInformation(run.getParent(),
				gitCredentialsId);
		if (gitCredentials != null)
		{
			gitUserId = ArgumentUtils.escapeForScript(gitCredentials.getUsername());
			gitPassword = ArgumentUtils.escapeForScript(gitCredentials.getPassword().getPlainText());
			if (RestApiUtils.isIspwDebugMode())
			{
				logger.println("gitRepoUrl=" + gitRepoUrl + ", gitUserId=" + gitUserId + ", gitPassword=" + gitPassword);
			}
		}
		else
		{
			logger.println("The git credentials were not able to be obtained.");
		}

		ArgumentListBuilder args = new ArgumentListBuilder();
		// build the list of arguments to pass to the CLI

		args.add(cliScriptFileRemote);
		args.add(GitToIspwConstants.ISPW_OPERATION_PARAM, "syncGitToIspw");

		// host connection
		args.add(CommonConstants.HOST_PARM, host);
		args.add(CommonConstants.PORT_PARM, port);
		args.add(CommonConstants.USERID_PARM, userId);
		args.add(CommonConstants.PW_PARM);
		args.add(password, true);

		if (StringUtils.isNotBlank(protocol))
		{
			args.add(CommonConstants.PROTOCOL_PARM, protocol);
		}

		args.add(CommonConstants.CODE_PAGE_PARM, codePage);
		args.add(CommonConstants.TIMEOUT_PARM, timeout);
		args.add(CommonConstants.TARGET_FOLDER_PARM, targetFolder);
		args.add(CommonConstants.DATA_PARM, topazCliWorkspace);
		
		if (StringUtils.isNotBlank(runtimeConfig))
		{
			args.add(GitToIspwConstants.ISPW_SERVER_CONFIG_PARAM, runtimeConfig);
		}

		// ispw
		args.add(GitToIspwConstants.ISPW_SERVER_STREAM_PARAM, stream);
		args.add(GitToIspwConstants.ISPW_SERVER_APP_PARAM, app);
		args.add(GitToIspwConstants.ISPW_SERVER_CHECKOUT_LEV_PARAM, ispwLevel);
		
		if(StringUtils.isNotBlank(ispwConfigPath)) {
			args.add(GitToIspwConstants.ISPW_CONFIG_PATH, StringUtils.trimToEmpty(ispwConfigPath));
		}
		
		if (StringUtils.isNotBlank(containerPref))
		{
			args.add(GitToIspwConstants.CONTAINER_CREATION_PREF_ARG_PARAM, StringUtils.trimToEmpty(containerPref));
		}

		if (StringUtils.isNotBlank(containerDesc))
		{
			args.add(GitToIspwConstants.CONTAINER_DESCRIPTION_ARG_PARAM, StringUtils.trimToEmpty(containerDesc));
		}
		
		// git
		args.add(GitToIspwConstants.GIT_USERID_PARAM, gitUserId);
		args.add(GitToIspwConstants.GIT_PW_PARAM);
		args.add(gitPassword, true);
		args.add(GitToIspwConstants.GIT_REPO_URL_PARAM, ArgumentUtils.escapeForScript(gitRepoUrl));
		args.add(GitToIspwConstants.GIT_REF_PARAM, ref);
		args.add(GitToIspwConstants.GIT_FROM_HASH_PARAM, fromHash);
		
		args.add(GitToIspwConstants.GIT_LOCAL_PATH_ARG_PARAM, gitLocalPath);

		boolean useHashFile = false;
		
		if ("-1".equals(fromHash)) {					
			ArgumentListBuilder argClone = args.clone();
			argClone.add(GitToIspwConstants.GIT_HASH_PARAM, toHash);
			String sCmd = argClone.toString();

			if (sCmd.length() > COMMAND_LINE_LIMITATION) {
				logger.println("The calculated list of changed programs is too long. Topaz CLI release 20.04.01 is the minimum release required for supporting a long command line used by Git to ISPW integration."); //$NON-NLS-1$
				useHashFile = true;	
			}
		}
		
		FilePath jenkinsWorkSpace = new FilePath(launcher.getChannel(), targetFolder);
		
		if (!jenkinsWorkSpace.exists()) {
			jenkinsWorkSpace.mkdirs();
		}
		
		FilePath toHashTempFile = null;
		
		if (useHashFile) 
		{
			toHashTempFile = jenkinsWorkSpace.createTempFile("toHash", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
			
			if (toHashTempFile.exists()) 
			{
				toHashTempFile.delete();
			}
			
			toHashTempFile = jenkinsWorkSpace.createTextTempFile("toHash", ".txt", toHash); //$NON-NLS-1$ //$NON-NLS-2$
			args.add(GitToIspwConstants.GIT_HASH_PARAM_FILE, toHashTempFile.getRemote());			
		}
		else 
		{
			// If the path doesn't contains space, the argument is not
			// properly double quoted, we need to fix it here
			if (!toHash.contains(" ")) //$NON-NLS-1$
			{
				toHash = "\"" + toHash + "\""; //$NON-NLS-1$ //$NON-NLS-2$
			}
			args.add(GitToIspwConstants.GIT_HASH_PARAM, toHash);		
		}
		
		workDir.mkdirs();
		logger.println("Shell script: " + args.toString());


		// invoke the CLI (execute the batch/shell script)
		int exitValue = launcher.launch().cmds(args).envs(envVars).stdout(logger).pwd(workDir).join();

		String osFile = launcher.isUnix()
				? GitToIspwConstants.SCM_DOWNLOADER_CLI_SH
				: GitToIspwConstants.SCM_DOWNLOADER_CLI_BAT;
		
		if (useHashFile && toHashTempFile != null && toHashTempFile.exists() && !RestApiUtils.isIspwDebugMode())
		{
			toHashTempFile.delete();
		}

		if (exitValue != 0)
		{
			throw new AbortException("Call " + osFile + " exited with value = " + exitValue); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			logger.println("Call " + osFile + " exited with value = " + exitValue); //$NON-NLS-1$ //$NON-NLS-2$

			return true;
		}
	}
}
