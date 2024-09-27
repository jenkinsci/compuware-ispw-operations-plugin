package com.compuware.ispw.model.rest;

import java.util.List;

public class UpdateGenPam {
	private String containerId;
	private String containerType;
	private String taskId;
	private String setId;
	private List<GenParmProperty> updateDetails;

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public String getContainerType() {
		return containerType;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getSetId() {
		return setId;
	}

	public void setSetId(String setId) {
		this.setId = setId;
	}

	public List<GenParmProperty> getUpdateDetails() {
		return updateDetails;
	}

	public void setUpdateDetails(List<GenParmProperty> updateDetails) {
		this.updateDetails = updateDetails;
	}

}
