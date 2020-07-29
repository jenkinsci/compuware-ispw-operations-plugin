/**
 * THESE MATERIALS CONTAIN CONFIDENTIAL INFORMATION AND TRADE SECRETS OF BMC SOFTWARE, INC. YOU SHALL MAINTAIN THE MATERIALS AS
 * CONFIDENTIAL AND SHALL NOT DISCLOSE ITS CONTENTS TO ANY THIRD PARTY EXCEPT AS MAY BE REQUIRED BY LAW OR REGULATION. USE,
 * DISCLOSURE, OR REPRODUCTION IS PROHIBITED WITHOUT THE PRIOR EXPRESS WRITTEN PERMISSION OF BMC SOFTWARE, INC.
 * 
 * ALL BMC SOFTWARE PRODUCTS LISTED WITHIN THE MATERIALS ARE TRADEMARKS OF BMC SOFTWARE, INC. ALL OTHER COMPANY PRODUCT NAMES
 * ARE TRADEMARKS OF THEIR RESPECTIVE OWNERS.
 * 
 * � Copyright 2020 BMC Software, Inc.
 */
package com.compuware.ispw.model.changeset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Class to encapsulate the information for a load module.
 */
public class LifeCycleLoadModule implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Expose
	private String loadModName;

	@Expose
	private String loadLibName;

	@Expose
	private String componentType;

	@Expose
	private String componentClass;
	
	@Expose
	private List<String> loadLibConcatenation = new ArrayList<>();

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
	 * @return a copy of the loadLibConcatenation list
	 */
	public List<String> getLoadLibConcatenation()
	{
		return new ArrayList<>(loadLibConcatenation);
	}

	/**
	 * @param loadLibConcatenation the loadLibConcatenation to add
	 */
	public void addLoadLibConcatenation(String loadLibConcatenation)
	{
		this.loadLibConcatenation.add(loadLibConcatenation);
	}
	
	/**
	 * @param loadLibConcatenation the loadLibConcatenation to add
	 */
	public void addLoadLibConcatenation(List<String> loadLibConcatenation)
	{
		this.loadLibConcatenation.addAll(loadLibConcatenation);
	}

	/**
	 * @param loadLibConcatenation the loadLibConcatenation to remove
	 */
	public void removeLoadLibConcatenation(String loadLibConcatenation)
	{
		this.loadLibConcatenation.remove(loadLibConcatenation);
	}
	
	/**
	 * Returns a json String
	 */
	@Override
	public String toString()
	{
		return gson.toJson(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.loadLibName == null) ? 0 : loadLibName.hashCode());
		result = prime * result + ((this.loadModName == null) ? 0 : loadModName.hashCode());
		result = prime * result + ((this.componentType == null) ? 0 : componentType.hashCode());
		result = prime * result + ((this.componentClass == null) ? 0 : componentClass.hashCode());
		result = prime * result + ((this.loadLibConcatenation == null) ? 0 : loadLibConcatenation.hashCode());
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
		if (getClass() != obj.getClass())
		{
			return false;
		}
		LifeCycleLoadModule loadMod = (LifeCycleLoadModule) obj;
		boolean isEqual = StringUtils.equals(loadMod.loadLibName, this.loadLibName);
		isEqual = isEqual && StringUtils.equals(loadMod.loadModName, this.loadModName);
		isEqual = isEqual && StringUtils.equals(loadMod.componentType, this.componentType);
		isEqual = isEqual && StringUtils.equals(loadMod.componentClass, this.componentClass);
		isEqual = isEqual && loadMod.getLoadLibConcatenation().equals(loadLibConcatenation);
		return isEqual;
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
 * � Copyright 2020 BMC Software, Inc.
 */