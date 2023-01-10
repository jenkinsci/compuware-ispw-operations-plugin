package com.compuware.ispw.model.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "release")
@XmlAccessorType(XmlAccessType.FIELD)
public class AssignmentInfo
{
	private String stream;
	private String application;
	private String subAppl;
	private String defaultPath;
	private String description;
	private String owner;
	private String assignmentPrefix;
	private String sandbox;
	private String sandboxJoinAtLevel;
	
	/* used for response - confirm with CES team
	@XmlElements({
	    @XmlElement(name="referenceNumber"),
	    @XmlElement(name="refNumber")
	})*/
	private String refNumber;
	/* used only for response - confirm with CES team
	@XmlElements({
	    @XmlElement(name="release")
	    ,@XmlElement(name="releaseId")
	})*/
	@XmlElement(name="releaseId")
	private String release;
	
	private String userTag;
	@XmlElement(name="assignmentId")
	private String projectNumber;

	private String message;

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

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public String getAssignmentPrefix()
	{
		return assignmentPrefix;
	}

	public void setAssignmentPrefix(String assignmentPrefix)
	{
		this.assignmentPrefix = assignmentPrefix;
	}

	public String getDefaultPath()
	{
		return defaultPath;
	}

	public void setDefaultPath(String defaultPath)
	{
		this.defaultPath = defaultPath;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getRefNumber()
	{
		return refNumber;
	}

	public void setRefNumber(String refNumber)
	{
		this.refNumber = refNumber;
	}

	public String getRelease()
	{
		return release;
	}

	public void setRelease(String release)
	{
		this.release = release;
	}

	public String getUserTag()
	{
		return userTag;
	}

	public void setUserTag(String userTag)
	{
		this.userTag = userTag;
	}

	public String getProjectNumber()
	{
		return projectNumber;
	}

	public void setProjectNumber(String projectNumber)
	{
		this.projectNumber = projectNumber;
	}

	public String getSubAppl()
	{
		return subAppl;
	}

	public void setSubAppl(String subAppl)
	{
		this.subAppl = subAppl;
	}

	public String getSandbox()
	{
		return sandbox;
	}

	public void setSandbox(String sandbox)
	{
		this.sandbox = sandbox;
	}

	public String getSandboxJoinAtLevel()
	{
		return sandboxJoinAtLevel;
	}

	public void setSandboxJoinAtLevel(String sandboxJoinAtLevel)
	{
		this.sandboxJoinAtLevel = sandboxJoinAtLevel;
	}
	
	
}
