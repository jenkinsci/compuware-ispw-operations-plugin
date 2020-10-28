package com.compuware.ispw.restapi.util;

import java.util.HashMap;
import java.util.Map;


public enum Operation
{
	BROWSE("B"), //$NON-NLS-1$
	CHECKOUT("C"), //$NON-NLS-1$
	DELETE("D"), //$NON-NLS-1$
	EDIT("S"), //$NON-NLS-1$
	FALLBACK("F"), //$NON-NLS-1$
	GENERATE_PARMS("GP"), //$NON-NLS-1$
	GENERATE("G"), //$NON-NLS-1$
	IMPLEMENT("I"), //$NON-NLS-1$
	LOADED("TL"), //$NON-NLS-1$
	PROMOTE("P"), //$NON-NLS-1$
	RENAME("RE"), //$NON-NLS-1$
	REGRESS("X"), //$NON-NLS-1$
	TRANSFER("T"), //$NON-NLS-1$
	UPDATE_REPLACE_VERSION("UV"), //$NON-NLS-1$
	UNDEFINED(" ");

	private String code;
	private String description;
	private String pastTense;

	private static final Map<String, Operation> codeCache = new HashMap<>();
	private static final Map<String, Operation> descriptionCache = new HashMap<>();
	private static final Map<String, Operation> descriptionCacheUpperCase = new HashMap<>();

	static
	{
		for (Operation operation : Operation.values())
		{
			codeCache.put(operation.getCode(), operation);
			descriptionCache.put(operation.getDescription(), operation);
			descriptionCacheUpperCase.put(operation.getDescription().toUpperCase(), operation);
		}
	}

	/**
	 * Default constructor.
	 * 
	 * @param code
	 *            The operation code
	 */
	private Operation(String code)
	{
		this.code = code;
		this.description = initializeDescription();
		this.pastTense = initializePastTenseDescription();
	}

	/**
	 * Gets the operation code.
	 * 
	 * @return the operation code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * Gets the operation description.
	 * 
	 * @return The operation description or the operation code if the operation code is unrecognized.
	 */
	public String getDescription()
	{
		return description;
	}
	
	public String getPastTenseDescription()
	{
		return pastTense;
	}

	/**
	 * Initializes the operation description.
	 * 
	 * @return The operation description
	 */
	private String initializeDescription()
	{
		switch (getCode())
		{
			case "B" : //$NON-NLS-1$
				return "Browse";
			case "C" : //$NON-NLS-1$
				return "Checkout";
			case "D" : //$NON-NLS-1$
				return "Delete";
			case "S" : //$NON-NLS-1$
				return "Edit";
			case "F" : //$NON-NLS-1$
				return "Fallback";
			case "FB" : //$NON-NLS-1$
				return "Fallback";
			case "GP" : //$NON-NLS-1$
				return "Generate with Parms";
			case "G" : //$NON-NLS-1$
				return "Generate";
			case "I" : //$NON-NLS-1$
				return "Implement";
			case "P" : //$NON-NLS-1$
				return "Promote";
			case "X" : //$NON-NLS-1$
				return "Regress";
			case "RE" : //$NON-NLS-1$
				return "Rename";
			case "T" : //$NON-NLS-1$
				return "Transfer";
			case "TL" : //$NON-NLS-1$
				return "Loaded";
			case "UV" : //$NON-NLS-1$
				return "Update Replace Version";
			default :
				return this.getCode();
		}
	}
	
	private String initializePastTenseDescription()
	{
		switch (getCode())
		{
			case "B" : //$NON-NLS-1$
				return "Browsed";
			case "C" : //$NON-NLS-1$
				return "Checked Out";
			case "D" : //$NON-NLS-1$
				return "Deleted";
			case "S" : //$NON-NLS-1$
				return "Edited";
			case "F" : //$NON-NLS-1$
				return "Fellback";
			case "FB" : //$NON-NLS-1$
				return "Fellback";
			case "GP" : //$NON-NLS-1$
				return "Generated with Parms";
			case "G" : //$NON-NLS-1$
				return "Generated";
			case "I" : //$NON-NLS-1$
				return "Implemented";
			case "P" : //$NON-NLS-1$
				return "Promoteed";
			case "X" : //$NON-NLS-1$
				return "Regressed";
			case "RE" : //$NON-NLS-1$
				return "Renamed";
			case "T" : //$NON-NLS-1$
				return "Transfered";
			case "TL" : //$NON-NLS-1$
				return "Loaded";
			case "UV" : //$NON-NLS-1$
				return "Updated Replace Version";
			default :
				return this.getCode();
		}
	}

	/**
	 * Gets an operation by operation code.
	 * 
	 * @param code
	 *            The operation code
	 * @return The operation enum
	 */
	public static Operation getByCode(String code)
	{
		return codeCache.get(code);
	}

	/**
	 * Gets an operation by description.
	 * 
	 * @param description
	 *            The operation description
	 * @return The operation enum
	 */
	public static Operation getByDescription(String description)
	{
		return descriptionCache.get(description);
	}

	/**
	 * Gets an operation by description.
	 * 
	 * @param description
	 *            The operation description
	 * @param ignoreCase
	 *            whether or not to ignore the operation description case
	 * @return The operation enum
	 */
	public static Operation getByDescription(String description, boolean ignoreCase)
	{
		if (ignoreCase)
		{
			return description == null ? null : descriptionCacheUpperCase.get(description.toUpperCase());
		}
		else
		{
			return getByDescription(description);
		}
	}
}
