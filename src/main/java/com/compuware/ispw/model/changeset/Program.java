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
 * Class to encapsulate the information of a changed program.
 */
public class Program implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Expose
	private String version = "1.0.0"; //$NON-NLS-1$

	@Expose
	private String programName;

	@Expose
	private String programLanguage;

	@Expose
	private Boolean isImpact;

	@Expose
	private String application;

	@Expose
	private String stream;

	@Expose
	private String level;

	@Expose
	private List<LifeCycleLoadModule> lifeCycleLoadModules = null;

	@Expose
	private List<DeployTargetLoadModule> deployTargetLoadModules = null;

	/**
	 * @return the programName
	 */
	public String getProgramName()
	{
		return programName;
	}

	/**
	 * @param programName
	 *            the programName to set
	 */
	public void setProgramName(String programName)
	{
		this.programName = programName;
	}

	/**
	 * @return the programLanguage
	 */
	public String getProgramLanguage()
	{
		return programLanguage;
	}

	/**
	 * @param programLanguage
	 *            the programLanguage to set
	 */
	public void setProgramLanguage(String programLanguage)
	{
		this.programLanguage = programLanguage;
	}

	/**
	 * @return the isImpact
	 */
	public Boolean getIsImpact()
	{
		return isImpact;
	}

	/**
	 * @param isImpact
	 *            the isImpact to set
	 */
	public void setIsImpact(Boolean isImpact)
	{
		this.isImpact = isImpact;
	}

	/**
	 * @return the application
	 */
	public String getApplication()
	{
		return application;
	}

	/**
	 * @param application
	 *            the application to set
	 */
	public void setApplication(String application)
	{
		this.application = application;
	}

	/**
	 * @return the stream
	 */
	public String getStream()
	{
		return stream;
	}

	/**
	 * @param stream
	 *            the stream to set
	 */
	public void setStream(String stream)
	{
		this.stream = stream;
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
	 * Returns the list of load modules. Changing the returned list will change the original list.
	 * 
	 * @return the programs
	 */
	public List<LifeCycleLoadModule> getLifeCycleLoadModules()
	{
		return lifeCycleLoadModules;
	}

	/**
	 * Add a load module to the list.
	 * 
	 * @param loadModule
	 */
	public void addLifeCycleLoadModule(LifeCycleLoadModule loadModule)
	{
		if (lifeCycleLoadModules == null)
		{
			lifeCycleLoadModules = new ArrayList<>();
		}
		
		this.lifeCycleLoadModules.add(loadModule);
	}

	/**
	 * Remove a load module from the list
	 * 
	 * @param loadModule
	 */
	public void removeLifeCycleLoadModule(LifeCycleLoadModule loadModule)
	{
		if (lifeCycleLoadModules != null)
		{
			this.lifeCycleLoadModules.remove(loadModule);

			if (lifeCycleLoadModules.isEmpty())
			{
				lifeCycleLoadModules = null;
			}
		}
	}

	/**
	 * Returns the list of deploy target load libraries. Changing the returned list will change the original.
	 * 
	 * @return
	 */
	public List<DeployTargetLoadModule> getDeployTargetLoadModules()
	{
		return deployTargetLoadModules;
	}

	/**
	 * Adds the given deploy target load library name to the list
	 * 
	 * @param deployTargetLoadModule
	 */
	public void addDeployTargetLoadModule(DeployTargetLoadModule deployTargetLoadModule)
	{
		if (deployTargetLoadModules == null)
		{
			deployTargetLoadModules = new ArrayList<>();
		}
		
		deployTargetLoadModules.add(deployTargetLoadModule);
	}

	/**
	 * Removes the given deploy target load library name from the list
	 * 
	 * @param deployTargetLoadModule
	 */
	public void removeDeployTargetLoadModule(DeployTargetLoadModule deployTargetLoadModule)
	{
		if (deployTargetLoadModules != null)
		{
			deployTargetLoadModules.remove(deployTargetLoadModule);
			
			if (deployTargetLoadModules.isEmpty())
			{
				deployTargetLoadModules = null;
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
		result = prime * result + ((application == null) ? 0 : application.hashCode());
		result = prime * result + ((deployTargetLoadModules == null) ? 0 : deployTargetLoadModules.hashCode());
		result = prime * result + ((isImpact == null) ? 0 : isImpact.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((lifeCycleLoadModules == null) ? 0 : lifeCycleLoadModules.hashCode());
		result = prime * result + ((programLanguage == null) ? 0 : programLanguage.hashCode());
		result = prime * result + ((programName == null) ? 0 : programName.hashCode());
		result = prime * result + ((stream == null) ? 0 : stream.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		if (!(obj instanceof Program))
		{
			return false;
		}
		Program other = (Program) obj;
		if (application == null)
		{
			if (other.application != null)
			{
				return false;
			}
		}
		else if (!application.equals(other.application))
		{
			return false;
		}
		if (deployTargetLoadModules == null)
		{
			if (other.deployTargetLoadModules != null)
			{
				return false;
			}
		}
		else if (!deployTargetLoadModules.equals(other.deployTargetLoadModules))
		{
			return false;
		}
		if (isImpact == null)
		{
			if (other.isImpact != null)
			{
				return false;
			}
		}
		else if (!isImpact.equals(other.isImpact))
		{
			return false;
		}
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
		if (lifeCycleLoadModules == null)
		{
			if (other.lifeCycleLoadModules != null)
			{
				return false;
			}
		}
		else if (!lifeCycleLoadModules.equals(other.lifeCycleLoadModules))
		{
			return false;
		}
		if (programLanguage == null)
		{
			if (other.programLanguage != null)
			{
				return false;
			}
		}
		else if (!programLanguage.equals(other.programLanguage))
		{
			return false;
		}
		if (programName == null)
		{
			if (other.programName != null)
			{
				return false;
			}
		}
		else if (!programName.equals(other.programName))
		{
			return false;
		}
		if (stream == null)
		{
			if (other.stream != null)
			{
				return false;
			}
		}
		else if (!stream.equals(other.stream))
		{
			return false;
		}
		if (version == null)
		{
			if (other.version != null)
			{
				return false;
			}
		}
		else if (!version.equals(other.version))
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