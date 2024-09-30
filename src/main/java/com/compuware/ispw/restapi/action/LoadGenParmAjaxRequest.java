package com.compuware.ispw.restapi.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.util.ReflectUtils;
import com.compuware.ispw.restapi.util.RestApiUtils;
import com.compuware.jenkins.common.configuration.CpwrGlobalConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Extension
public class LoadGenParmAjaxRequest implements UnprotectedRootAction {

	private StandardCredentials cesCredentials;
	private String credentialsId;
	private static final String[] defaultProps = new String[] { "taskId", "containerType", "containerId" };
	private static final String contextPath = "/ispw/{srid}/generateWithParam";
	private @Nonnull String requestBody = StringUtils.EMPTY;
	private @Nonnull String url = StringUtils.EMPTY;
	private @Nonnull String token = StringUtils.EMPTY;

	public String getUrlName() {
		return "loadGenParm"; // Base URL for your plugin
	}

	// AJAX endpoint to get the form based on the action ID
	public void doGetForm(StaplerRequest request, StaplerResponse response) throws IOException {
		JSONObject resultJson = new JSONObject();
		String reqBody = request.getParameter("param1");
		credentialsId = request.getParameter("param3");
		String connectionId = request.getParameter("param2");
		String cesUrl = RestApiUtils.getCesUrl(connectionId, null);
		String cesIspwToken = RestApiUtils.getCesToken(credentialsId, null);
		this.cesCredentials = RestApiUtils.getCesCredentials(credentialsId, null);
		List<String> pathTokens = Arrays.asList(defaultProps);
		String cesIspwHost = RestApiUtils.getIspwHostLabel(connectionId);
		IspwRequestBean ispwRequestBean = getIspwRequestBean(cesIspwHost, reqBody, contextPath, pathTokens);
		this.url = cesUrl + ispwRequestBean.getContextPath(); // CES URL
		this.requestBody = ispwRequestBean.getJsonRequest();
		this.token = cesIspwToken; // CES TOKEN
		
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			// Create an HttpPost request
		  HttpPost postRequest = new HttpPost(url);
			// Set the authorization header
			postRequest.setHeader("Authorization", token);
			postRequest.setHeader("Content-Type", "application/json");
			// Set the request body
			StringEntity entity = new StringEntity(requestBody);
			postRequest.setEntity(entity);
			// Execute the request
			HttpResponse response1 = httpClient.execute(postRequest);
			// Get the response content
			String responseBody = EntityUtils.toString(response1.getEntity());
			// Print the response
			if(response1.getStatusLine().getStatusCode() ==200) {
			ObjectMapper objectMapper = new ObjectMapper();
			// Parse JSON response into JsonNode
			JsonNode rootNode = objectMapper.readTree(responseBody);
			resultJson.put(defaultProps[0], ispwRequestBean.getIspwContextPathBean().getTaskId());
			resultJson.put(defaultProps[1], ispwRequestBean.getIspwContextPathBean().getContainerType());
			resultJson.put(defaultProps[2], ispwRequestBean.getIspwContextPathBean().getContainerId());
			String setIdValue = rootNode.get("setId").asText();
		    // Remove leading and trailing quotes, if present
		    setIdValue = setIdValue.replaceAll("^\"|\"$", "");
			resultJson.put("setId", setIdValue);
			JsonNode xmlN = rootNode.path("xml");
			String xmlResponse = cleanXmlString(xmlN.toString().trim());
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false); // Disable namespace awareness
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8)));
			document.getDocumentElement().normalize();
			
            JsonNode entries = rootNode.path("defaults").path("entry");
            Map<String, String> defaultVMap = new HashMap<>();
            for (JsonNode entry : entries) {
                String key = entry.path("key").asText();
                String value = entry.path("value").asText();
                defaultVMap.put(key, value);
            }
			// Create XPath to search for tns:field elements
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			String expression = "//Dialog/area/field";
			XPathExpression xPathExpression = xpath.compile(expression);
			NodeList nodeList = (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = null;
	     
			for (int i = 0; i < nodeList.getLength(); i++) {
				jsonObject = new JSONObject();
				Node fieldNode = nodeList.item(i);
				// Extract attributes
				String id = fieldNode.getAttributes().getNamedItem("id").getNodeValue();
				String label = fieldNode.getAttributes().getNamedItem("label").getNodeValue();
				String type = fieldNode.getAttributes().getNamedItem("type").getNodeValue();
				String target = fieldNode.getAttributes().getNamedItem("target").getNodeValue();

				jsonObject.put("id", id);
				jsonObject.put("name",label);
				jsonObject.put("type",type);
				jsonObject.put("target", target);
				jsonObject.put("value",defaultVMap.get(id));
			    jsonArray.add(jsonObject);
			}
			resultJson.put("dataArr", jsonArray);
			response.setContentType("application/json");
			response.getWriter().write(resultJson.toString());
			}else {
				response.setContentType("application/json");
				response.getWriter().write(responseBody);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String cleanXmlString(String xmlResponse) {
		// Remove the BOM or any non-printable characters at the start
		if (xmlResponse.startsWith("\"") && xmlResponse.endsWith("\"")) {
			xmlResponse = xmlResponse.substring(1, xmlResponse.length() - 1);
		}
		xmlResponse = xmlResponse.replaceAll("[^\\x20-\\x7E]", "").trim();
		xmlResponse = xmlResponse.replace("\\r\\n", " ").trim();

		// Ensure that XML declarations and attributes are properly formatted
		xmlResponse = xmlResponse.replace("\\\"", "\""); // Remove unnecessary escape characters
		return xmlResponse;
	}

	public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody, String contextPath,
			List<String> pathTokens) {

		IspwRequestBean bean = new IspwRequestBean();
		Map<String, Object> jsonBody = new HashMap<>();
		IspwContextPathBean ispwContextPathBean = new IspwContextPathBean();
		ispwContextPathBean.setSrid(srid);
		bean.setIspwContextPathBean(ispwContextPathBean);

		String path = contextPath.replace("{srid}", srid);

		String[] lines = ispwRequestBody.split("\n");
		for (String line : lines) {
			line = StringUtils.trimToEmpty(line);

			if (line.startsWith("#")) {
				continue;
			}

			int indexOfEqualSign = line.indexOf("=");
			if (indexOfEqualSign != -1) {
				String name = StringUtils.trimToEmpty(line.substring(0, indexOfEqualSign));
				String value = StringUtils.trimToEmpty(line.substring(indexOfEqualSign + 1, line.length()));

				if (StringUtils.isNotBlank(value)) {
					if (pathTokens.contains(name)) {
						path = path.replace("{" + name + "}", value);
						ReflectUtils.reflectSetter(ispwContextPathBean, name, value);
						jsonBody.put(name, value);
					}
				}
			}
		}
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String jsonString = objectMapper.writeValueAsString(jsonBody);
			bean.setJsonRequest(jsonString);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		bean.setContextPath(RestApiUtils.cleanContextPath(path));
		return bean;

	}

	@Override
	public String getIconFileName() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	
}
