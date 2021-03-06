package com.compuware.ispw.restapi.action;

import java.io.IOException;
import java.io.PrintStream;
import com.compuware.ispw.model.rest.TaskResponse;
import com.compuware.ispw.restapi.Constants;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;
import com.compuware.ispw.restapi.util.Operation;
import com.compuware.ispw.restapi.util.RestApiUtils;
import hudson.FilePath;

/**
 * Action to generate tasks in specified assignment
 * 
 * @author Sam Zhou
 *
 */
public class GenerateTasksInAssignmentAction extends SetInfoPostAction {

	private static final String[] defaultProps =
			new String[] { assignmentId, level };

	private static final String contextPath =
			"/ispw/{srid}/assignments/{assignmentId}/tasks/generate?level={level}&mname={mname}&mtype={mtype}";

	public static String getDefaultProps() {
		return RestApiUtils.join(Constants.LINE_SEPARATOR, defaultProps, true);
	}

	public GenerateTasksInAssignmentAction(PrintStream logger) {
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
		if (ispwContextPathBean.getAssignmentId() != null)
		{
			logger.println("ISPW: The generate process has started for assignment "
					+ ispwContextPathBean.getAssignmentId() + " at level "
					+ ispwContextPathBean.getLevel());			
		}
	}

	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson)
	{
		TaskResponse taskResponse = new JsonProcessor().parse(responseJson, TaskResponse.class);
		if (taskResponse.getSetId() == null && !taskResponse.getMessage().trim().isEmpty())
		{
			logger.println("ISPW: " + taskResponse.getMessage());
		}
		else
		{
			logger.println("ISPW: Set " + taskResponse.getSetId() + " - created to generate Assignment "
					+ ispwRequestBean.getIspwContextPathBean().getAssignmentId());
		}

		return taskResponse;
	}

	@Override
	public String preprocess(String ispwRequestBody, FilePath pathToParmFile, PrintStream logger) throws IOException, InterruptedException
	{
		String automaticRegex = "(?i)(?m)(^(?!#)(.+)?generateautomatically.+true(.+)?$)";
		return super.preprocess(automaticRegex, ispwRequestBody, pathToParmFile, logger, getIspwOperation().getDescription(),
				getIspwOperation().getPastTenseDescription());
	}

	@Override
	public Operation getIspwOperation()
	{
		return Operation.GENERATE;
	}
}
