package com.compuware.ispw.restapi.action;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.compuware.ces.communications.service.data.EventCallback;
import com.compuware.ces.model.BasicAuthentication;
import com.compuware.ces.model.HttpHeader;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.WebhookToken;
import com.compuware.ispw.restapi.util.ReflectUtils;
import com.compuware.ispw.restapi.util.RestApiUtils;

/**
 * A generic type post action
 * 
 * @author Sam Zhou
 *
 */
public abstract class GenericPostAction<T> extends AbstractPostAction {

	public GenericPostAction(PrintStream logger) {
		super(logger);
	}
	
	public IspwRequestBean getIspwRequestBean(Class<T> postClazz, String srid, String ispwRequestBody,
			WebhookToken webhookToken, String contextPath) {

		IspwRequestBean bean = new IspwRequestBean();

		IspwContextPathBean ispwContextPathBean = new IspwContextPathBean();
		ispwContextPathBean.setSrid(srid);
		bean.setIspwContextPathBean(ispwContextPathBean);
		
		String path = contextPath.replace("{srid}", srid);
		List<String> allParams = RestApiUtils.listAllParams(path);
		
		T postObject = ReflectUtils.newInstance(postClazz);
		
		bean.setJsonObject(postObject);

		boolean hasEvent = false;
		ArrayList<EventCallback> events = new ArrayList<EventCallback>();
		EventCallback event = new EventCallback();
		events.add(event);

		String[] lines = ispwRequestBody.split("\n");
		for (String line : lines) {
			line = StringUtils.trimToEmpty(line);
			
			if(line.startsWith("#")) {
				continue;
			}
			
			int indexOfEqualSign = line.indexOf("=");
			if (indexOfEqualSign != -1) {
				String name = StringUtils.trimToEmpty(line.substring(0, indexOfEqualSign));
				String value =
						StringUtils
								.trimToEmpty(line.substring(indexOfEqualSign + 1, line.length()));

				if (StringUtils.isNotBlank(value)) {
					if (name.equals(httpHeaders)) {
						ArrayList<HttpHeader> httpHeaders = RestApiUtils.toHttpHeaders(value);
						if (!httpHeaders.isEmpty()) {
							ReflectUtils.reflectSetter(postObject, "httpHeaders", value);
						}
					} else if (name.equals(credentials)) {
						BasicAuthentication auth = RestApiUtils.toBasicAuthentication(value);
						if (auth != null) {
							ReflectUtils.reflectSetter(postObject, "credentials", auth);
						}
					} else if (name.equals(eventsName)) {
						hasEvent = true;
						event.setName(value);
					} else if (name.equals(eventsMethod)) {
						hasEvent = true;
						event.setMethod(value);
					} else if (name.equals(eventsBody)) {
						hasEvent = true;
						event.setBody(value);
					} else if (name.equals(eventsHttpHeaders)) {
						ArrayList<HttpHeader> httpHeaders = RestApiUtils.toHttpHeaders(value);
						if (!httpHeaders.isEmpty()) {
							hasEvent = true;
							event.setHttpHeaders(httpHeaders);
						}
					} else if (name.equals(eventsCredentials)) {
						BasicAuthentication auth = RestApiUtils.toBasicAuthentication(value);
						if (auth != null) {
							hasEvent = true;
							event.setCredentials(auth);
						}
					} else {

						//if the context path contains the name, replace the {name} with the true value
						if(allParams.contains(name)) 
						{
							ReflectUtils.reflectSetter(ispwContextPathBean, name, value);
							
							//Reflect getter is necessary in here because some times
							path = path.replace("{" + name + "}", ReflectUtils.reflectGetter(ispwContextPathBean, name));
						}
						
						ReflectUtils.reflectSetter(postObject, name, value); // set the rest of the
																				// SetInfo fields using
																				// reflection
					}

				}
			}
		}

		if (webhookToken != null && hasEvent) {
			event.setUrl(webhookToken.getURL());
			ReflectUtils.reflectSetter(postObject, "eventCallbacks", events);
		}

		path = path.replaceAll(",", "&taskId=");
		bean.setContextPath(RestApiUtils.cleanContextPath(path));

		JsonProcessor jsonGenerator = new JsonProcessor();
		String jsonRequest = jsonGenerator.generate(postObject);
		bean.setJsonRequest(jsonRequest);
		return bean;
	}

}
