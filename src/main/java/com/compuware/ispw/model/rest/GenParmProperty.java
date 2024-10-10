package com.compuware.ispw.model.rest;

public class GenParmProperty {
	private String category;
	private String name;
	private String value;
	
	

	public GenParmProperty(String category, String name, String value) {
		super();
		this.category = category;
		this.name = name;
		this.value = value;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
