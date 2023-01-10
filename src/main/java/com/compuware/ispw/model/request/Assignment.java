package com.compuware.ispw.model.request;

import com.compuware.ces.model.validation.Required;

public class Assignment
{
	@Required
	private String defaultStream;
	@Required
	private String defaultApplication;
	@Required
	private String defaultPath;
	@Required
	private String description;
	@Required
	private String assignmentPrefix;
	private String refNumber;
	private String owner;
	private String release;
	private String tag;
	private String projectNumber;
	private String subAppl;

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public String getDefaultApplication()
	{
		return defaultApplication;
	}

	public void setDefaultApplication(String defaultApplication)
	{
		this.defaultApplication = defaultApplication;
	}

	public String getDefaultStream()
	{
		return defaultStream;
	}

	public void setDefaultStream(String defaultStream)
	{
		this.defaultStream = defaultStream;
	}

	public String getAssignmentPrefix()
	{
		return assignmentPrefix;
	}

	public void setAssignmentPrefix(String assignmentPrefix)
	{
		this.assignmentPrefix = assignmentPrefix;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getDefaultPath()
	{
		return defaultPath;
	}

	public void setDefaultPath(String defaultPath)
	{
		this.defaultPath = defaultPath;
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

	public String getTag()
	{
		return tag;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
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
}
