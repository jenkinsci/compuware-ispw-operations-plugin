package com.compuware.ispw.restapi.action;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.compuware.ispw.model.rest.WorkListInfo;
import com.compuware.ispw.model.rest.WorkListResponse;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;
import com.compuware.ispw.restapi.util.RestApiUtils;

/**
 * Action to get an ISPW work list.
 *
 */
public class GetWorkListAction extends AbstractGetAction
{

	private static final String[] defaultProps = new String[]{inProgress, production, historical, startDate, endDate, type,
			name, operation, level, environment, application, subAppl, stream, lastUpdatedBy, owner, releaseId, refNumber, group, rtConfig};
	private static final String contextPath = "/ispw/{srid}/worklist?inProgress={inProgress}&production={production}&historical={historical}&startDate={startDate}&endDate={endDate}&type={type}&name={name}&operation={operation}&level={level}&environment={environment}&application={application}&subAppl={subAppl}&stream={stream}&lastUpdatedBy={lastUpdatedBy}&owner={owner}&releaseId={releaseId}&refNumber={refNumber}&group={group}&rtConfig={rtConfig}"; //$NON-NLS-1$

	public GetWorkListAction(PrintStream logger)
	{
		super(logger);
	}

	@Override
	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody, WebhookToken webhookToken)
	{

		List<String> pathTokens = Arrays.asList(defaultProps);
		IspwRequestBean ispwRequestBean = super.getIspwRequestBean(srid, ispwRequestBody, contextPath, pathTokens);
		String path = ispwRequestBean.getContextPath();

		// if parameters not set, remove them from query string

		for (String prop : defaultProps)
		{
			String searchVal = prop + "={" + prop + "}"; //$NON-NLS-1$ //$NON-NLS-2$
			path = path.replace(searchVal, StringUtils.EMPTY);
		}

		path = path.replaceAll("[&]+", "&"); //$NON-NLS-1$ //$NON-NLS-2$
		if (path.endsWith("&")) //$NON-NLS-1$
		{
			path = path.substring(0, path.length() - 1);
		}

		ispwRequestBean.setContextPath(path);

		return ispwRequestBean;
	}

	@Override
	public void startLog(PrintStream logger, IspwContextPathBean ispwContextPathBean, Object jsonObject)
	{
		logger.println("Get the work list information"); //$NON-NLS-1$
	}

	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson)
	{
		String fixedResponseJson = RestApiUtils.fixWorkListResponseJson(responseJson);
		WorkListResponse listResponse = new JsonProcessor().parse(fixedResponseJson, WorkListResponse.class);

		if (listResponse != null)
		{
			for (WorkListInfo workListInfo : listResponse.getWorkListItems())
			{
				logger.println(" "); //$NON-NLS-1$
				logger.println("Action: " + workListInfo.getAction()); //$NON-NLS-1$
				logger.println("Alternate Name: " + workListInfo.getAlternateName()); //$NON-NLS-1$
				logger.println("Application: " + workListInfo.getApplication()); //$NON-NLS-1$
				if (StringUtils.isNotBlank(workListInfo.getSubAppl()))
				{
					logger.println("SubAppl: " + workListInfo.getSubAppl()); //$NON-NLS-1$
				}
				logger.println("Assignment ID: " + workListInfo.getAssignmentId()); //$NON-NLS-1$
				logger.println("Clazz: " + workListInfo.getClazz()); //$NON-NLS-1$
				logger.println("Date/Time: " + workListInfo.getDateTime()); //$NON-NLS-1$
				logger.println("Environment: " + workListInfo.getEnvironment()); //$NON-NLS-1$
				logger.println("Group: " + workListInfo.getGroup()); //$NON-NLS-1$
				logger.println("Level: " + workListInfo.getLevel()); //$NON-NLS-1$
				logger.println("Message: " + workListInfo.getMessage()); //$NON-NLS-1$
				logger.println("Name: " + workListInfo.getName()); //$NON-NLS-1$
				logger.println("Operation: " + workListInfo.getOperation()); //$NON-NLS-1$
				logger.println("Owner: " + workListInfo.getOwner()); //$NON-NLS-1$
				logger.println("Path: " + workListInfo.getPath()); //$NON-NLS-1$
				logger.println("Ref Number: " + workListInfo.getRefNumber()); //$NON-NLS-1$
				logger.println("Relative Path: " + workListInfo.getRelativePath()); //$NON-NLS-1$
				logger.println("Release ID: " + workListInfo.getReleaseId()); //$NON-NLS-1$
				logger.println("Stream: " + workListInfo.getStream()); //$NON-NLS-1$
				logger.println("Task ID: " + workListInfo.getTaskId()); //$NON-NLS-1$
				logger.println("Technology: " + workListInfo.getTechnology()); //$NON-NLS-1$
				logger.println("Type: " + workListInfo.getType()); //$NON-NLS-1$
				logger.println("User: " + workListInfo.getUser()); //$NON-NLS-1$
				logger.println("Version: " + workListInfo.getVersion()); //$NON-NLS-1$
			}
		}

		return listResponse;
	}

}
