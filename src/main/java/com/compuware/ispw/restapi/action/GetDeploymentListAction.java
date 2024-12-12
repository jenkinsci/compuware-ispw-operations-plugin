/**
 * THESE MATERIALS CONTAIN CONFIDENTIAL INFORMATION AND TRADE SECRETS OF BMC SOFTWARE, INC. YOU SHALL MAINTAIN THE MATERIALS AS
 * CONFIDENTIAL AND SHALL NOT DISCLOSE ITS CONTENTS TO ANY THIRD PARTY EXCEPT AS MAY BE REQUIRED BY LAW OR REGULATION. USE,
 * DISCLOSURE, OR REPRODUCTION IS PROHIBITED WITHOUT THE PRIOR EXPRESS WRITTEN PERMISSION OF BMC SOFTWARE, INC.
 *
 * ALL BMC SOFTWARE PRODUCTS LISTED WITHIN THE MATERIALS ARE TRADEMARKS OF BMC SOFTWARE, INC. ALL OTHER COMPANY PRODUCT NAMES
 * ARE TRADEMARKS OF THEIR RESPECTIVE OWNERS.
 *
 * (c) Copyright 2023 BMC Software, Inc.
 */
package com.compuware.ispw.restapi.action;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.compuware.ispw.model.rest.DeploymentInfo;
import com.compuware.ispw.model.rest.DeploymentInfoResponse;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;
import com.compuware.ispw.restapi.util.RestApiUtils;

/**
 * Action to get an ISPW deployment list.
 *
 */
public class GetDeploymentListAction extends AbstractGetAction
{

	private static final String[] defaultProps = new String[]{todaysDate, priorWeek, startDate, endDate, requestId, setId,
			environment, status, rtConfig};
	private static final String contextPath = "/ispw/{srid}/deployments?todaysDate={todaysDate}&priorWeek={priorWeek}&startDate={startDate}&endDate={endDate}&requestId={requestId}&setId={setId}&environment={environment}&status={status}&rtConfig={rtConfig}"; //$NON-NLS-1$

	public GetDeploymentListAction(PrintStream logger)
	{
		super(logger);
	}

	@Override
	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody, WebhookToken webhookToken)
	{

		List<String> pathTokens = Arrays.asList(defaultProps);
		IspwRequestBean ispwRequestBean = super.getIspwRequestBean(srid, ispwRequestBody, contextPath, pathTokens);
		String path = ispwRequestBean.getContextPath();

		// if parameters not set, remove them from query string

		for (String prop : defaultProps)
		{
			String searchVal = prop + "={" + prop + "}"; //$NON-NLS-1$ //$NON-NLS-2$
			path = path.replace(searchVal, StringUtils.EMPTY);
		}

		path = path.replaceAll("[&]+", "&"); //$NON-NLS-1$ //$NON-NLS-2$
		if (path.endsWith("&")) //$NON-NLS-1$
		{
			path = path.substring(0, path.length() - 1);
		}

		ispwRequestBean.setContextPath(path);

		return ispwRequestBean;
	}

	@Override
	public void startLog(PrintStream logger, IspwContextPathBean ispwContextPathBean, Object jsonObject)
	{
		logger.println("Get the deployment list information"); //$NON-NLS-1$
	}

	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson)
	{
		String fixedResponseJson = RestApiUtils.fixWorkListResponseJson(responseJson);
		DeploymentInfoResponse listResponse = new JsonProcessor().parse(fixedResponseJson, DeploymentInfoResponse.class);

		if (listResponse != null)
		{
			for (DeploymentInfo deploymentInfo : listResponse.getDeployments())
			{
				logger.println(" "); //$NON-NLS-1$
				logger.println("Request ID: " + deploymentInfo.getRequestId()); //$NON-NLS-1$
				logger.println("Set: " + deploymentInfo.getSetId()); //$NON-NLS-1$
				logger.println("Environment: " + deploymentInfo.getEnvironment()); //$NON-NLS-1$
				logger.println("Status: " + deploymentInfo.getStatus()); //$NON-NLS-1$
				logger.println("Description: " + deploymentInfo.getDescription()); //$NON-NLS-1$
				logger.println("Create Date/Time: " + deploymentInfo.getCreateDateTime()); //$NON-NLS-1$
			}
		}
		
		return listResponse;
	}

}
