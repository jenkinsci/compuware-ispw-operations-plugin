/**
 * These materials contain confidential information and trade secrets of Compuware Corporation. You shall maintain the materials
 * as confidential and shall not disclose its contents to any third party except as may be required by law or regulation. Use,
 * disclosure, or reproduction is prohibited without the prior express written permission of Compuware Corporation.
 * 
 * All Compuware products listed within the materials are trademarks of Compuware Corporation. All other company or product
 * names are trademarks of their respective owners.
 * 
 * Copyright (c) 2020 Compuware Corporation. All rights reserved.
 * (c) Copyright 2020 BMC Software, Inc.
 */
package com.compuware.ispw.restapi.action;

import java.io.PrintStream;
import com.compuware.ispw.model.rest.BuildResponse;
import com.compuware.ispw.restapi.BuildParms;
import com.compuware.ispw.restapi.BuildParmsRequestBody;
import com.compuware.ispw.restapi.Constants;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;
import com.compuware.ispw.restapi.util.RestApiUtils;
import hudson.FilePath;

/**
 * 
 */
public class BuildTaskAction extends SetInfoPostAction implements IBuildAction
{
	private static final String[] defaultProps = new String[]{taskId, runtimeConfiguration};

	private static final String contextPath = "/ispw/{srid}/build?taskId={taskId}&application={application}&assignmentId={assignmentId}" //$NON-NLS-1$
			+ "&level={level}&mname={mname}&mtype={mtype}"; //$NON-NLS-1$

	private BuildParms buildParms;
	
	public static String getDefaultProps()
	{
		return RestApiUtils.join(Constants.LINE_SEPARATOR, defaultProps, true);
	}

	public BuildTaskAction(PrintStream logger)
	{
		super(logger);
	}

	@Override
	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody, WebhookToken webhookToken)
	{
		IspwRequestBean bean = getIspwRequestBean(srid, ispwRequestBody, webhookToken, contextPath);

		String contextPath = bean.getContextPath();
		contextPath = contextPath.replaceAll(",", "&taskId=");
		bean.setContextPath(contextPath);

		return bean;
	}

	@SuppressWarnings("nls")
	@Override
	public void startLog(PrintStream logger, IspwContextPathBean ispwContextPathBean, Object jsonObject)
	{
		if (ispwContextPathBean.getTaskId() != null)
		{
			logger.println("ISPW: The build process has started for task " + ispwContextPathBean.getTaskId());
		}
		else
		{
			logger.println("ISPW: The build process has started for task " + ispwContextPathBean.getMname());
		}
	}

	@SuppressWarnings("nls")
	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson)
	{
		BuildResponse buildResp = new JsonProcessor().parse(responseJson, BuildResponse.class);
		if (buildResp.getSetId() == null && !buildResp.getMessage().trim().isEmpty())
		{
			logger.println("ISPW: " + buildResp.getMessage());
		}
		else
		{
			logger.println("ISPW: Set " + buildResp.getSetId() + " - Set created to process the build. ");
		}

		return buildResp;
	}
	
	/* (non-Javadoc)
	 * @see com.compuware.ispw.restapi.action.IBuildAction#getIspwRequestBean(java.lang.String, java.lang.String, com.compuware.ispw.restapi.WebhookToken, java.io.File)
	 */
	@Override
	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody, WebhookToken webhookToken,
			FilePath buildParmPath)
	{
		BuildParmsRequestBody buildParmsRequestBody = getRequestBody(ispwRequestBody, buildParmPath, this.getLogger());
		
		if (buildParmsRequestBody.hasRequestBody())
		{
			buildParms = buildParmsRequestBody.getBuildParms();

			StringBuilder body = new StringBuilder(buildParmsRequestBody.getRequestBody());
			
			System.out.println("Request body: "+body.toString());
			
			return getIspwRequestBean(srid, body.toString(), webhookToken);
		}
		else
		{
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.compuware.ispw.restapi.action.IBuildAction#getBuildParms()
	 */
	@Override
	public BuildParms getBuildParms()
	{
		return buildParms;
	}
}
