package com.compuware.ispw.restapi.action;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.compuware.ispw.model.rest.ContainerListInfo;
import com.compuware.ispw.model.rest.ContainerListResponse;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;
import com.compuware.ispw.restapi.util.RestApiUtils;

/**
 * Action to get the container list information
 *
 */
public class GetContainerListAction extends AbstractGetAction {

	private static final String[] defaultProps = new String[] { userId, containerId, containerType, application,subAppl, owner, description, refNumber, releaseId, stream, path, tag, includeClosedContainers, rtConfig };
	private static final String contextPath = "/ispw/{srid}/containers/list?userId={userId}&containerId={containerId}&containerType={containerType}&application={application}&subAppl={subAppl}&owner={owner}&description={description}&refNumber={refNumber}&releaseId={releaseId}&stream={stream}&path={path}&tag={tag}&includeClosedContainers={includeClosedContainers}&rtConfig={rtConfig}"; //$NON-NLS-1$

	public GetContainerListAction(PrintStream logger) {
		super(logger);
	}
	
	@Override
	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody,
			WebhookToken webhookToken) {

		List<String> pathTokens = Arrays.asList(defaultProps);
		IspwRequestBean ispwRequestBean =  super.getIspwRequestBean(srid, ispwRequestBody, contextPath, pathTokens);
		String path = ispwRequestBean.getContextPath();
		
		//if parameters not set, remove them from query string
		path = path.replace("userId={userId}", StringUtils.EMPTY); //$NON-NLS-1$
		path = path.replace("containerId={containerId}", StringUtils.EMPTY); //$NON-NLS-1$
		path = path.replace("containerType={containerType}", StringUtils.EMPTY); //$NON-NLS-1$
		path = path.replace("application={application}", StringUtils.EMPTY); //$NON-NLS-1$
		path = path.replace("subAppl={subAppl}", StringUtils.EMPTY); //$NON-NLS-1$
		path = path.replace("owner={owner}", StringUtils.EMPTY); //$NON-NLS-1$
		path = path.replace("description={description}", StringUtils.EMPTY); //$NON-NLS-1$
		path = path.replace("refNumber={refNumber}", StringUtils.EMPTY); //$NON-NLS-1$
		path = path.replace("releaseId={releaseId}", StringUtils.EMPTY); //$NON-NLS-1$
		path = path.replace("stream={stream}", StringUtils.EMPTY); //$NON-NLS-1$
		path = path.replace("path={path}", StringUtils.EMPTY); //$NON-NLS-1$
		path = path.replace("tag={tag}", StringUtils.EMPTY); //$NON-NLS-1$
		path = path.replace("includeClosedContainers={includeClosedContainers}", StringUtils.EMPTY); //$NON-NLS-1$

		path = path.replaceAll("[&]+", "&");
		if (path.endsWith("&")) {
			path = path.substring(0, path.length() - 1);
		}
		
		ispwRequestBean.setContextPath(path);
		
		return ispwRequestBean;
	}

	@Override
	public void startLog(PrintStream logger, IspwContextPathBean ispwContextPathBean, Object jsonObject)
	{
		logger.println("Get the container list information"); //$NON-NLS-1$
	}

	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson)
	{
		String fixedResponseJson = RestApiUtils.fixCesContainerListResponseJson(responseJson);
		ContainerListResponse listResponse = new JsonProcessor().parse(fixedResponseJson, ContainerListResponse.class);
		
		if (listResponse != null)
		{
			for (ContainerListInfo containerListInfo : listResponse.getContainerList())
			{
				logger.println(" "); //$NON-NLS-1$
				logger.println("Application: " + containerListInfo.getApplication()); //$NON-NLS-1$
				if (StringUtils.isNotEmpty(containerListInfo.getSubAppl()))
				{
					logger.println("SubAppl: " + containerListInfo.getSubAppl()); //$NON-NLS-1$
				}
				logger.println("Container ID: " + containerListInfo.getContainerId()); //$NON-NLS-1$
				logger.println("Container type: " + containerListInfo.getContainerType()); //$NON-NLS-1$
				logger.println("Description: " + containerListInfo.getDescription()); //$NON-NLS-1$
				logger.println("Owner: " + containerListInfo.getOwner()); //$NON-NLS-1$
				logger.println("Path: " + containerListInfo.getPath()); //$NON-NLS-1$
				logger.println("Reference number: " + containerListInfo.getWorkRefNumber()); //$NON-NLS-1$
				logger.println("Release ID: " + containerListInfo.getReleaseId()); //$NON-NLS-1$ 
				logger.println("Stream: " + containerListInfo.getStream()); //$NON-NLS-1$
				logger.println("Tag: " + containerListInfo.getUserTag()); //$NON-NLS-1$
			}
		}

		return listResponse;
	}

}
