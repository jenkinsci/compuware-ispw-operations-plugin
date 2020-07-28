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
* Copyright (c) 2017 Compuware Corporation. All rights reserved.
******************************************************************************/
package com.compuware.ispw.model.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "loadModule")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class LoadModule
{
	private String modName;
	private String libName;

	public String getModName()
	{
		return modName;
	}

	public void setModName(String modName)
	{
		this.modName = modName;
	}

	public String getLibName()
	{
		return libName;
	}

	public void setLibName(String libName)
	{
		this.libName = libName;
	}
}
