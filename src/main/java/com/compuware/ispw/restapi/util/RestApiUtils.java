package com.compuware.ispw.restapi.util;

import static com.cloudbees.plugins.credentials.CredentialsMatchers.filter;
import static com.cloudbees.plugins.credentials.CredentialsMatchers.withId;
import static com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.compuware.ces.model.BasicAuthentication;
import com.compuware.ces.model.HttpHeader;
import com.compuware.ispw.model.changeset.DeployTargetLoadModule;
import com.compuware.ispw.model.changeset.LevelLoadLib;
import com.compuware.ispw.model.changeset.LifeCycleLoadModule;
import com.compuware.ispw.model.changeset.Program;
import com.compuware.ispw.model.changeset.ProgramList;
import com.compuware.ispw.model.rest.MessageResponse;
import com.compuware.ispw.model.rest.SetInfoResponse;
import com.compuware.ispw.model.rest.TaskInfo;
import com.compuware.ispw.model.ttt.rest.JaxbDeployTargetLoadModule;
import com.compuware.ispw.model.ttt.rest.JaxbLevelLoadLib;
import com.compuware.ispw.model.ttt.rest.JaxbLifeCycleLoadModule;
import com.compuware.ispw.model.ttt.rest.JaxbProgram;
import com.compuware.ispw.restapi.Constants;
import com.compuware.ispw.restapi.JsonProcessor;
import com.compuware.ispw.restapi.ResponseContentSupplier;
import com.compuware.jenkins.common.configuration.CpwrGlobalConfiguration;
import com.compuware.jenkins.common.configuration.HostConnection;
import hudson.AbortException;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import jenkins.model.Jenkins;

/**
 * Utilities for ISPW operations plug-in
 * 
 * @author Sam Zhou
 *
 */
public class RestApiUtils {

	private static Logger logger = Logger.getLogger(RestApiUtils.class);

	public static String join(String delimiter, String[] stringArray, boolean appendEqualSign) {
		String result = StringUtils.EMPTY;

		StringBuilder sb = new StringBuilder();
		if (stringArray != null) {
			for (String string : stringArray) {
				sb.append(string).append(appendEqualSign ? "=" : StringUtils.EMPTY)
						.append(delimiter);
			}
		}

		if (sb.length() > 0) {
			result = sb.toString();
			result = result.substring(0, result.length() - delimiter.length());
		}

		return result;
	}

	public static ArrayList<HttpHeader> toHttpHeaders(String flat) {
		ArrayList<HttpHeader> headers = new ArrayList<HttpHeader>();

		String[] nameValues = flat.split(";");
		for (String nameValue : nameValues) {
			nameValue = StringUtils.trimToEmpty(nameValue);
			if (StringUtils.isNotEmpty(nameValue)) {
				int indexOfColon = nameValue.indexOf(":");
				if (indexOfColon != -1) {
					String name = StringUtils.trimToEmpty(nameValue.substring(0, indexOfColon));
					String value =
							StringUtils.trimToEmpty(nameValue.substring(indexOfColon + 1,
									nameValue.length()));

					if (StringUtils.isNotBlank(value)) {
						HttpHeader header = new HttpHeader();
						header.setName(name);
						header.setValue(value);
						headers.add(header);
					}
				}
			}
		}

		return headers;
	}

	public static BasicAuthentication toBasicAuthentication(String flat) {
		BasicAuthentication auth = null;

		int indexOfColon = flat.indexOf(":");
		if (indexOfColon != -1) {
			String username = StringUtils.trimToEmpty(flat.substring(0, indexOfColon));
			String password =
					StringUtils.trimToEmpty(flat.substring(indexOfColon + 1, flat.length()));
			auth = new BasicAuthentication();
			auth.setUsername(username);
			auth.setPassword(password);
		}

		return auth;
	}

	public static boolean containsIgnoreCase(List<String> tokens, String anotherToken) {
		for (String token : tokens) {
			if (token.equalsIgnoreCase(anotherToken))
				return true;
		}

		return false;
	}
	
