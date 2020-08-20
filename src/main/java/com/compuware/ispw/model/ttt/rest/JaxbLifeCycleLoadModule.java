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
package com.compuware.ispw.model.ttt.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Class to encapsulate the information for a load module.
 */
@XmlRootElement(name = "lifeCycleLoadModule")
@XmlAccessorType(XmlAccessType.NONE)
public class JaxbLifeCycleLoadModule implements Serializable
{
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "loadModName")
	private String loadModName;
	
	@XmlElement(name = "loadLibName")
	private String loadLibName;
	
	@XmlElement(name = "componentType")
	private String componentType;
	
	@XmlElement(name = "componentClass")
	private String componentClass;
	
	@XmlElement(name = "loadLibConcatenation")
	private List<JaxbLevelLoadLib> loadLibConcatenation = null;

	/**
	 * @return the loadModName
	 */
	public String getLoadModName()
	{
		return loadModName;
	}

	/**
	 * @param loadModName
	 *            the loadModName to set
	 */
	public void setLoadModName(String loadModName)
	{
		this.loadModName = loadModName;
	}

	/**
	 * @return the loadLibName
	 */
	public String getLoadLibName()
	{
		return loadLibName;
	}

	/**
	 * @param loadLibName
	 *            the loadLibName to set
	 */
	public void setLoadLibName(String loadLibName)
	{
		this.loadLibName = loadLibName;
	}

	/**
	 * @return the componentType
	 */
	public String getComponentType()
	{
		return componentType;
	}

	/**
	 * @param componentType
	 *            the componentType to set
	 */
	public void setComponentType(String componentType)
	{
		this.componentType = componentType;
	}

	/**
	 * @return the componentClass
	 */
	public String getComponentClass()
	{
		return componentClass;
	}

	/**
	 * @param componentClass
	 *            the componentClass to set
	 */
	public void setComponentClass(String componentClass)
	{
		this.componentClass = componentClass;
	}

	/**
	 * @return the loadLibConcatenation list - modifying this list will change the original
	 */
	public List<JaxbLevelLoadLib> getLoadLibConcatenation()
	{
		return loadLibConcatenation;
	}

	/**
	 * @param levelLoadLib the loadLibConcatenation to add
	 */
	public void addLoadLibConcatenation(JaxbLevelLoadLib levelLoadLib)
	{
		if (loadLibConcatenation == null)
		{
			loadLibConcatenation = new ArrayList<>();
		}
		
		this.loadLibConcatenation.add(levelLoadLib);
	}
	
	/**
	 * @param loadLibConcats the loadLibConcatenation to add
	 */
	public void addLoadLibConcatenation(List<JaxbLevelLoadLib> loadLibConcats)
	{
		if (loadLibConcatenation == null)
		{
			loadLibConcatenation = new ArrayList<>();
		}
		
		this.loadLibConcatenation.addAll(loadLibConcats);
	}

	/**
	 * @param loadLibConcatenation the loadLibConcatenation to remove
	 */
	public void removeLoadLibConcatenation(JaxbLevelLoadLib loadLib)
	{
		if (loadLibConcatenation != null)
		{
			this.loadLibConcatenation.remove(loadLib);

			if (loadLibConcatenation.isEmpty())
			{
				loadLibConcatenation = null;
			}
		}
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
		result = prime * result + ((componentClass == null) ? 0 : componentClass.hashCode());
		result = prime * result + ((componentType == null) ? 0 : componentType.hashCode());
		result = prime * result + ((loadLibConcatenation == null) ? 0 : loadLibConcatenation.hashCode());
		result = prime * result + ((loadLibName == null) ? 0 : loadLibName.hashCode());
		result = prime * result + ((loadModName == null) ? 0 : loadModName.hashCode());
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
		if (!(obj instanceof JaxbLifeCycleLoadModule))
		{
			return false;
		}
		JaxbLifeCycleLoadModule other = (JaxbLifeCycleLoadModule) obj;
		if (componentClass == null)
		{
			if (other.componentClass != null)
			{
				return false;
			}
		}
		else if (!componentClass.equals(other.componentClass))
		{
			return false;
		}
		if (componentType == null)
		{
			if (other.componentType != null)
			{
				return false;
			}
		}
		else if (!componentType.equals(other.componentType))
		{
			return false;
		}
		if (loadLibConcatenation == null)
		{
			if (other.loadLibConcatenation != null)
			{
				return false;
			}
		}
		else if (!loadLibConcatenation.equals(other.loadLibConcatenation))
		{
			return false;
		}
		if (loadLibName == null)
		{
			if (other.loadLibName != null)
			{
				return false;
			}
		}
		else if (!loadLibName.equals(other.loadLibName))
		{
			return false;
		}
		if (loadModName == null)
		{
			if (other.loadModName != null)
			{
				return false;
			}
		}
		else if (!loadModName.equals(other.loadModName))
		{
			return false;
		}
		return true;
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