/**
 * These materials contain confidential information and trade secrets of Compuware Corporation. You shall maintain the materials
 * as confidential and shall not disclose its contents to any third party except as may be required by law or regulation. Use,
 * disclosure, or reproduction is prohibited without the prior express written permission of Compuware Corporation.
 * 
 * All Compuware products listed within the materials are trademarks of Compuware Corporation. All other company or product
 * names are trademarks of their respective owners.
 * 
 * Copyright (c) 2019 Compuware Corporation. All rights reserved.
 */
package com.compuware.ispw.model.rest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 */
@XmlRootElement(name="build")
@XmlAccessorType(XmlAccessType.NONE)
public class BuildResponse
{
	@XmlElement(name = "setId")
	private String setId;
	@XmlElement(name = "assignmentId")
	private String assignmentId;
	@XmlElement(name = "message")
	private String message;
	@XmlElement(name = "url")
	private String url;
	@XmlElement(name = "tasks")
	private List<TaskInfo> tasksBuilt = new ArrayList<>();

	/**
	 * @return the setId
	 */
	public String getSetId()
	{
		return setId;
	}

	/**
	 * @param setId
	 *            the setId to set
	 */
	public void setSetId(String setId)
	{
		this.setId = setId;
	}

	/**
	 * @return the assignmentId
	 */
	public String getAssignmentId()
	{
		return assignmentId;
	}

	/**
	 * @param assignmentId
	 *            the assignmentId to set
	 */
	public void setAssignmentId(String assignmentId)
	{
		this.assignmentId = assignmentId;
	}

	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * @return the tasksBuilt
	 */
	public List<TaskInfo> getTasksBuilt()
	{
		return tasksBuilt;
	}
	
	public void addTaskBuilt(TaskInfo builtTask)
	{
		tasksBuilt.add(builtTask);
	}

	/**
	 * @param tasksBuilt
	 *            the tasksBuilt to set
	 */
	public void setTasksBuilt(List<TaskInfo> tasksBuilt)
	{
		this.tasksBuilt = tasksBuilt;
	}

}
