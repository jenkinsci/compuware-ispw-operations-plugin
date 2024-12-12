package com.compuware.ispw.restapi.action;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.compuware.ispw.model.rest.TaskInfo;
import com.compuware.ispw.model.rest.TaskListResponse;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;
import com.compuware.ispw.restapi.util.RestApiUtils;

/**
 * Action to get task list in the specified Set
 * 
 * @author Steven Kansa
 *
 */
public class GetSetTaskListAction extends AbstractGetAction {

	private static final String[] defaultProps = new String[] { setId, rtConfig };
	private static final String contextPath = "/ispw/{srid}/sets/{setId}/tasks?rtConfig={rtConfig}";

	public GetSetTaskListAction(PrintStream logger) {
		super(logger);
	}
	
	@Override
	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody,
			WebhookToken webhookToken) {
		List<String> pathTokens = Arrays.asList(defaultProps);
		return super.getIspwRequestBean(srid, ispwRequestBody, contextPath, pathTokens);
	}

	@Override
	public void startLog(PrintStream logger, IspwContextPathBean ispwContextPathBean, Object jsonObject)
	{
		logger.println("Getting task list in Set "+ispwContextPathBean.getSetId());
	}

	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson)
	{
		String fixedResponseJson = RestApiUtils.fixCesTaskListResponseJson(responseJson);
		TaskListResponse listResponse = new JsonProcessor().parse(fixedResponseJson, TaskListResponse.class);
		if (!listResponse.getTasks().isEmpty() && StringUtils.isNotEmpty(listResponse.getTasks().get(0).getSubAppl()))
		{
			logger.println("TaskId, Module, Type, UserId, Version, Status, Application/SubAppl/Stream/Level, Release");
			for (TaskInfo taskInfo : listResponse.getTasks())
			{
				logger.println(" " + taskInfo.getTaskId() + ", " + taskInfo.getModuleName() + ", " + taskInfo.getModuleType()
						+ ", " + taskInfo.getUserId() + ", " + taskInfo.getVersion() + ", " + taskInfo.getStatus() + ", "
						+ taskInfo.getApplication() + "/" + taskInfo.getSubAppl() + "/" + taskInfo.getStream() + "/"
						+ taskInfo.getLevel() + ", " + taskInfo.getRelease());
			}
		}
		else
		{
			logger.println("TaskId, Module, Type, UserId, Version, Status, Application/Stream/Level, Release");
			for (TaskInfo taskInfo : listResponse.getTasks())
			{
				logger.println(" " + taskInfo.getTaskId() + ", " + taskInfo.getModuleName() + ", " + taskInfo.getModuleType()
						+ ", " + taskInfo.getUserId() + ", " + taskInfo.getVersion() + ", " + taskInfo.getStatus() + ", "
						+ taskInfo.getApplication() + "/" + taskInfo.getStream() + "/" + taskInfo.getLevel() + ", "
						+ taskInfo.getRelease());
			}
		}
		return listResponse;
	}

}
