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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Class to encapsulate the information for a deploy load module.
 */
public class DeployTargetLoadModule implements Serializable
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
	private String deployEnvironment;

	@Expose
	private String subenvironment;

	@Expose
	private String system;

	@Expose
	private String deployType;

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
		result = prime * result + ((deployEnvironment == null) ? 0 : deployEnvironment.hashCode());
		result = prime * result + ((deployType == null) ? 0 : deployType.hashCode());
		result = prime * result + ((loadLibName == null) ? 0 : loadLibName.hashCode());
		result = prime * result + ((loadModName == null) ? 0 : loadModName.hashCode());
		result = prime * result + ((subenvironment == null) ? 0 : subenvironment.hashCode());
		result = prime * result + ((system == null) ? 0 : system.hashCode());
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
		if (!(obj instanceof DeployTargetLoadModule))
		{
			return false;
		}
		DeployTargetLoadModule other = (DeployTargetLoadModule) obj;
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
		if (deployEnvironment == null)
		{
			if (other.deployEnvironment != null)
			{
				return false;
			}
		}
		else if (!deployEnvironment.equals(other.deployEnvironment))
		{
			return false;
		}
		if (deployType == null)
		{
			if (other.deployType != null)
			{
				return false;
			}
		}
		else if (!deployType.equals(other.deployType))
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
		if (subenvironment == null)
		{
			if (other.subenvironment != null)
			{
				return false;
			}
		}
		else if (!subenvironment.equals(other.subenvironment))
		{
			return false;
		}
		if (system == null)
		{
			if (other.system != null)
			{
				return false;
			}
		}
		else if (!system.equals(other.system))
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