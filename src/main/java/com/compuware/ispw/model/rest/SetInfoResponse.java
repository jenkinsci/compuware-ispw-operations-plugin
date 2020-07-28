package com.compuware.ispw.model.rest;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="setResponse")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SetInfoResponse
{
	private String setid;
	private String applicationId;
	private String streamName;
	private String description;
	private String owner;
	private String startDate;
	private String startTime;
	private String deployImplementationDate;
	private String deployImplementationTime;
	private String deployActiveDate;
	private String deployActiveTime;
	private String message;
	private String state;
	
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
