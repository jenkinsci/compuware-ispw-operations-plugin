package com.compuware.ispw.model.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.compuware.ces.communications.service.data.EventCallback;
import com.compuware.ces.model.BasicAuthentication;
import com.compuware.ces.model.HttpHeader;
import com.compuware.ces.model.validation.Default;

@XmlRootElement(name = "DeploySet")
@XmlAccessorType(XmlAccessType.NONE)
public class DeploySetInfo
{
	@XmlElement(name = "deployments")
	private List<DeploymentInfo> deployments = new ArrayList<DeploymentInfo>(); 
	@XmlElement(name = "id")
	private String id;
	
	public DeploySetInfo()
	{
	}
		
	public void addDeployment(DeploymentInfo deployment)
	{
		deployments.add(deployment);
	}
	
	public List<DeploymentInfo> getDeploymentList()
	{
		return deployments;
	}
	
	public void setDeploymentList(List<DeploymentInfo> deployments)
	{
		this.deployments=deployments;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}
}
