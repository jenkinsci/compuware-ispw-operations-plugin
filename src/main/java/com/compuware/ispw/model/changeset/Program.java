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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

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
	private List<LifeCycleLoadModule> lifeCycleLoadModules = new ArrayList<>();

	@Expose
	private List<DeployTargetLoadModule> deployTargetLoadModules = new ArrayList<>();

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
	 * Returns a copy of the list of load modules. Changing the returned list will not change the original list.
	 * 
	 * @return the programs
	 */
	public List<LifeCycleLoadModule> getLifeCycleLoadModules()
	{
		return new ArrayList<>(lifeCycleLoadModules);
	}

	/**
	 * Add a load module to the list.
	 * 
	 * @param loadModule
	 */
	public void addLifeCycleLoadModule(LifeCycleLoadModule loadModule)
	{
		this.lifeCycleLoadModules.add(loadModule);
	}

	/**
	 * Remove a load module from the list
	 * 
	 * @param loadModule
	 */
	public void removeLifeCycleLoadModule(LifeCycleLoadModule loadModule)
	{
		this.lifeCycleLoadModules.remove(loadModule);
	}

	/**
	 * Returns a copy of the list of deploy target load libraries. Changing the returned list will not change the original.
	 * 
	 * @return
	 */
	public List<DeployTargetLoadModule> getDeployTargetLoadModules()
	{
		return new ArrayList<>(deployTargetLoadModules);
	}

	/**
	 * Adds the given deploy target load library name to the list
	 * 
	 * @param loadLibraryName
	 */
	public void addDeployTargetLoadModule(DeployTargetLoadModule loadLibraryName)
	{
		deployTargetLoadModules.add(loadLibraryName);
	}

	/**
	 * Removes the given deploy target load library name from the list
	 * 
	 * @param loadLibraryName
	 */
	public void removeDeployTargetLoadModule(DeployTargetLoadModule loadLibraryName)
	{
		deployTargetLoadModules.remove(loadLibraryName);
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
		result = prime * result + ((this.application == null) ? 0 : application.hashCode());
		result = prime * result + ((this.level == null) ? 0 : level.hashCode());
		result = prime * result + ((this.programLanguage == null) ? 0 : programLanguage.hashCode());
		result = prime * result + ((this.programName == null) ? 0 : programName.hashCode());
		result = prime * result + ((this.stream == null) ? 0 : stream.hashCode());
		result = prime * result + ((Boolean.TRUE.equals(isImpact)) ? 0 : 1);
		result = prime * result + (this.lifeCycleLoadModules == null ? 0 : lifeCycleLoadModules.hashCode());
		result = prime * result + (this.deployTargetLoadModules == null ? 0 : deployTargetLoadModules.hashCode());
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
		Program program = (Program) obj;
		boolean isEqual = StringUtils.equals(program.application, this.application);
		isEqual = isEqual && StringUtils.equals(program.level, this.level);
		isEqual = isEqual && StringUtils.equals(program.programLanguage, this.programLanguage);
		isEqual = isEqual && StringUtils.equals(program.programName, this.programName);
		isEqual = isEqual && StringUtils.equals(program.stream, this.stream);
		isEqual = isEqual && program.getLifeCycleLoadModules().equals(lifeCycleLoadModules);
		isEqual = isEqual && program.getDeployTargetLoadModules().equals(deployTargetLoadModules);
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