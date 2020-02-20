package com.compuware.ispw.restapi.action;

import java.io.PrintStream;
import org.apache.commons.lang3.StringUtils;
import com.compuware.ispw.model.rest.MultiTaskInfoResponse;
import com.compuware.ispw.model.rest.TransferTaskInfo;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;
import com.compuware.ispw.restapi.util.ReflectUtils;

/**
 * Transfer task between containers
 * 
 * @author Sam Zhou
 *
 */
public class TransferTaskAction extends AbstractPostAction implements IAction
{

	private static final String contextPath = "/ispw/{srid}/assignments/{assignmentId}/tasks/transfer?level={level}&mname={mname}&mtype={mtype}&taskId={taskId}";

	public TransferTaskAction(PrintStream logger)
	{
		super(logger);
	}

	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody, WebhookToken webhookToken)
	{

		IspwRequestBean bean = new IspwRequestBean();

		IspwContextPathBean ispwContextPathBean = new IspwContextPathBean();
		ispwContextPathBean.setSrid(srid);
		bean.setIspwContextPathBean(ispwContextPathBean);

		String path = contextPath.replace("{srid}", srid);
		TransferTaskInfo transferTaskInfo = new TransferTaskInfo();
		bean.setJsonObject(transferTaskInfo);

		String[] lines = ispwRequestBody.split("\n");
		for (String line : lines)
		{
			line = StringUtils.trimToEmpty(line);

			if (line.startsWith("#"))
			{
				continue;
			}

			int indexOfEqualSign = line.indexOf("=");
			if (indexOfEqualSign != -1)
			{
				String name = StringUtils.trimToEmpty(line.substring(0, indexOfEqualSign));
				String value = StringUtils.trimToEmpty(line.substring(indexOfEqualSign + 1, line.length()));

				if (StringUtils.isNotBlank(value))
				{
					if (name.equals(assignmentId))
					{
						path = path.replace("{" + assignmentId + "}", value);
						ispwContextPathBean.setAssignmentId(value);
					}
					else if (name.equals(level))
					{
						path = path.replace("{" + level + "}", value);
						ispwContextPathBean.setLevel(value);
					}
					else if (name.equals(mname))
					{
						path = path.replace("{" + mname + "}", value);
						ispwContextPathBean.setMname(value);
					}
					else if (name.equals(mtype))
					{
						path = path.replace("{" + mtype + "}", value);
						ispwContextPathBean.setMtype(value);
					}
					else if (name.equals(taskId))
					{
						path = path.replace("{" + taskId + "}", value);
						ispwContextPathBean.setTaskId(value);
					}
					else
					{
						// set the rest of the TransferTaskInfo fields using reflection
						ReflectUtils.reflectSetter(transferTaskInfo, name, value);
					}
				}
			}
		}

		// if level/mname/mtype/taskId are not set, remove them from query string
		path = path.replace("level={level}", StringUtils.EMPTY);
		path = path.replace("&mname={mname}", StringUtils.EMPTY);
		path = path.replace("&mtype={mtype}", StringUtils.EMPTY);
		path = path.replace("&taskId={taskId}", StringUtils.EMPTY);

		bean.setContextPath(path);

		JsonProcessor jsonGenerator = new JsonProcessor();
		String jsonRequest = jsonGenerator.generate(transferTaskInfo);
		bean.setJsonRequest(jsonRequest);
		return bean;
	}

	@Override
	public void startLog(PrintStream logger, IspwContextPathBean ispwContextPathBean, Object jsonObject)
	{
		String message = String.format("Transfering tasks for assignment %s at (level=%s, mname=%s, mtype=%s, taskId=%s)",
				ispwContextPathBean.getAssignmentId(), ispwContextPathBean.getLevel(), ispwContextPathBean.getMname(),
				ispwContextPathBean.getMtype(), ispwContextPathBean.getTaskId());
		logger.println(message);
	}

	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson)
	{
		MultiTaskInfoResponse taskResp = new JsonProcessor().parse(responseJson, MultiTaskInfoResponse.class);
		logger.println("Transfer task response message: " + taskResp.getMessage());

		return taskResp;
	}
	
}
