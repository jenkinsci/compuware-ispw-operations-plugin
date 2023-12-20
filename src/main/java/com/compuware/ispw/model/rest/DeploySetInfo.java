/**
 * THESE MATERIALS CONTAIN CONFIDENTIAL INFORMATION AND TRADE SECRETS OF BMC SOFTWARE, INC. YOU SHALL MAINTAIN THE MATERIALS AS
 * CONFIDENTIAL AND SHALL NOT DISCLOSE ITS CONTENTS TO ANY THIRD PARTY EXCEPT AS MAY BE REQUIRED BY LAW OR REGULATION. USE,
 * DISCLOSURE, OR REPRODUCTION IS PROHIBITED WITHOUT THE PRIOR EXPRESS WRITTEN PERMISSION OF BMC SOFTWARE, INC.
 *
 * ALL BMC SOFTWARE PRODUCTS LISTED WITHIN THE MATERIALS ARE TRADEMARKS OF BMC SOFTWARE, INC. ALL OTHER COMPANY PRODUCT NAMES
 * ARE TRADEMARKS OF THEIR RESPECTIVE OWNERS.
 *
 * (c) Copyright 2023 BMC Software, Inc.
 */
package com.compuware.ispw.model.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
