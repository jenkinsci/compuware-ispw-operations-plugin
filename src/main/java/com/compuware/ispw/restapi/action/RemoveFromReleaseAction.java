package com.compuware.ispw.restapi.action;

import java.io.PrintStream;
import org.apache.commons.lang3.StringUtils;
import com.compuware.ispw.model.rest.MultiTaskInfoResponse;
import com.compuware.ispw.restapi.HttpMode;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;

/**
 * Action to remove one or more tasks from an ISPW release.
 */
@SuppressWarnings("nls")
public class RemoveFromReleaseAction extends AbstractPostAction implements IAction
{

	private PrintStream logger;

	private static final String CONTEXT_PATH = "/ispw/{srid}/releases/{releaseId}/tasks/remove?level={level}&mname={mname}&mtype={mtype}&taskId={taskId}";

	public RemoveFromReleaseAction(PrintStream logger)
	{
		super(logger);
	}

	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody, WebhookToken webhookToken)
	{

		IspwRequestBean bean = new IspwRequestBean();

		IspwContextPathBean ispwContextPathBean = new IspwContextPathBean();
		ispwContextPathBean.setSrid(srid);
		bean.setIspwContextPathBean(ispwContextPathBean);

		String path = CONTEXT_PATH.replace("{srid}", srid);

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
					if (name.equals(releaseId))
					{
						path = path.replace("{" + releaseId + "}", value);
						ispwContextPathBean.setReleaseId(value);
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
				}
			}
		}

		// if level/mname/mtype/taskId are not set, remove them from query string
		path = path.replace("level={level}", StringUtils.EMPTY);
		path = path.replace("&mname={mname}", StringUtils.EMPTY);
		path = path.replace("&mtype={mtype}", StringUtils.EMPTY);
		path = path.replace("&taskId={taskId}", StringUtils.EMPTY);

		bean.setContextPath(path);

		return bean;
	}
	
	@Override
	public void startLog(PrintStream logger, IspwContextPathBean ispwContextPathBean, Object jsonObject)
	{
		String message = String.format("Removing tasks for release %s at (level=%s, mname=%s, mtype=%s, taskId=%s)",
				ispwContextPathBean.getReleaseId(), ispwContextPathBean.getLevel(), ispwContextPathBean.getMname(),
				ispwContextPathBean.getMtype(), ispwContextPathBean.getTaskId());
		logger.println(message);
	}

	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson)
	{
		MultiTaskInfoResponse taskResp = new JsonProcessor().parse(responseJson, MultiTaskInfoResponse.class);
		logger.println("Remove from release response message: " + taskResp.getMessage());

		return taskResp;
	}

}
