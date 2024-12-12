package com.compuware.ispw.restapi.action;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import com.compuware.ispw.model.rest.TaskListingResponse;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;

/**
 * Action to get generate listing in the specified release task
 * 
 * @author Sam Zhou
 *
 */
public class GetReleaseTaskGenerateListingAction extends AbstractGetAction {

	private static final String[] defaultProps = new String[] { releaseId, taskId, rtConfig };
	private static final String contextPath = "/ispw/{srid}/releases/{releaseId}/tasks/{taskId}/listing?rtConfig={rtConfig}";

	public GetReleaseTaskGenerateListingAction(PrintStream logger) {
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
		logger.println("Getting Release task generate listing of task "
				+ ispwContextPathBean.getTaskId() + " in release "
				+ ispwContextPathBean.getReleaseId());
	}

	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson)
	{
		TaskListingResponse listingResp = new JsonProcessor().parse(responseJson, TaskListingResponse.class);
		logger.println("Listing: "+listingResp.getListing());
		
		return listingResp;
	}

}
