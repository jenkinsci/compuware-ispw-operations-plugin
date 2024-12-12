package com.compuware.ispw.restapi.action;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.compuware.ispw.model.rest.SetInfoResponse;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;

/**
 * Action to get a set status
 * 
 * @author Sam Zhou
 *
 */
public class GetSetInfoAction extends AbstractGetAction {

	private static final String[] defaultProps = new String[] { setId, level, rtConfig };
	private static final String contextPath = "/ispw/{srid}/sets/{setId}?level={level}&rtConfig={rtConfig}";
	
	public GetSetInfoAction(PrintStream logger) {
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
		logger.println("Getting info on Set "+ispwContextPathBean.getSetId());
	}

	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson)
	{
		SetInfoResponse setInfoResp = new JsonProcessor().parse(responseJson, SetInfoResponse.class);
		if (StringUtils.isNotBlank(responseJson))
		{
			logger.println("SetId, State, Owner, Application/subAppl/Stream, Start Date/Start Time");
			logger.println(" " + setInfoResp.getSetid() + ", " + setInfoResp.getState() + ", " + setInfoResp.getOwner() + ", "
					+ setInfoResp.getApplicationId() + "/" + setInfoResp.getSubAppl() + "/" + setInfoResp.getStreamName() + ", "
					+ setInfoResp.getStartDate() + "/" + setInfoResp.getStartTime());
		}
		else
		{
			logger.println("SetId, State, Owner, Application/Stream, Start Date/Start Time");
			logger.println(" " + setInfoResp.getSetid() + ", " + setInfoResp.getState() + ", " + setInfoResp.getOwner() + ", "
					+ setInfoResp.getApplicationId() + "/" + setInfoResp.getStreamName() + ", "
					+ setInfoResp.getStartDate() + "/" + setInfoResp.getStartTime());
		}
		
		return setInfoResp;
	}

}
