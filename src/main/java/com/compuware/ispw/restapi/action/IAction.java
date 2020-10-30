/**
 * These materials contain confidential information and trade secrets of Compuware Corporation. You shall maintain the materials
 * as confidential and shall not disclose its contents to any third party except as may be required by law or regulation. Use,
 * disclosure, or reproduction is prohibited without the prior express written permission of Compuware Corporation.
 * 
 * All Compuware products listed within the materials are trademarks of Compuware Corporation. All other company or product
 * names are trademarks of their respective owners.
 * 
 * Copyright (c) 2019 Compuware Corporation. All rights reserved.
 * (c) Copyright 2020 BMC Software, Inc.
 */
package com.compuware.ispw.restapi.action;

import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.compuware.ispw.restapi.BuildParms;
import com.compuware.ispw.restapi.HttpMode;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.WebhookToken;
import com.compuware.ispw.restapi.util.RestApiUtils;

import hudson.FilePath;

/**
 * Common interface for all actions
 * 
 * @author Sam Zhou
 *
 */
@SuppressWarnings("nls")
public interface IAction {

	public static final String application = "application";
	public static final String assignmentId = "assignmentId";
	public static final String assignmentPrefix = "assignmentPrefix";
	public static final String autoDeploy = "autoDeploy";
	public static final String changeType = "changeType";
	public static final String containerId = "containerId";
	public static final String containerType = "containerType";
	public static final String credentials = "credentials";
	public static final String currentLevel = "currentLevel";
	public static final String defaultPath = "defaultPath";
	public static final String description = "description";
	public static final String dpenvlst = "dpenvlst";
	public static final String endDate = "endDate";
	public static final String environment = "environment";
	public static final String eventsBody = "events.body";
	public static final String eventsCredentials = "events.credentials";
	public static final String eventsHttpHeaders = "events.httpHeaders";
	public static final String eventsMethod = "events.method";
	public static final String eventsName = "events.name";
	public static final String eventsUrl = "events.url";
	public static final String executionStatus = "executionStatus";
	public static final String group = "group";
	public static final String historical = "historical";
	public static final String httpHeaders = "httpHeaders";
	public static final String includeClosedContainers = "includeClosedContainers";
	public static final String inProgress = "inProgress";
	public static final String lastUpdatedBy = "lastUpdatedBy";
	public static final String level = "level";
	public static final String mname = "mname";
	public static final String moduleName = "moduleName";
	public static final String moduleType = "moduleType";
	public static final String mtype = "mtype";
	public static final String name = "name";
	public static final String operation = "operation";
	public static final String owner = "owner";
	public static final String path = "path";
	public static final String production = "production";
	public static final String referenceNumber = "referenceNumber";
	public static final String refNumber = "refNumber";
	public static final String releaseId = "releaseId";
	public static final String releasePrefix = "releasePrefix";
	public static final String requestId = "requestId";
	public static final String runtimeConfiguration = "runtimeConfiguration";
	public static final String setId = "setId";
	public static final String startDate = "startDate";
	public static final String startingLevel = "startingLevel";
	public static final String stream = "stream";
	public static final String system = "system";
	public static final String tag = "tag";
	public static final String taskId = "taskId";
	public static final String userId = "userId";
	public static final String userTag = "userTag";
	public static final String action = "action";
	public static final String checkoutFromLevel = "checkoutFromLevel";
	public static final String checkout = "checkout";
	public static final String taskName = "taskName"; // TODO: need to update to mname/mtype to be consistent on CES
														// side
	public static final String type = "type";

	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody, WebhookToken webhookToken);

	public PrintStream getLogger();

	public void startLog(PrintStream logger, IspwContextPathBean ispwContextPathBean, Object jsonObject);

	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson);

	public HttpMode getHttpMode();

	/**
	 * This method should be overridden to add "automatic" support for an action
	 * 
	 * @param ispwRequestBody
	 *            - the request body entered by the user.
	 * @param pathToParmFile
	 *            - The file that contains the build parms. (should be something
	 *            like "Jenkins\workspace\job-name\")
	 * @param logger
	 *            - the logger.
	 * @return - a String containing the request body that should be used.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public default String preprocess(String ispwRequestBody, FilePath pathToParmFile, PrintStream logger)
			throws IOException, InterruptedException {
		return ispwRequestBody;
	}

	/**
	 * Reads the given request body to find out if the parameters of the build
	 * should taken from the request body, or if they should be read from a file. If
	 * the automaticRegex parameter is specified in the given body, the REST API
	 * parameters will be read from a file in the given pathToParmFile. The given
	 * request body will not be changed if the automaticRegex parameter is not
	 * specified.
	 * 
	 * @param automaticRegex
	 *            the regex used to match on the relevant "automatic" string for
	 *            this action. <br>
	 * 			(for example
	 *            <code>(?i)(?m)(^(?!#)(.+)?deployautomatically.+true(.+)?$)</code>)
	 * @param ispwRequestBody
	 *            the request body entered by the user.
	 * @param pathToParmFile
	 *            The file that contains the build parms. (should be something like
	 *            "Jenkins\workspace\job-name\")
	 * @param logger
	 *            the logger.
	 * @param operation
	 *            the operation to preprocess. A custom string can be passed in, or <b>utilizing the
	 *            {@link com.compuware.ispw.restapi.util.Operation#getDescription()} is preferred</b>
	 * @param pastTenseOp
	 *            the past tense of the operation used for logging. A custom string can be passed in, or <b>utilizing the
	 *            {@link com.compuware.ispw.restapi.util.Operation#getPastTenseDescription()} is preferred</b>
	 * @return a String containing the request body that should be used.
	 * @throws IOException
	 *             IO exception
	 * @throws InterruptedException
	 *             interrupted exception
	 */
	default String preprocess(String automaticRegex, String ispwRequestBody, FilePath pathToParmFile,
			PrintStream logger, String operation, String pastTenseOp) throws IOException, InterruptedException {
		Pattern runAutomaticallyPattern = Pattern.compile(automaticRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		if (ispwRequestBody != null) {
			Matcher runAutomaticallyMatcher = runAutomaticallyPattern.matcher(ispwRequestBody);

			if (runAutomaticallyMatcher.find()) {
				boolean exists = false;
				try {
					exists = pathToParmFile != null && pathToParmFile.exists();
				} catch (IOException | InterruptedException x) {
					x.printStackTrace();
					logger.println("Warn: " + x.getMessage());
				}

				if (exists) {
					ispwRequestBody = runAutomaticallyMatcher.replaceAll(StringUtils.EMPTY);

					try {
						// if a line of the body contains the regex and "true" case-
						// insensitive, and the line is not a
						// comment.
						// File parmFile = new File(buildParmPath, BUILD_PARAM_FILE_NAME);
						logger.println(
								operation + " parameters will automatically be retrieved from file " + pathToParmFile.toURI());

						String jsonString = pathToParmFile.readToString();
						BuildParms buildParms = null;

						if (jsonString != null && !jsonString.isEmpty()) {
							buildParms = BuildParms.parse(jsonString);
						}

						if (buildParms != null) {
							ispwRequestBody = getRequestBodyUsingAutomaticParms(ispwRequestBody, buildParms);
						}

						logger.println("Done reading automaticBuildParams.txt.");
					} catch (IOException | InterruptedException e) {
						// do NOT auto build if has exception
						ispwRequestBody = StringUtils.EMPTY;

						e.printStackTrace();
						logger.println(
								"The tasks could not be " + pastTenseOp + " automatically because the following error occurred: "
										+ e.getMessage());
						throw e;
					}

				} else {
					// do NOT auto build if file doesn't exist
					ispwRequestBody = StringUtils.EMPTY;
					logger.println(
							"The tasks could not be " + pastTenseOp + " automatically because the automaticBuildParams.txt file does not exist.");
				}
			}
		}
		if (RestApiUtils.isIspwDebugMode()) {
			logger.println("Using requestBody :\n{" + ispwRequestBody + "\n}"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return ispwRequestBody;
	}

	/**
	 * For the case where "buildautomatically" is specified, this method replaces
	 * any parameters specified in the request body with the parameters in the
	 * BuildParms object. Any information that does not need to be replaced (i.e.
	 * comments, event data, runtimeConfiguration...) will still be part of the
	 * returned request body.
	 * 
	 * @param inputRequestBody
	 *            the input request body that the user provided
	 * @param buildParms
	 *            the build parms to use instead of the given request body
	 * @return a String request body to use instead of what the user specified. This
	 *         will never be null.
	 */
	default String getRequestBodyUsingAutomaticParms(String inputRequestBody, BuildParms buildParms) {
		String ispwRequestBody = inputRequestBody;
		// Remove any line that is not a comment and contains application, assignmentid,
		// releaseid, taskid,
		// mname, mtype, or level. These parms will be replaced with parms from the
		// file.
		String linesToReplaceRegex = "(?i)(^(?!#)( +)?(application|assignmentid|releaseid|taskid|mname|mtype|level)(.+)?$)"; //$NON-NLS-1$
		Pattern linesToReplacePattern = Pattern.compile(linesToReplaceRegex,
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher linesToReplaceMatcher = linesToReplacePattern.matcher(ispwRequestBody);
		ispwRequestBody = linesToReplaceMatcher.replaceAll(StringUtils.EMPTY);

		StringBuilder requestBodyBuilder = new StringBuilder();
		if (buildParms.getContainerId() != null) {
			requestBodyBuilder.append("assignmentId = " + buildParms.getContainerId());
		}
		if (buildParms.getTaskLevel() != null) {
			requestBodyBuilder.append("\nlevel = " + buildParms.getTaskLevel());
		}
		if (buildParms.getReleaseId() != null) {
			requestBodyBuilder.append("\nreleaseId = " + buildParms.getReleaseId());
		}
		if (buildParms.getTaskIds() != null && !buildParms.getTaskIds().isEmpty()) {
			requestBodyBuilder.append("\ntaskId = ");
			for (String taskId : buildParms.getTaskIds()) {
				requestBodyBuilder.append(taskId + ",");
			}
			requestBodyBuilder.deleteCharAt(requestBodyBuilder.length() - 1); // remove last comma
		}
		requestBodyBuilder.append("\n").append(ispwRequestBody); // the original request body may still contain webhook
																	// event
																	// information and runtime configuration
		ispwRequestBody = requestBodyBuilder.toString();
		return ispwRequestBody;
	}

	public default void postprocess() {

	}
}
