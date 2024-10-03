package com.compuware.ispw.restapi.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.util.ReflectUtils;
import com.compuware.ispw.restapi.util.RestApiUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Extension
public class LoadGenParmAjaxRequest implements UnprotectedRootAction {

	private StandardCredentials cesCredentials;
	private String credentialsId;
	private static final String[] defaultProps = new String[] { "taskId", "containerType", "containerId" };
	private static final String contextPath = "/ispw/{srid}/tasks/{taskId}/generateWithParms?containerId={containerId}&containerType={containerType}";
	private @Nonnull String requestBody = StringUtils.EMPTY;
	private @Nonnull String url = StringUtils.EMPTY;
	private @Nonnull String token = StringUtils.EMPTY;

	public String getUrlName() {
		return "loadGenParm"; // Base URL for your plugin
	}

	// AJAX endpoint to get the form based on the action ID
	public void doGetForm(StaplerRequest request, StaplerResponse response) throws IOException, URISyntaxException {
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
		ObjectMapper objectMapper = new ObjectMapper();
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			// Create an HttpGet request
			HttpGet httpGet = new HttpGet(this.url);
			httpGet.setHeader("Authorization", token);
			try (CloseableHttpResponse response1 = httpClient.execute(httpGet)) {
				String responseBody = EntityUtils.toString(response1.getEntity());
				if (response1.getStatusLine().getStatusCode() == 200 && !responseBody.isEmpty()) {
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
					Document document = builder
							.parse(new ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8)));
					document.getDocumentElement().normalize();

					JsonNode entries = rootNode.path("datagroupInfo").path("entry");
					Map<String, String> defaultVMap = new HashMap<>();
					for (JsonNode entry : entries) {
						String key = entry.path("key").asText();
						JsonNode object = entry.path("value");
						if (object.isTextual()) {
							Map<String, String> map = parseKeyValuePairs(object.asText());
							defaultVMap.put(key + "." + map.get("field"), map.get("defaultVal"));
						}
						if (object.isArray()) {
							for (JsonNode node : object) {
								Map<String, String> map1 = parseKeyValuePairs(node.asText());
								defaultVMap.put(key +"."+ map1.get("field"), map1.get("defaultVal"));

							}
						}
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
						String value = fieldNode.getAttributes().getNamedItem("value").getNodeValue();
						String[] arr= splitVar(value);
						jsonObject.put("id", id);
						jsonObject.put("name", label);
						jsonObject.put("type", type);
						jsonObject.put("target", target);
						String key = arr[0]+ "."+id;
						if(defaultVMap.containsKey(key) && defaultVMap.get(key)!=null) {
							jsonObject.put("value", defaultVMap.get(key));
						}
						jsonArray.add(jsonObject);
					}
					resultJson.put("dataArr", jsonArray);
					response.setContentType("application/json");
					response.getWriter().write(resultJson.toString());
				} else if (response1.getStatusLine().getStatusCode() != 200) {
					JSONObject error = new JSONObject();
					error.put("status", "error");
					JsonNode rootNode = objectMapper.readTree(responseBody);
					error.put("message", rootNode.get("message").asText());
					response.getWriter().write(error.toString());
				}

				else if (responseBody.isEmpty()) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.setContentType("application/json");
					response.getWriter().write("{\"status\": \"error\", \"message\": \"No data found\"}");
				}

			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.setContentType("application/json");
				response.getWriter().write("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
			}
		}
	}
	
	
	private String[] splitVar(String name)
	{
		if (name == null || name.length() == 0)
		{
			return null;
		}
		// may want to check for braces ... name may be stored in another var
		// if name starts and ends with braces, call resolveValue
		String cleanedInput = name.trim().replaceAll("[{}]", "");
		String[] parts = new String[2];
		String[] split = cleanedInput.split("\\.");
		if (split.length < 2)
		{
			parts[0] = "DIALOG";
			parts[1] = split[0];
		}
		else
		{
			parts[0] = split[0];
			parts[1] = split[1];
		}

		return parts;
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

	public String fixJson(String input) {
		// Add double quotes around field names and string values using a simple regex
		return input.replaceAll("(\\w+)", "\"$1\"") // Wrap field names and values with double quotes
				.replaceAll(":\"(\\w+)\"", ":\"$1\""); // Only wrap field names, not numeric values
	}

	public static Map<String, String> parseKeyValuePairs(String input) {
		// Create a Map to store the key-value pairs
		Map<String, String> map = new HashMap<>();
		// Remove the curly braces
		String cleanedInput = input.trim().replaceAll("[{}]", "");
		// Split the string by commas to separate key-value pairs
		String[] keyValuePairs = cleanedInput.split(",\\s*");
		for (String pair : keyValuePairs) {
			// Split each pair by the first colon to separate key and value
			String[] keyValue = pair.split(":", 2); // limit to 2 parts

			if (keyValue.length == 2) {
				// Trim whitespace and add to the map
				String key = keyValue[0].trim();
				String value = keyValue[1].trim();

				// Store in the map
				map.put(key, value);
			}
		}

		return map;
	}

}
