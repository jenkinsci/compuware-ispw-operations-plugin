package com.compuware.ispw.restapi.action;

import java.io.PrintStream;

import com.compuware.ispw.model.rest.SetInfo;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.WebhookToken;
import com.compuware.ispw.restapi.util.Operation;

/**
 * A generic SetInfo post action
 * 
 * @author Sam Zhou
 *
 */
public abstract class SetInfoPostAction extends GenericPostAction<SetInfo> {

	public SetInfoPostAction(PrintStream logger) {
		super(logger);
	}

	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody, WebhookToken webhookToken,
			String contextPath) {
		return super.getIspwRequestBean(SetInfo.class, srid, ispwRequestBody, webhookToken, contextPath);
	}
	
	/**
	 * Returns the ISPW Operation enumeration object for this action<br>
	 * <b><i>May not be implemented for all actions<b><i>
	 * 
	 * @return the Operation
	 */
	public Operation getIspwOperation()
	{
		return Operation.UNDEFINED;
	}
}
