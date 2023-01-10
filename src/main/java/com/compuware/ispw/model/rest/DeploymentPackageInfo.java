/******************************************************************************
* These materials contain confidential information and trade secrets of 
* Compuware Corporation. You shall maintain the materials as confidential 
* and shall not disclose its contents to any third party except as may be 
* required by law or regulation. Use, disclosure, or reproduction is 
* prohibited without the prior express written permission of Compuware 
* Corporation.
* 
* All Compuware products listed within the materials are trademarks of 
* Compuware Corporation. All other company or product names are trademarks
* of their respective owners.
* 
* Copyright (c) 2017-2019 Compuware Corporation. All rights reserved.
******************************************************************************/

package com.compuware.ispw.model.rest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "deploymentPackage")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeploymentPackageInfo
{
	private String packageId;
	private String subEnvironment;
	private String system;
	private String category;
	private String status;
	private String startDate;
	private String endDate;
	private String implementationDate;
	private String activeDate;
	private List<DeploymentPackageItem> deploymentItems = new ArrayList<DeploymentPackageItem>();

	public String getPackageId()
	{
		return packageId;
	}
	
	public void setPackageId(String packageId)
	{
		this.packageId = packageId;
	}
	
	public String getSubEnvironment()
	{
		return subEnvironment;
	}
	
	public void setSubEnvironment(String subEnvironment)
	{
		this.subEnvironment = subEnvironment;
	}
	
	public String getSystem()
	{
		return system;
	}
	
	public void setSystem(String system)
	{
		this.system = system;
	}
	
	public String getCategory()
	{
		return category;
	}
	
	public void setCategory(String category)
	{
		this.category = category;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	
	public String getStartDate()
	{
		return startDate;
	}
	
	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}
	
	public String getEndDate()
	{
		return endDate;
	}
	
	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}
	
	public String getImplementationDate()
	{
		return implementationDate;
	}
	
	public void setImplementationDate(String implementationDate)
	{
		this.implementationDate = implementationDate;
	}
	
	public String getActiveDate()
	{
		return activeDate;
	}
	
	public void setActiveDate(String activeDate)
	{
		this.activeDate = activeDate;
	}
	
	public List<DeploymentPackageItem> getDeploymentItems()
	{
		return deploymentItems;
	}

	public void setDeploymentItems(List<DeploymentPackageItem> deploymentItems)
	{
		this.deploymentItems = deploymentItems;
	}
	
	public void addDeploymentItem(DeploymentPackageItem deploymentItem)
	{
		this.deploymentItems.add(deploymentItem);
	}
}
