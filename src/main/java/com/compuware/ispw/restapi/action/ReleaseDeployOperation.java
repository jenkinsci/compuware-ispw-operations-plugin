package com.compuware.ispw.restapi.action;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.compuware.ispw.model.rest.ContainerListInfo;
import com.compuware.ispw.model.rest.DeploySetInfo;
import com.compuware.ispw.model.rest.DeploymentInfo;
import com.compuware.ispw.model.rest.ReleaseDeployOpResponse;
import com.compuware.ispw.model.rest.ReleaseResponse;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReleaseDeployOperation extends SetInfoPostAction {

	private static final String[] defaultProps = new String[] { releaseId, action };
	private static final String contextPath = "/ispw/{srid}/releases/{releaseId}/deployments/{action}";
	/**
	 * Constructor
	 * 
	 * @param logger the jenkins logger
	 */
	public ReleaseDeployOperation(PrintStream logger) {
		super(logger);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.compuware.ispw.restapi.action.IAction#getIspwRequestBean(java.lang.
	 * String, java.lang.String, com.compuware.ispw.restapi.WebhookToken)
	 */
	@Override
	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody, WebhookToken webhookToken) {
		return getIspwRequestBean(srid, ispwRequestBody, webhookToken, contextPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.compuware.ispw.restapi.action.IAction#startLog(java.io.PrintStream,
	 * com.compuware.ispw.restapi.IspwContextPathBean, java.lang.Object)
	 */
	@Override
	public void startLog(PrintStream logger, IspwContextPathBean ispwContextPathBean, Object jsonObject) {
		String msg = String.format("%s action has started for deployments in release %s", ispwContextPathBean.getAction(), ispwContextPathBean.getReleaseId());
		logger.println(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.compuware.ispw.restapi.action.IAction#endLog(java.io.PrintStream,
	 * com.compuware.ispw.restapi.IspwRequestBean, java.lang.String)
	 */
	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson) {
		ReleaseDeployOpResponse releaseResp = new JsonProcessor().parse(responseJson, ReleaseDeployOpResponse.class);
		
	//update end log to show set id request id? n msg?
		logger.println("Action" + ispwRequestBean.getIspwContextPathBean().getAction() + " has been performed on below deployments:");
		//List<DeploySetInfo> outputSets = releaseResp.getSets();
		
		if (releaseResp != null)
		{
			for (DeploySetInfo set : releaseResp.getSets())
			{
				logger.println(" "); //$NON-NLS-1$
				logger.println("id: " + set.getId()); //$NON-NLS-1$
				logger.println("deployments for set : " + set.getId());
				List<DeploymentInfo> deployments = new ArrayList<DeploymentInfo>();
				deployments = set.getDeploymentList();
				for(DeploymentInfo deployment : deployments)
				{
					logger.println(" "); //$NON-NLS-1$
					logger.println("requestId: " + deployment.getRequestId()); //$NON-NLS-1$
					logger.println("url: " + deployment.getUrl());  //$NON-NLS-1$
					
					logger.println("-------------------------------------------------");
				}
				
			}
		}

		logger.println("Completed operation " + ispwRequestBean.getIspwContextPathBean().getAction() + " for deployments for release " + ispwRequestBean.getIspwContextPathBean().getReleaseId());
		
		return releaseResp;
	}

}
