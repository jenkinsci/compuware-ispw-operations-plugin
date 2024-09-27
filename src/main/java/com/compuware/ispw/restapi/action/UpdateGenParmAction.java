package com.compuware.ispw.restapi.action;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.compuware.ispw.model.rest.GenParmProperty;
import com.compuware.ispw.model.rest.UpdateGenPam;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.WebhookToken;
import com.compuware.ispw.restapi.util.RestApiUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateGenParmAction extends SetInfoPostAction {
	private static final String contextPath = "/ispw/{srid}/updateGenerateWithParam";

	public UpdateGenParmAction(PrintStream logger) {
		super(logger);
	}

	@Override
	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody, WebhookToken webhookToken) {
		IspwRequestBean bean = new IspwRequestBean();
		IspwContextPathBean ispwContextPathBean = new IspwContextPathBean();
		ispwContextPathBean.setSrid(srid);
		bean.setIspwContextPathBean(ispwContextPathBean);
		String path = contextPath.replace("{srid}", srid);
		bean.setContextPath(RestApiUtils.cleanContextPath(path));

		ObjectMapper objectMapper = new ObjectMapper();
		UpdateGenPam updateGenPam = new UpdateGenPam();
		List<GenParmProperty> updateDetails = new ArrayList<GenParmProperty>();
		try {
			JsonNode node = objectMapper.readTree(ispwRequestBody);
			updateGenPam.setContainerId(node.get("containerId").asText());
			updateGenPam.setContainerType(node.get("containerType").asText());
			updateGenPam.setTaskId(node.get("taskId").asText());
			String setId= node.get("setId").asText();
	        setId = setId.replaceAll("^\"|\"$", "");  // Remove extra quotes from start and end
			updateGenPam.setSetId(setId);
			JsonNode inputNode = node.get("inputs");
			Iterator<Entry<String, JsonNode>> fieldsIterator = inputNode.fields();
			GenParmProperty genParmProperty = null;
			while (fieldsIterator.hasNext()) {
				Entry<String, JsonNode> field = fieldsIterator.next();
				String fieldName = field.getKey();
				String fieldValue = field.getValue().toString();
				if (fieldName.startsWith("dynamicField_") && fieldName.contains(".")) {
					String modifiedString = fieldName.replaceFirst("dynamicField_", "");
					String[] parts = modifiedString.split("\\.");
					genParmProperty = new GenParmProperty(parts[0], parts[1], fieldValue);
					updateDetails.add(genParmProperty);
				}
			}
			updateGenPam.setUpdateDetails(updateDetails);
			bean.setJsonRequest(objectMapper.writeValueAsString(updateGenPam));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bean;
	}

	@Override
	public void startLog(PrintStream logger, IspwContextPathBean ispwContextPathBean, Object jsonObject) {

	}

	@Override
	public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson) {
		return null;
	}

}
