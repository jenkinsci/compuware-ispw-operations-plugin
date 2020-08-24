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
* (c) Copyright 2017-2020, 2020 BMC Software, Inc.
******************************************************************************/

package com.compuware.ispw.model.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.compuware.ces.model.validation.Required;
import com.compuware.ispw.model.ttt.rest.JaxbProgram;

@XmlRootElement(name = "task")
@XmlAccessorType(XmlAccessType.NONE)
public class TaskInfo
{
	@Required
	@XmlElement(name = "application")
	private String application;
	
	@Required
	@XmlElement(name = "stream")
	private String stream;
	
	@Required
	@XmlElement(name = "moduleType")
	private String moduleType;
	
	@Required
	@XmlElement(name = "moduleName")
	private String moduleName;
	
	@Required
	@XmlElement(name = "userId")
	private String userId;
	
	@XmlElement(name = "url")
	private String url;
	
	@XmlElement(name = "taskId")
	private String taskId;
	
	@XmlElement(name = "extension")
	private String extension;
	
	@XmlElement(name = "level")
	private String level;
	
	@XmlElement(name = "operation")
	private String operation;
	
	@XmlElement(name = "action")
	private String action;
	
	@XmlElement(name = "dateTime")
	private String dateTime;
	
	@XmlElement(name = "status")
	private String status;
	
	@XmlElement(name = "message")
	private String message;
	
	@XmlElement(name = "set")
	private String set;
	
	@XmlElement(name = "internalVersion")
	private String internalVersion;
	
	@XmlElement(name = "baseVersion")
	private String baseVersion;
	
	@XmlElement(name = "replaceVersion")
	private String replaceVersion;
	
	@XmlElement(name = "environment")
	private String environment;
	
	@XmlElement(name = "clazz")
	private String clazz;
	
	@XmlElement(name = "version")
	private String version;
	
	@XmlElement(name = "alternateName")
	private String alternateName;
	
	@XmlElement(name = "release")
	private String release;
	
	@XmlElement(name = "container")
	private String container;
	
	@XmlElement(name = "flags")
	private String flags;
	
	@XmlElement(name = "currentLevel")
	private String currentLevel;
	
	@XmlElement(name = "startingLevel")
	private String startingLevel;
	
	@XmlElement(name = "generateSequence")
	private String generateSequence;
	
	@XmlElement(name = "sql")
	private Boolean sql;
	
	@XmlElement(name = "ims")
	private Boolean ims;
	
	@XmlElement(name = "cics")
	private Boolean cics;
	
	@XmlElement(name = "program")
	private Boolean program;
	
	@XmlElement(name = "option1")
	private String option1;
	
	@XmlElement(name = "option2")
	private String option2;
	
	@XmlElement(name = "option3")
	private String option3;
	
	@XmlElement(name = "option4")
	private String option4;
	
	@XmlElement(name = "option5")
	private String option5;
	
	@XmlElement(name = "jaxbProgram")
	private JaxbProgram jaxbProgram = null;

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

	public JaxbProgram getJaxbProgram()
	{
		return jaxbProgram;
	}

	public void setJaxbProgram(JaxbProgram jaxbProgram)
	{
		this.jaxbProgram = jaxbProgram;
	}
}
