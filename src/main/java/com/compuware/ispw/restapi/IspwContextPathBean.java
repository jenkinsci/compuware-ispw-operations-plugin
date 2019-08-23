package com.compuware.ispw.restapi;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A Java bean that holds any possible context path related parameters
 * Note: this method is using reflection to set object properties, so if you need some transformation
 * 		you need do it in get() method instead of set() method.
 * 
 * @author Sam Zhou
 *
 */
public class IspwContextPathBean {
	private String application;
	private String srid;
	private String assignmentId;
	private String releaseId;
	private String requestId;
	private String setId;
	private String level;
	private String taskId;
	private String mname;
	private String mtype;
	private String action;
	private String approver;
	private String checkout = Constants.FALSE;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}	

	public String getSrid() {
		return srid;
	}
	public void setSrid(String srid) {
		this.srid = srid;
	}
	public String getAssignmentId() {
		return assignmentId;
	}
	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
	}
	public String getReleaseId() {
		return releaseId;
	}
	public void setReleaseId(String releaseId) {
		this.releaseId = releaseId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getSetId() {
		return setId;
	}
	public void setSetId(String setId) {
		this.setId = StringUtils.trimToEmpty(setId).toUpperCase();
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getMname() {
		return mname;
	}
	public void setMname(String mname) {
		this.mname = mname;
	}
	public String getMtype() {
		return mtype;
	}
	public void setMtype(String mtype) {
		this.mtype = mtype;
	}
	
	public String getCheckout() {
		return checkout;
	}

	public void setCheckout(String checkout) {
		this.checkout = checkout;
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = StringUtils.trimToEmpty(action).toLowerCase();
	}

	public String getApprover()
	{
		return approver;
	}

	public void setApprover(String approver)
	{
		this.approver = StringUtils.trimToEmpty(approver).toUpperCase();
	}
	
	public String getApplication()
	{
		return this.application;
	}
	
	public void setApplication(String application)
	{
		this.application = application;
	}
}
