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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.compuware.ces.model.validation.Required;

@XmlRootElement(name = "task")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class TaskInfo
{
	@Required
	private String application;
	@Required
	private String stream;
	@Required
	private String moduleType;
	@Required
	private String moduleName;
	@Required
	private String userId;
	private String url;
	private String taskId;
	private String extension;
	private String level;
	private String operation;
	private String action;
	private String dateTime;
	private String status;
	private String message;
	private String set;
	private String internalVersion;
	private String baseVersion;
	private String replaceVersion;
	private String environment;
	private String clazz;
	private String version;
	private String alternateName;
	private String release;
	private String container;
	private String flags;
	private String currentLevel;
	private String startingLevel;
	private String generateSequence;
	private Boolean sql;
	private Boolean ims;
	private Boolean cics;
	private Boolean program;
	private String option1;
	private String option2;
	private String option3;
	private String option4;
	private String option5;
	
	@XmlElement(name = "loadModules")
	private List<LoadModule> loadModules = null;
	
	public String getApplication()
	{
		return application;
	}
	
	public void setApplication(String application)
	{
		this.application = application;
	}
	
	public String getStream()
	{
		return stream;
	}
	
	public void setStream(String stream)
	{
		this.stream = stream;
	}
	
	public String getModuleType()
	{
		return moduleType;
	}
	
	public void setModuleType(String moduleType)
	{
		this.moduleType = moduleType;
	}
	
	public String getModuleName()
	{
		return moduleName;
	}
	
	public void setModuleName(String moduleName)
	{
		this.moduleName = moduleName;
	}
	
	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getUserId()
	{
		return userId;
	}
	
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	
	public String getTaskId()
	{
		return taskId;
	}
	
	public void setTaskId(String taskId)
	{
		this.taskId = taskId;
	}

	public String getExtension()
	{
		return extension;
	}
	public void setExtension(String extension)
	{
		this.extension = extension;
	}

	public String getLevel()
	{
		return level;
	}
	
	public void setLevel(String level)
	{
		this.level = level;
	}
	
	public String getOperation()
	{
		return operation;
	}
	
	public void setOperation(String operation)
	{
		this.operation = operation;
	}
	
	public String getAction()
	{
		return action;
	}
	
	public void setAction(String action)
	{
		this.action = action;
	}
	
	public String getDateTime()
	{
		return dateTime;
	}
	
	public void setDateTime(String dateTime)
	{
		this.dateTime = dateTime;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getSet()
	{
		return set;
	}
	
	public void setSet(String set)
	{
		this.set = set;
	}
	
	public String getInternalVersion()
	{
		return internalVersion;
	}
	
	public void setInternalVersion(String internalVersion)
	{
		this.internalVersion = internalVersion;
	}
	
	public String getBaseVersion()
	{
		return baseVersion;
	}
	
	public void setBaseVersion(String baseVersion)
	{
		this.baseVersion = baseVersion;
	}
	
	public String getReplaceVersion()
	{
		return replaceVersion;
	}
	
	public void setReplaceVersion(String replaceVersion)
	{
		this.replaceVersion = replaceVersion;
	}
	
	public String getEnvironment()
	{
		return environment;
	}
	
	public void setEnvironment(String environment)
	{
		this.environment = environment;
	}

	public String getClazz()
	{
		return clazz;
	}
	
	public void setClazz(String clazz)
	{
		this.clazz = clazz;
	}
	
	public String getVersion()
	{
		return version;
	}
	
	public void setVersion(String version)
	{
		this.version = version;
	}
	
	public String getAlternateName()
	{
		return alternateName;
	}
	
	public void setAlternateName(String alternateName)
	{
		this.alternateName = alternateName;
	}
	
	public String getRelease()
	{
		return release;
	}
	
	public void setRelease(String release)
	{
		this.release = release;
	}
	
	public String getContainer()
	{
		return container;
	}
	
	public void setContainer(String container)
	{
		this.container = container;
	}
	
	public String getFlags()
	{
		return flags;
	}
	
	public void setFlags(String flags)
	{
		this.flags = flags;
	}
	
	public String getCurrentLevel()
	{
		return currentLevel;
	}
	
	public void setCurrentLevel(String currentLevel)
	{
		this.currentLevel = currentLevel;
	}
	
	public String getStartingLevel()
	{
		return startingLevel;
	}
	
	public void setStartingLevel(String startingLevel)
	{
		this.startingLevel = startingLevel;
	}
	
	public String getGenerateSequence()
	{
		return generateSequence;
	}
	
	public void setGenerateSequence(String genSeq)
	{
		this.generateSequence = genSeq;
	}
	
	public Boolean getSql()
	{
		return sql;
	}
	
	public void setSql(Boolean sql)
	{
		this.sql = sql;
	}
	
	public Boolean getIms()
	{
		return ims;
	}
	
	public void setIms(Boolean ims)
	{
		this.ims = ims;
	}

	public Boolean getCics()
	{
		return cics;
	}

	public void setCics(Boolean cics)
	{
		this.cics = cics;
	}
	
	public Boolean getProgram()
	{
		return program;
	}
	
	public void setProgram(Boolean program)
	{
		this.program = program;
	}
	
	public String getOption1()
	{
		return option1;
	}
	
	public void setOption1(String option1)
	{
		this.option1 = option1;
	}
	
	public String getOption2()
	{
		return option2;
	}
	
	public void setOption2(String option2)
	{
		this.option2 = option2;
	}
	
	public String getOption3()
	{
		return option3;
	}
	
	public void setOption3(String option3)
	{
		this.option3 = option3;
	}
	
	public String getOption4()
	{
		return option4;
	}
	
	public void setOption4(String option4)
	{
		this.option4 = option4;
	}
	
	public String getOption5()
	{
		return option5;
	}
	
	public void setOption5(String option5)
	{
		this.option5 = option5;
	}

	/**
	 * @return the loadModules
	 */
	public List<LoadModule> getLoadModules()
	{
		return loadModules;
	}
	
	public void addLoadModule(LoadModule loadModule)
	{
		if (loadModules == null)
		{
			loadModules = new ArrayList<LoadModule>();
		}

		loadModules.add(loadModule);
	}
}
