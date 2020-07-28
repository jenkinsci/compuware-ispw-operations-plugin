/**
 * These materials contain confidential information and trade secrets of Compuware Corporation. You shall maintain the materials
 * as confidential and shall not disclose its contents to any third party except as may be required by law or regulation. Use,
 * disclosure, or reproduction is prohibited without the prior express written permission of Compuware Corporation.
 * 
 * All Compuware products listed within the materials are trademarks of Compuware Corporation. All other company or product
 * names are trademarks of their respective owners.
 * 
 * Copyright (c) 2019 Compuware Corporation. All rights reserved. (c) Copyright 2020 BMC Software, Inc.
 */
package com.compuware.ispw.restapi;

import org.apache.commons.lang3.StringUtils;

/**
 * Contains BuildParms and request body built from BuildParms
 * 
 * @author pmisvz0
 *
 */
public class BuildParmsRequestBody
{
	private String requestBody;
	private BuildParms buildParms;

	/**
	 * Constructor
	 * 
	 * @param requestBody
	 *            request body
	 * @param buildParms
	 *            build parameters
	 */
	public BuildParmsRequestBody(String requestBody, BuildParms buildParms)
	{
		this.requestBody = requestBody;
		this.buildParms = buildParms;
	}

	/**
	 * @return the requestBody
	 */
	public String getRequestBody()
	{
		return requestBody;
	}

	/**
	 * @param requestBody
	 *            the requestBody to set
	 */
	public void setRequestBody(String requestBody)
	{
		this.requestBody = requestBody;
	}

	/**
	 * @return the buildParms
	 */
	public BuildParms getBuildParms()
	{
		return buildParms;
	}

	/**
	 * @param buildParms
	 *            the buildParms to set
	 */
	public void setBuildParms(BuildParms buildParms)
	{
		this.buildParms = buildParms;
	}

	public boolean hasRequestBody()
	{
		return StringUtils.isNotBlank(requestBody);
	}

}
