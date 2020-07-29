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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Class to encapsulate the list of programs that were changed
 */
public class ProgramList implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Expose
	private String version = "1.0.0"; //$NON-NLS-1$

	@Expose
	private List<Program> programs = new ArrayList<>();

	public String getVersion()
	{
		return this.version;
	}

	/**
	 * Returns a copy of the list of changed programs. Changing this returned list does not change the original.
	 * 
	 * @return the programs
	 */
	public List<Program> getPrograms()
	{
		return new ArrayList<>(programs);
	}

	/**
	 * Adds a program to the list
	 * 
	 * @param program
	 */
	public void addProgram(Program program)
	{
		this.programs.add(program);
	}

	/**
	 * Removes a program from the list
	 * 
	 * @param program
	 */
	public void removeProgram(Program program)
	{
		this.programs.remove(program);
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
		result = prime * result + ((programs == null) ? 0 : programs.hashCode());
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
		ProgramList programList = (ProgramList) obj;
		return programList.getPrograms().equals(this.programs);
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