	//Fix CES bug - CWE-124094 - Get assignment/release/set task list doesn't return a JSON array ("tasks":[]) if they contains just one task
	public static String fixCesTaskListResponseJson(String responseJson) {
		String fixedResponseJson = responseJson;
		
		if(responseJson.startsWith("{\"tasks\":{")) {
			fixedResponseJson = responseJson.replace("{\"tasks\":{", "{\"tasks\":[{");
			fixedResponseJson = fixedResponseJson.replace("}}", "}]}");
		}
		
		return fixedResponseJson;
	}
	
	public static String fixCesContainerListResponseJson(String responseJson) {
		String fixedResponseJson = responseJson;
		
		if(responseJson.startsWith("{\"containers\":{")) { //$NON-NLS-1$
			fixedResponseJson = responseJson.replace("{\"containers\":{", "{\"containers\":[{"); //$NON-NLS-1$ //$NON-NLS-2$
			fixedResponseJson = fixedResponseJson.replace("}}", "}]}"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return fixedResponseJson;
	}
	
	public static String fixWorkListResponseJson(String responseJson)
	{
		String fixedResponseJson = responseJson;

		if (responseJson.startsWith("{\"workList\":{")) //$NON-NLS-1$
		{
			fixedResponseJson = responseJson.replace("{\"workList\":{", "{\"workList\":[{"); //$NON-NLS-1$ //$NON-NLS-2$
			fixedResponseJson = fixedResponseJson.replace("}}", "}]}"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return fixedResponseJson;
	}
	
	public static ListBoxModel buildConnectionIdItems(@AncestorInPath Jenkins context, @QueryParameter String connectionId,
			@AncestorInPath Item project) {
		CpwrGlobalConfiguration globalConfig = CpwrGlobalConfiguration.get();
		HostConnection[] hostConnections = globalConfig.getHostConnections();

		ListBoxModel model = new ListBoxModel();
		model.add(new Option(StringUtils.EMPTY, StringUtils.EMPTY, false));

		for (HostConnection connection : hostConnections)
		{
			boolean isSelected = false;
			if (connectionId != null)
			{
				isSelected = connectionId.matches(connection.getConnectionId());
			}

			model.add(new Option(connection.getDescription() + " [" + connection.getHostPort() + ']', //$NON-NLS-1$
					connection.getConnectionId(), isSelected));
		}

		return model;
	}
	
	/**
	 * Get the host connection
	 * 
	 * @param connectionId
	 *            The connection ID
	 * @return The host connection or a null object
	 */
	public static HostConnection getHostConnection(String connectionId) {
		CpwrGlobalConfiguration globalConfig = CpwrGlobalConfiguration.get();
		HostConnection hostConnection = globalConfig.getHostConnection(connectionId);
		return hostConnection;
	}
	
	public static String getCesUrl(String connectionId, PrintStream logger) throws AbortException {
		HostConnection hostConnection = getHostConnection(connectionId);
		RestApiUtils.assertNotNull(logger, hostConnection, "Unable to connect to host connection for connectionId: %s", connectionId);

		String cesUrl = StringUtils.trimToEmpty(hostConnection.getCesUrl());

		if (!cesUrl.toLowerCase().startsWith("http")) {
			throw new AbortException(
					"Host connection does NOT contain a valid CES URL. Please re-configure in 'Manage Jenkins | Configure System | Compuware Configurations' section; CES URL="
							+ cesUrl);
		}

		try
		{
			URL url = new URL(cesUrl);
			String protocol = url.getProtocol();
			String host = url.getHost();
			int port = url.getPort();
			
			if (port <= 0) {
				if("http".equals(protocol.toLowerCase())) {
					port = 80;
				} else if("https".equals(protocol.toLowerCase())) {
					port = 443;
				}
			}

			cesUrl = protocol + "://" + host + ":" + port;
		} catch (MalformedURLException x) {
			throw new AbortException(
					"Host connection does NOT contain a valid CES URL. Please re-configure in 'Manage Jenkins | Configure System | Compuware Configurations' section: "
							+ x.getMessage() + "; CES URL=" + cesUrl);
		}

		return cesUrl;
	}
	
	public static String getIspwHostLabel(String connectionId) {
		HostConnection hostConnection = getHostConnection(connectionId);

		String host = StringUtils.trimToEmpty(hostConnection.getHost());
		String port = StringUtils.trimToEmpty(hostConnection.getPort());

		String cesIspwHost = host + "-" + port;

		return cesIspwHost;
	}

	public static String getCesToken(String credentialsId, Item item) {
		List<StringCredentials> creds =
				filter(lookupCredentials(StringCredentials.class, item,
						ACL.SYSTEM, Collections.<DomainRequirement> emptyList()),
						withId(StringUtils.trimToEmpty(credentialsId)));

		String token = StringUtils.EMPTY;
		if (creds != null && creds.size() > 0) {
			StringCredentials cred = creds.get(0);
			token = cred.getSecret().getPlainText();
		}

		return token;
	}
	
	public static StandardCredentials getCesCredentials(String credentialsId, Item item)
	{
		StandardCredentials cred = null;
		List<StandardCredentials> creds = filter(
				lookupCredentials(StandardCredentials.class, item, ACL.SYSTEM, Collections.<DomainRequirement> emptyList()),
				withId(StringUtils.trimToEmpty(credentialsId)));
		if (!creds.isEmpty())
		{
			cred = creds.get(0);
		}
		return cred;
	}
	
	public static ListBoxModel buildCredentialsIdItems(@AncestorInPath Jenkins context, @QueryParameter String credentialsId,
			@AncestorInPath Item project)
	{
		List<StandardCredentials> creds = CredentialsProvider.lookupCredentials(StandardCredentials.class, project, ACL.SYSTEM,
				Collections.<DomainRequirement> emptyList());
		StandardListBoxModel model = new StandardListBoxModel();
		model.add(new Option(StringUtils.EMPTY, StringUtils.EMPTY, false));
		for (StandardCredentials c : creds)
		{
			boolean isSelected = false;
			if (credentialsId != null)
			{
				isSelected = credentialsId.matches(c.getId());
			}
			String description = StringUtils.trimToEmpty(c.getDescription());
			model.add(new Option(description, c.getId(), isSelected));
		}
		return model;
	}
	
	public static ListBoxModel buildIspwActionItems(
			@AncestorInPath Jenkins context, @QueryParameter String ispwAction,
			@AncestorInPath Item project) {

		String[] publishedActions = ReflectUtils.listPublishedCommands();
		
		ListBoxModel model = new ListBoxModel();

		model.add(new Option(StringUtils.EMPTY, StringUtils.EMPTY, false));

		Arrays.sort(publishedActions);

		for (String action : publishedActions) {
			boolean isSelected = false;

			if (ispwAction != null) {
				isSelected = action.matches(ispwAction);
			}

			model.add(new Option(action, action, isSelected));
		}

		return model;
	}
	
	public static String maskToken(String token) {
		
		if (isIspwDebugMode()) {
			return token;
		} else {
			StringBuffer masked =
					new StringBuffer(StringUtils.trimToEmpty(token));
			
			if (token.length() >= 8) {
				masked.setLength(0);
				String s1 = token.substring(0, 2);
				masked.append(s1);
				
				for(int i=0; i<token.length()-4; i++)
					masked.append("*");
				
				String s2 = token.substring(token.length() - 2, token.length());
				masked.append(s2);
			}

			return masked.toString();
		}
		
	}
	
	public static boolean logMessageIfAny(PrintStream logger, ResponseContentSupplier response,
			boolean isError) {
		boolean anyMessage = false;
		String jsonContent = StringUtils.trimToEmpty(response.getContent());

		if (jsonContent.startsWith("{") && jsonContent.endsWith("}")) {

			// print ISPW error message if any
			JsonProcessor jsonProcessor = new JsonProcessor();
			MessageResponse messageResp = jsonProcessor.parse(jsonContent, MessageResponse.class);

			// tidy the message
			String message =
					StringUtils.trimToEmpty(messageResp.getMessage()).replaceAll("(\\s)+", " ")
							.replaceAll("( \\.)", ".");
			
			if (StringUtils.isNotBlank(message)) {
				anyMessage = true;
				if (isError)
				{
					logger.println("Error - " + message);
				}
				else
				{
					logger.println("Message - " + message);
				}
			}
		}
		
		return anyMessage;
	}
	
	public static String getSystemProperty(String key) {
		String result = System.getProperty(key);
		return StringUtils.trimToEmpty(result);
	}
	
	public static boolean isIspwDebugMode() {
		String debugMode = getSystemProperty(Constants.ISPW_DEBUG_MODE);
		return Constants.TRUE.equalsIgnoreCase(debugMode);
	}

	/**
	 * Assert not null, abort Jenkins job
	 * 
	 * @param logger
	 *            the Jenkins log
	 * @param object
	 *            the object to be tested
	 * @param message
	 *            the debug message
	 * @param args
	 *            the debug message arguments
	 * 
	 * @throws AbortException
	 *             abort exception if null
	 */
	public static void assertNotNull(PrintStream logger, Object object, String message, Object... args) throws AbortException
	{
		if (object == null)
		{
			throw new AbortException(String.format(message, args));
		}
		else
		{
			if (RestApiUtils.isIspwDebugMode())
			{
				logger.println(String.format("%s=%s", object.getClass().getName(), object));
			}
		}
	}

	/**
	 * Assert not null, if null, abort Jenkins job
	 * 
	 * @param logger
	 *            the Jenkins log
	 * @param object
	 *            the object to be tested
	 * @param message
	 *            debug message if null
	 * 
	 * @throws AbortException
	 *             abort exception if null
	 */
	public static void assertNotNull(PrintStream logger, Object object, String message) throws AbortException
	{
		assertNotNull(logger, object, message, StringUtils.EMPTY);
	}
	
	/**
	 * List all parameters in the contextPath based on pairs of {}
	 * 
	 * @param contextPath the context path
	 * @return array of all parameters
	 */
	public static List<String> listAllParams(String contextPath) {
		List<String> queryParms = new ArrayList<String>();
		
		Pattern pattern = Pattern.compile("\\{(\\w+)\\}");
		Matcher matcher = pattern.matcher(contextPath);
		while (matcher.find()) {
			for (int i = 0; i < matcher.groupCount(); i++) {
				String key = matcher.group(i);
				String key2 = key.substring(1, key.length()-1);
				queryParms.add(key2);
			}
		}			
		
		return queryParms;
	}
	
	
	/**
	 * Found out what is the query parameter names based on pairs of {}
	 * @param contextPath the context path
	 * @return array of query parameter names
	 */
	public static List<String> listQueryParams(String contextPath) {
		List<String> queryParams = new ArrayList<String>();
		
		int index = contextPath.indexOf("?");
		if (index != -1) {
			
			String s2 = contextPath.substring(index);
			queryParams = listAllParams(s2);		
		}
		
		return queryParams;
	}
	
	/**
	 * Clean up the context path and replace unused query parameters
	 * @param contextPath the context path
	 * @return context path that is ready for submit
	 */
	public static String cleanContextPath(String contextPath) {
		String resultPath = contextPath;
		
		List<String> queryParams = listQueryParams(contextPath);
		
		contextPath = contextPath.replace("?&", "?");
		
		int index = contextPath.indexOf("?");
		if (index != -1) {
			String s1 = contextPath.substring(0, index);
			String s2 = contextPath.substring(index);
			
			for(String queryParam : queryParams) {
				s2 = s2.replace(queryParam+"={"+queryParam+"}", StringUtils.EMPTY);
			}
			
			s2 = s2.replaceAll("[&]+", "&");
			if(s2.endsWith("&")) {
				s2 = s2.substring(0, s2.length()-1);
			}

			resultPath = s1 + s2;
		}
		
		return resultPath;
	}
	
	/**
	 * Find all variables that match variable name
	 * 
	 * @param s
	 *            the string to extract the variables from
	 * @return array list of variables
	 */
	public static ArrayList<String> getVariables(String s)
	{
		String regex = "\\{\\w*\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);

		ArrayList<String> vars = new ArrayList<String>();
		while (matcher.find())
		{
			vars.add(matcher.group().replace("{", StringUtils.EMPTY).replace("}", StringUtils.EMPTY));
		}

		return vars;
	}

	/**
	 * Convert SetInfoResponse object to a ProgramList object
	 * 
	 * @param setInfoResp
	 *            the SetInfoResponse object
	 * @return a ProgramList object
	 */
	public static ProgramList convertSetInfoResp(SetInfoResponse setInfoResp)
	{
		ProgramList programList = new ProgramList();

		if (setInfoResp != null)
		{
			List<TaskInfo> taskInfos = setInfoResp.getTasks();
			if (taskInfos != null && !taskInfos.isEmpty())
			{
				for (TaskInfo taskInfo : taskInfos)
				{
					JaxbProgram jaxbProgram = taskInfo.getJaxbProgram();

					if (jaxbProgram != null)
					{
						Program program = new Program();
						program.setStream(jaxbProgram.getStream());
						program.setApplication(jaxbProgram.getApplication());
						program.setSubAppl(jaxbProgram.getSubAppl());
						program.setIsImpact(jaxbProgram.getIsImpact());
						program.setLevel(jaxbProgram.getLevel());
						program.setProgramLanguage(jaxbProgram.getProgramLanguage());
						program.setProgramName(jaxbProgram.getProgramName());
						programList.addProgram(program);

						List<JaxbLifeCycleLoadModule> jaxbLifeCycleLoads = jaxbProgram.getLifeCycleLoadModules();
						if (jaxbLifeCycleLoads != null && !jaxbLifeCycleLoads.isEmpty())
						{
							for (JaxbLifeCycleLoadModule jaxbLifeCycleLoad : jaxbLifeCycleLoads)
							{
								LifeCycleLoadModule lclm = new LifeCycleLoadModule();
								program.addLifeCycleLoadModule(lclm);

								lclm.setComponentClass(jaxbLifeCycleLoad.getComponentClass());
								lclm.setComponentType(jaxbLifeCycleLoad.getComponentType());
								lclm.setLoadLibName(jaxbLifeCycleLoad.getLoadLibName());
								lclm.setLoadModName(jaxbLifeCycleLoad.getLoadModName());

								List<JaxbLevelLoadLib> jaxbLevelLoads = jaxbLifeCycleLoad.getLoadLibConcatenation();
								for (JaxbLevelLoadLib jaxbLevelLoad : jaxbLevelLoads)
								{
									LevelLoadLib levelLoadLib = new LevelLoadLib();
									lclm.addLoadLibConcatenation(levelLoadLib);

									levelLoadLib.setLevel(jaxbLevelLoad.getLevel());
									levelLoadLib.setLoadLib(jaxbLevelLoad.getLoadLib());
								}
							}
						}

						List<JaxbDeployTargetLoadModule> jaxbDeployLoads = jaxbProgram.getDeployTargetLoadModules();
						if (jaxbDeployLoads != null && !jaxbDeployLoads.isEmpty())
						{
							for (JaxbDeployTargetLoadModule jaxbDeployLoad : jaxbDeployLoads)
							{
								DeployTargetLoadModule dtlm = new DeployTargetLoadModule();
								program.addDeployTargetLoadModule(dtlm);

								dtlm.setComponentClass(jaxbDeployLoad.getComponentClass());
								dtlm.setComponentType(jaxbDeployLoad.getComponentType());
								dtlm.setDeployEnvironment(jaxbDeployLoad.getDeployEnvironment());
								dtlm.setDeployType(jaxbDeployLoad.getDeployType());
								dtlm.setLoadLibName(jaxbDeployLoad.getLoadLibName());
								dtlm.setLoadModName(jaxbDeployLoad.getLoadModName());
								dtlm.setSubenvironment(jaxbDeployLoad.getSubenvironment());
								dtlm.setSystem(jaxbDeployLoad.getSystem());
							}
						}
					}
				}
			}
		}

		return programList;
	}
	
	/**
	 * Get runtime configuration from request body
	 * 
	 * @param ispwRequestBody the request body
	 * @return runtime configuration
	 */
	public static String getRuntimeConfig(String ispwRequestBody)
	{
		String runtimeConfig = StringUtils.EMPTY;

		if (StringUtils.isNotBlank(ispwRequestBody))
		{
			String[] lines = ispwRequestBody.split("\n");
			for (String line : lines)
			{
				line = StringUtils.trimToEmpty(line);

				if (line.startsWith("#"))
				{
					continue;
				}

				int indexOfEqualSign = line.indexOf("=");
				if (indexOfEqualSign != -1)
				{
					String name = StringUtils.trimToEmpty(line.substring(0, indexOfEqualSign));
					String value = StringUtils.trimToEmpty(line.substring(indexOfEqualSign + 1, line.length()));
					if (Constants.REQUEST_BODY_RTCONFIG.equals(name))
					{
						runtimeConfig = value;
						break;
					}
				}
			}
		}

		return runtimeConfig;
	}
}
