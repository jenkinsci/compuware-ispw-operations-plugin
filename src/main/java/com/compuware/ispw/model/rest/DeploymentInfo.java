package com.compuware.ispw.model.rest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "deployment")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeploymentInfo
{
	private List<String> message = new ArrayList<String>();
	private String requestId;
	private String setId;
	private String environment;
	private String status;
	private String description;
	private String createDate;
	private String createDateTime;
	private String url;
	private List<DeploymentPackageInfo> packages = new ArrayList<DeploymentPackageInfo>();
	
	public List<String> getMessage()
	{
		return message;
	}

	public void setMessage(List<String> message)
	{
		this.message = message;
	}
	
	public void addMessage(String msg)
	{
		this.message.add(msg);
	}

	public String getRequestId()
	{
		return requestId;
	}
	
	public void setRequestId(String requestId)
	{
		this.requestId = requestId;
	}
	
	public String getSetId()
	{
		return setId;
	}
	
	public void setSetId(String setId)
	{
		this.setId = setId;
	}
	
	public String getEnvironment()
	{
		return environment;
	}
	
	public void setEnvironment(String environment)
	{
		this.environment = environment;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getCreateDate()
	{
		return createDate;
	}
	
	public void setCreateDate(String createDate)
	{
		this.createDate = createDate;
	}
	
	public String getCreateDateTime()
	{
		return createDateTime;
	}
	
	public void setCreateDateTime(String createDateTime)
	{
		this.createDateTime = createDateTime;
	}

	public List<DeploymentPackageInfo> getPackages()
	{
		return packages;
	}

	public void setPackages(List<DeploymentPackageInfo> packages)
	{
		this.packages = packages;
	}
	
	public void addPackage(DeploymentPackageInfo pkg)
	{
		this.packages.add(pkg);
	}
	
	public List<String> getUrl()
	{
		return message;
	}

	public void setMessage(String url)
	{
		this.url = url;
	}
}
