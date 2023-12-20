package com.compuware.ispw.model.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="releaseDeploy")
public class ReleaseDeployOpResponse
{
	//private String releaseId; 
	/*private String Id;
	private String requestId;
	private String message;
	private String url;*/
	
	private List<DeploySetInfo> sets = new ArrayList<DeploySetInfo>();
		
	public ReleaseDeployOpResponse()
	{
	}
	
	public void addSet(DeploySetInfo set)
	{
		sets.add(set);
	}
	
	public List<DeploySetInfo> getSets()
	{
		return sets;
	}
	
	public void setSets(List<DeploySetInfo> sets)
	{
		this.sets = sets;
	}
	/*public ReleaseDeployOpResponse(String requestId, String message, String url)
	{
		this.message	=message;
		this.requestId	=requestId;
		this.url=url;
	}*/

	/*public String getRequestId()
	{
		return requestId;
	}

	public void setReleaseId(String releaseId)
	{
		this.requestId = requestId;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}*/
	
	
}
