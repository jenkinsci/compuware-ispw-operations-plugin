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
package com.compuware.ispw.model.changeset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
	private List<LevelLoadLib> loadLibConcatenation = null;

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
	public List<LevelLoadLib> getLoadLibConcatenation()
	{
		return loadLibConcatenation;
	}

	/**
	 * @param levelLoadLib the loadLibConcatenation to add
	 */
	public void addLoadLibConcatenation(LevelLoadLib levelLoadLib)
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
	public void addLoadLibConcatenation(List<LevelLoadLib> loadLibConcats)
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
	public void removeLoadLibConcatenation(LevelLoadLib loadLib)
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
		if (!(obj instanceof LifeCycleLoadModule))
		{
			return false;
		}
		LifeCycleLoadModule other = (LifeCycleLoadModule) obj;
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