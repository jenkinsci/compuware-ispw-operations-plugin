package com.compuware.ispw.restapi.action;

import java.io.IOException;
import java.io.PrintStream;

import com.compuware.ispw.model.rest.TaskResponse;
import com.compuware.ispw.restapi.Constants;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;
import com.compuware.ispw.restapi.util.RestApiUtils;

import hudson.FilePath;

public class DeployTaskAction extends SetInfoPostAction {

	private static final String[] defaultProps =
			new String[] { assignmentId, level, runtimeConfiguration };

	private static final String contextPath =
			"/ispw/{srid}/assignments/{assignmentId}/taskIds/deploy?taskId={taskId}&level={level}&mname={mname}&mtype={mtype}";

	public static String getDefaultProps() {
		return RestApiUtils.join(Constants.LINE_SEPARATOR, defaultProps, true);
	}

	public DeployTaskAction(PrintStream logger) {
		super(logger);
	}
	
	@Override
	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody,
			WebhookToken webhookToken) {
		return getIspwRequestBean(srid, ispwRequestBody, webhookToken, contextPath);
	}

	@Override
	public void startLog(PrintStream logger, IspwContextPathBean ispwContextPathBean, Object jsonObject)
	{
		logger.println("ISPW: Deploying Assignment " + ispwContextPathBean.getAssignmentId()
		+ " at level " + ispwContextPathBean.getLevel());
	}

	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson)
	{
		TaskResponse taskResp = new JsonProcessor().parse(responseJson, TaskResponse.class);
		logger.println("ISPW: Set "+taskResp.getSetId()+" - created to deploy Assignment "+ispwRequestBean.getIspwContextPathBean().getAssignmentId());
		
		return taskResp;

	}
	
	@Override
	public String preprocess(String ispwRequestBody, FilePath pathToParmFile, PrintStream logger) throws IOException, InterruptedException
	{
		String automaticRegex = "(?i)(?m)(^(?!#)(.+)?deployautomatically.+true(.+)?$)";
		return super.preprocess(automaticRegex, ispwRequestBody, pathToParmFile, logger);
	}

}
