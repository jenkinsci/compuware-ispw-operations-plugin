/**
 * These materials contain confidential information and trade secrets of Compuware Corporation. You shall maintain the materials
 * as confidential and shall not disclose its contents to any third party except as may be required by law or regulation. Use,
 * disclosure, or reproduction is prohibited without the prior express written permission of Compuware Corporation.
 * 
 * All Compuware products listed within the materials are trademarks of Compuware Corporation. All other company or product
 * names are trademarks of their respective owners.
 * 
 * Copyright (c) 2020 Compuware Corporation. All rights reserved. (c) Copyright 2020 BMC Software, Inc.
 */
package com.compuware.ispw.model.ttt.rest;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Level/load library bean
 * 
 * @author pmisvz0
 *
 */
@XmlRootElement(name = "levelLoadLib")
@XmlAccessorType(XmlAccessType.NONE)
public class JaxbLevelLoadLib implements Serializable
{
	private static final long serialVersionUID = -5574697020335583072L;

	@XmlElement(name = "level")
	private String level;
	
	@XmlElement(name = "loadLib")
	private String loadLib;

	/**
	 * Constructor
	 */
	public JaxbLevelLoadLib()
	{
	}

	/**
	 * Constructor
	 */
	public JaxbLevelLoadLib(String level, String loadLib)
	{
		this.level = level;
		this.loadLib = loadLib;
	}

	/**
	 * @return the level
	 */
	public String getLevel()
	{
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(String level)
	{
		this.level = level;
	}

	/**
	 * @return the loadLib
	 */
	public String getLoadLib()
	{
		return loadLib;
	}

	/**
	 * @param loadLib
	 *            the loadLib to set
	 */
	public void setLoadLib(String loadLib)
	{
		this.loadLib = loadLib;
	}

	/**
	 * Returns a json String
	 */
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((loadLib == null) ? 0 : loadLib.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof JaxbLevelLoadLib))
		{
			return false;
		}
		JaxbLevelLoadLib other = (JaxbLevelLoadLib) obj;
		if (level == null)
		{
			if (other.level != null)
			{
				return false;
			}
		}
		else if (!level.equals(other.level))
		{
			return false;
		}
		if (loadLib == null)
		{
			if (other.loadLib != null)
			{
				return false;
			}
		}
		else if (!loadLib.equals(other.loadLib))
		{
			return false;
		}
		return true;
	}

}
