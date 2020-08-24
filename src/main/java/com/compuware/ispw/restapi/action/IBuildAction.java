/**
 * These materials contain confidential information and trade secrets of Compuware Corporation. You shall maintain the materials
 * as confidential and shall not disclose its contents to any third party except as may be required by law or regulation. Use,
 * disclosure, or reproduction is prohibited without the prior express written permission of Compuware Corporation.
 * 
 * All Compuware products listed within the materials are trademarks of Compuware Corporation. All other company or product
 * names are trademarks of their respective owners.
 * 
 * Copyright (c) 2020 Compuware Corporation. All rights reserved.
 */
package com.compuware.ispw.restapi.action;

import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import com.compuware.ispw.restapi.BuildParms;
import com.compuware.ispw.restapi.BuildParmsRequestBody;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.WebhookToken;
import com.compuware.ispw.restapi.util.RestApiUtils;
import hudson.FilePath;

/**
 * Interface for actions that run the ISPW build operation.
 */
public interface IBuildAction extends IAction
{
	public static String BUILD_PARAM_FILE_NAME = "automaticBuildParams.txt"; //$NON-NLS-1$

	/**
	 * Reads the given request body to find out if the parameters of the build should taken form the request body, or if they
	 * should be read from a file. If the "buildautomatically" parameter is specified in the given body, the build parameters
	 * will be read from a file in the given buildDirectory. The given request body will not be changed if the
	 * "buildautomatically" parameter is not specified.
	 * 
	 * @param ispwRequestBody
	 *            the request body entered by the user.
	 * @param buildParmPath
	 *            The file contain the build parms. (should be something like "Jenkins\jobs\job-name\builds\42")
	 * @param logger
	 *            the logger.
	 * @return a String containing the request body that should be used.
	 */
	public default BuildParmsRequestBody getRequestBody(String ispwRequestBody, FilePath buildParmPath, PrintStream logger)
	{
		String buildAutomaticallyRegex = "(?i)(?m)(^(?!#)(.+)?buildautomatically.+true(.+)?$)"; //$NON-NLS-1$
		Pattern buildAutomaticallyPattern = Pattern.compile(buildAutomaticallyRegex,
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		
		BuildParmsRequestBody buildParmsRequestBody = null;
		
		if (ispwRequestBody != null)
		{
			Matcher buildAutomaticallyMatcher = buildAutomaticallyPattern.matcher(ispwRequestBody);
			
			if (buildAutomaticallyMatcher.find())
			{
				boolean exists = false;
				try {
					exists = buildParmPath != null && buildParmPath.exists();
				} catch (IOException | InterruptedException x) {
					x.printStackTrace();
					logger.println("Warn: " + x.getMessage());
				}
				
				if(exists)
				{
					ispwRequestBody = buildAutomaticallyMatcher.replaceAll(StringUtils.EMPTY);

					try
					{
						// if a line of the body contains "buildautomatically" and "true" case insensitive, and the line is not a
						// comment.
						// File parmFile = new File(buildParmPath, BUILD_PARAM_FILE_NAME);
						logger.println("Build parameters will automatically be retrieved from file " + buildParmPath.toURI());
	
						String jsonString = buildParmPath.readToString();
						BuildParms buildParms = null;
						
						if (jsonString != null && !jsonString.isEmpty())
						{
							buildParms = BuildParms.parse(jsonString);
						}
						
						if (buildParms != null)
						{
							// Remove any line that is not a comment and contains application, assignmentid, releaseid, taskid,
							// mname, mtype, or level. These parms will be replaced with parms from the file.
							String linesToReplaceRegex = "(?i)(^(?!#)( +)?(application|assignmentid|releaseid|taskid|mname|mtype|level)(.+)?$)"; //$NON-NLS-1$
							Pattern linesToReplacePattern = Pattern.compile(linesToReplaceRegex,
									Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
							Matcher linesToReplaceMatcher = linesToReplacePattern.matcher(ispwRequestBody);
							ispwRequestBody = linesToReplaceMatcher.replaceAll(StringUtils.EMPTY);
	
							StringBuilder requestBodyBuilder = new StringBuilder();
							if (buildParms.getContainerId() != null)
							{
								requestBodyBuilder.append("assignmentId = " + buildParms.getContainerId());
							}
							if (buildParms.getTaskLevel() != null)
							{
								requestBodyBuilder.append("\nlevel = " + buildParms.getTaskLevel());
							}
							if (buildParms.getReleaseId() != null)
							{
								requestBodyBuilder.append("\nreleaseId = " + buildParms.getReleaseId());
							}
							if (buildParms.getTaskIds() != null && !buildParms.getTaskIds().isEmpty())
							{
								requestBodyBuilder.append("\ntaskId = ");
								for (String taskId : buildParms.getTaskIds())
								{
									requestBodyBuilder.append(taskId + ",");
								}
								requestBodyBuilder.deleteCharAt(requestBodyBuilder.length() - 1); // remove last comma
							}
							requestBodyBuilder.append("\n").append(ispwRequestBody); // the original request body may still contain webhook event
																		// information and runtime configuration
							ispwRequestBody = requestBodyBuilder.toString();
							
							buildParmsRequestBody = new BuildParmsRequestBody(ispwRequestBody, buildParms);
						}
						
						logger.println("Done reading automaticBuildParams.txt.");
					}
					catch (IOException | InterruptedException e)
					{
						//do NOT auto build if has exception
						ispwRequestBody = StringUtils.EMPTY;
						
						e.printStackTrace();
						logger.println("skip auto build, exception happens: " + e.getMessage());
					}
					
				}
				else
				{
					//do NOT auto build if file doesn't exist
					ispwRequestBody = StringUtils.EMPTY;
					
					logger.println("skip auto build, automaticBuildParams.txt doesn't exist.");
				}
			}
		}
		if (RestApiUtils.isIspwDebugMode())
		{
			logger.println("Using requestBody :\n{" + ispwRequestBody + "\n}"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return buildParmsRequestBody;
	}

	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody, WebhookToken webhookToken,
			FilePath buildParmPath);

	public BuildParms getBuildParms();
}
