/**
 * THESE MATERIALS CONTAIN CONFIDENTIAL INFORMATION AND TRADE SECRETS OF BMC SOFTWARE, INC. YOU SHALL MAINTAIN THE MATERIALS AS
 * CONFIDENTIAL AND SHALL NOT DISCLOSE ITS CONTENTS TO ANY THIRD PARTY EXCEPT AS MAY BE REQUIRED BY LAW OR REGULATION. USE,
 * DISCLOSURE, OR REPRODUCTION IS PROHIBITED WITHOUT THE PRIOR EXPRESS WRITTEN PERMISSION OF BMC SOFTWARE, INC.
 *
 * ALL BMC SOFTWARE PRODUCTS LISTED WITHIN THE MATERIALS ARE TRADEMARKS OF BMC SOFTWARE, INC. ALL OTHER COMPANY PRODUCT NAMES
 * ARE TRADEMARKS OF THEIR RESPECTIVE OWNERS.
 *
 * (c) Copyright 2023 BMC Software, Inc.
 */
package com.compuware.ispw.model.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="releaseDeploy")
public class ReleaseDeployOpResponse
{
	
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
	
}
