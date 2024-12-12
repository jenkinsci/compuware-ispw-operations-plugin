package com.compuware.ispw.restapi.action;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.compuware.ispw.model.rest.ReleaseInfo;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;

/**
 * Action to get release information
 * 
 * @author Sam Zhou
 *
 */
public class GetReleaseInfoAction extends AbstractGetAction {

	private static final String[] defaultProps = new String[] { releaseId , rtConfig};
	private static final String contextPath = "/ispw/{srid}/releases/{releaseId}?rtConfig={rtConfig}";

	public GetReleaseInfoAction(PrintStream logger) {
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
		logger.println("Getting info on Release "+ispwContextPathBean.getReleaseId());
	}

	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson)
	{
		ReleaseInfo releaseInfo = new JsonProcessor().parse(responseJson, ReleaseInfo.class);
		if (StringUtils.isNotBlank(releaseInfo.getSubAppl()))
		{
			logger.println("Stream/Application/SubAppl: " + releaseInfo.getStream() + "/" + releaseInfo.getApplication() + "/"
					+ releaseInfo.getSubAppl());
		}
		else
		{
			logger.println("Stream/Application: " + releaseInfo.getStream() + "/" + releaseInfo.getApplication());
		}
		logger.println("Release: " + releaseInfo.getReleaseId() + " - "
				+ releaseInfo.getDescription());
		logger.println("Owner: " + releaseInfo.getOwner());
		logger.println("Work reference #: " + releaseInfo.getWorkRefNumber());
		logger.println("Release prefix: " + releaseInfo.getReleasePrefix());
		logger.println("User tag: " + releaseInfo.getUserTag());
		
		return releaseInfo;

	}

}
