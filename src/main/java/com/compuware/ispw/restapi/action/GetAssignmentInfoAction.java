package com.compuware.ispw.restapi.action;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.compuware.ispw.model.rest.AssignmentInfo;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;

/**
 * Action to get the assignment information
 * 
 * @author Sam Zhou
 *
 */
public class GetAssignmentInfoAction extends AbstractGetAction {

	private static final String[] defaultProps = new String[] { assignmentId, rtConfig };
	private static final String contextPath = "/ispw/{srid}/assignments/{assignmentId}?rtConfig={rtConfig}";

	public GetAssignmentInfoAction(PrintStream logger) {
		super(logger);
	}
	
	@Override
	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody,
			WebhookToken webhookToken) {

		List<String> pathTokens = Arrays.asList(defaultProps);
		System.out.println("ispwRequestBody : " + ispwRequestBody);
		return super.getIspwRequestBean(srid, ispwRequestBody, contextPath, pathTokens);
	}

	@Override
	public void startLog(PrintStream logger, IspwContextPathBean ispwContextPathBean, Object jsonObject)
	{
		logger.println("Getting info on Assignment "+ispwContextPathBean.getAssignmentId());
	}

	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson)
	{
		AssignmentInfo assignment = new JsonProcessor().parse(responseJson, AssignmentInfo.class);
		logger.println("Stream/Application/Default path: " + assignment.getStream() + "/"
				+ assignment.getApplication()+ "/" + assignment.getDefaultPath());
		if (StringUtils.isNotBlank(assignment.getSubAppl()))
		{
			logger.println("SubAppl: " + assignment.getSubAppl());
		}
		logger.println("Assignment: " + assignment.getProjectNumber() + " - "
				+ assignment.getDescription());
		logger.println("Owner: " + assignment.getOwner());
		logger.println("Rreference number: " + assignment.getRefNumber());
		logger.println("Release: " + assignment.getRelease());
		logger.println("User tag: " + assignment.getUserTag());
		
		return assignment;
	}

}
