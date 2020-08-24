/**
* THESE MATERIALS CONTAIN CONFIDENTIAL INFORMATION AND TRADE SECRETS OF BMC SOFTWARE, INC. YOU SHALL MAINTAIN THE MATERIALS AS
* CONFIDENTIAL AND SHALL NOT DISCLOSE ITS CONTENTS TO ANY THIRD PARTY EXCEPT AS MAY BE REQUIRED BY LAW OR REGULATION. USE,
* DISCLOSURE, OR REPRODUCTION IS PROHIBITED WITHOUT THE PRIOR EXPRESS WRITTEN PERMISSION OF BMC SOFTWARE, INC.
*
* ALL BMC SOFTWARE PRODUCTS LISTED WITHIN THE MATERIALS ARE TRADEMARKS OF BMC SOFTWARE, INC. ALL OTHER COMPANY PRODUCT NAMES
* ARE TRADEMARKS OF THEIR RESPECTIVE OWNERS.
*
* (c) Copyright 2020 BMC Software, Inc.
*/
package com.compuware.ispw.model.rest;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="setResponse")
@XmlAccessorType(XmlAccessType.NONE)
public class SetInfoResponse
{
	@XmlElement(name = "setid")
	private String setid;
	
	@XmlElement(name = "applicationId")
	private String applicationId;
	
	@XmlElement(name = "streamName")
	private String streamName;
	
	@XmlElement(name = "description")
	private String description;
	
	@XmlElement(name = "owner")
	private String owner;
	
	@XmlElement(name = "startDate")
	private String startDate;
	
	@XmlElement(name = "startTime")
	private String startTime;
	
	@XmlElement(name = "deployImplementationDate")
	private String deployImplementationDate;
	
	@XmlElement(name = "deployImplementationTime")
	private String deployImplementationTime;
	
	@XmlElement(name = "deployActiveDate")
	private String deployActiveDate;
	
	@XmlElement(name = "deployActiveTime")
	private String deployActiveTime;
	
	@XmlElement(name = "message")
	private String message;
	
	@XmlElement(name = "state")
	private String state;
	
	@XmlElement(name = "tasks")
	private List<TaskInfo> tasks = null;
	
	public String getSetid()
	{
		return setid;
	}
	
	public void setSetid(String setid)
	{
		this.setid = setid;
	}
	
	public String getApplicationId()
	{
		return applicationId;
	}
	
	public void setApplicationId(String applicationId)
	{
		this.applicationId = applicationId;
	}
	
	public String getStreamName()
	{
		return streamName;
	}
	
	public void setStreamName(String streamName)
	{
		this.streamName = streamName;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getOwner()
	{
		return owner;
	}
	
	public void setOwner(String owner)
	{
		this.owner = owner;
	}
	
	public String getStartDate()
	{
		return startDate;
	}

	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}

	public String getStartTime()
	{
		return startTime;
	}

	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}

	public String getDeployImplementationDate()
	{
		return deployImplementationDate;
	}

	public void setDeployImplementationDate(String deployImplementationDate)
	{
		this.deployImplementationDate = deployImplementationDate;
	}

	public String getDeployImplementationTime()
	{
		return deployImplementationTime;
	}

	public void setDeployImplementationTime(String deployImplementationTime)
	{
		this.deployImplementationTime = deployImplementationTime;
	}

	public String getDeployActiveDate()
	{
		return deployActiveDate;
	}

	public void setDeployActiveDate(String deployActiveDate)
	{
		this.deployActiveDate = deployActiveDate;
	}

	public String getDeployActiveTime()
	{
		return deployActiveTime;
	}

	public void setDeployActiveTime(String deployActiveTime)
	{
		this.deployActiveTime = deployActiveTime;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public void addTask(TaskInfo task)
	{
		tasks.add(task);
	}
	
	public List<TaskInfo> getTasks()
	{
		return tasks;
	}

	/**
	 * @param tasks the tasks to set
	 */
	public void setTasks(List<TaskInfo> tasks)
	{
		this.tasks = tasks;
	}
}
/**
* THESE MATERIALS CONTAIN CONFIDENTIAL INFORMATION AND TRADE SECRETS OF BMC SOFTWARE, INC. YOU SHALL MAINTAIN THE MATERIALS AS
* CONFIDENTIAL AND SHALL NOT DISCLOSE ITS CONTENTS TO ANY THIRD PARTY EXCEPT AS MAY BE REQUIRED BY LAW OR REGULATION. USE,
* DISCLOSURE, OR REPRODUCTION IS PROHIBITED WITHOUT THE PRIOR EXPRESS WRITTEN PERMISSION OF BMC SOFTWARE, INC.
*
* ALL BMC SOFTWARE PRODUCTS LISTED WITHIN THE MATERIALS ARE TRADEMARKS OF BMC SOFTWARE, INC. ALL OTHER COMPANY PRODUCT NAMES
* ARE TRADEMARKS OF THEIR RESPECTIVE OWNERS.
*
* (c) Copyright 2020 BMC Software, Inc.
*/