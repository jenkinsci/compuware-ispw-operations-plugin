/**
*  Copyright (c) 2020 Compuware Corporation. All rights reserved.
* (c) Copyright 2020,2021-2022, 2023 BMC Software, Inc.
*/
package com.compuware.ispw.restapi;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.parboiled.common.FileUtils;
import com.cloudbees.plugins.credentials.common.StandardCertificateCredentials;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.compuware.ispw.git.GitToIspwUtils;
import com.compuware.ispw.model.changeset.ProgramList;
import com.compuware.ispw.model.rest.BuildResponse;
import com.compuware.ispw.model.rest.SetInfoResponse;
import com.compuware.ispw.model.rest.TaskInfo;
import com.compuware.ispw.model.rest.TaskListResponse;
import com.compuware.ispw.model.rest.TaskResponse;
import com.compuware.ispw.restapi.action.GetSetInfoAction;
import com.compuware.ispw.restapi.action.IAction;
import com.compuware.ispw.restapi.action.SetInfoPostAction;
import com.compuware.ispw.restapi.action.SetOperationAction;
import com.compuware.ispw.restapi.action.UpdateGenParmAction;
import com.compuware.ispw.restapi.util.HttpRequestNameValuePair;
import com.compuware.ispw.restapi.util.Operation;
import com.compuware.ispw.restapi.util.ReflectUtils;
import com.compuware.ispw.restapi.util.RestApiUtils;
import com.compuware.jenkins.common.configuration.CpwrGlobalConfiguration;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;

/**
 * ISPW rest API pipeline build step
 * 
 * @author Martin d'Anjou
 * @author Sam Zhou
 */
public final class IspwRestApiRequestStep extends AbstractStepImpl {

	private @Nonnull String url = StringUtils.EMPTY;
	private boolean ignoreSslErrors = DescriptorImpl.ignoreSslErrors;
	private HttpMode httpMode = DescriptorImpl.httpMode;
	private String httpProxy = DescriptorImpl.httpProxy;
	private String validResponseCodes = DescriptorImpl.validResponseCodes;
	private String validResponseContent = DescriptorImpl.validResponseContent;
	private MimeType acceptType = DescriptorImpl.acceptType;
	private MimeType contentType = DescriptorImpl.contentType;
	private Integer timeout = DescriptorImpl.timeout;
	private String authentication = DescriptorImpl.authentication;
	private String requestBody = DescriptorImpl.requestBody;
	private List<HttpRequestNameValuePair> customHeaders = DescriptorImpl.customHeaders;
	private String outputFile = DescriptorImpl.outputFile;
	private ResponseHandle responseHandle = DescriptorImpl.responseHandle;
	private String token = DescriptorImpl.token; // modified by pmisvz0
	private StandardCredentials cesCredentials;

	// ISPW
	private String connectionId = DescriptorImpl.connectionId;
	private String credentialsId = DescriptorImpl.credentialsId;
	private String ispwAction = DescriptorImpl.ispwAction;
	private String ispwRequestBody = DescriptorImpl.ispwRequestBody;
	private Boolean consoleLogResponseBody = DescriptorImpl.consoleLogResponseBody;
	private Boolean skipWaitingForSet = DescriptorImpl.skipWaitingForSet;
    public  String serializedDynamicFields = DescriptorImpl.serializedDynamicFields; // Using Map for dynamic fields

	
    @DataBoundConstructor
    public IspwRestApiRequestStep() {
    }

    public String getUrl() {
        return url;
    }
    
	public boolean isIgnoreSslErrors() {
		return ignoreSslErrors;
	}

    public HttpMode getHttpMode() {
        return httpMode;
    }

    // ispw begin
	public String getIspwAction() {
		return ispwAction;
	}

	@DataBoundSetter
	public void setIspwAction(String ispwAction) {
		this.ispwAction = ispwAction;
	}
	

	public String getConnectionId() {
		return connectionId;
	}

	@DataBoundSetter
	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}

	public String getCredentialsId()
	{
		return credentialsId;
	}
	
	@DataBoundSetter
	public void setCredentialsId(String credentialsId) {
		this.credentialsId = credentialsId;
	}
	
	public String getIspwRequestBody() {
		return ispwRequestBody;
	}

	@DataBoundSetter
	public void setIspwRequestBody(String ispwRequestBody) {
		this.ispwRequestBody = ispwRequestBody;
	}
    
	public String getToken() {
		return token;
	}
    
	public StandardCredentials getCESCredentials()
	{
		return cesCredentials;
	}
	
    public String getHttpProxy() {
        return httpProxy;
    }

    public String getValidResponseCodes() {
        return validResponseCodes;
    }
    
    public String getValidResponseContent() {
        return validResponseContent;
    }

    public MimeType getAcceptType() {
        return acceptType;
    }
    
    public MimeType getContentType() {
        return contentType;
    }

    public Integer getTimeout() {
        return timeout;
    }

	public Boolean getSkipWaitingForSet() {
		return skipWaitingForSet;
	}

	@DataBoundSetter
	public void setSkipWaitingForSet(Boolean skipWaitingForSet) {
		this.skipWaitingForSet = skipWaitingForSet;
	}
    
    @DataBoundSetter
    public void setConsoleLogResponseBody(Boolean consoleLogResponseBody) {
        this.consoleLogResponseBody = consoleLogResponseBody;
    }

    public Boolean getConsoleLogResponseBody() {
        return consoleLogResponseBody;
    }

    public String getAuthentication() {
        return authentication;
    }

    public String getRequestBody() {
        return requestBody;
    }
    
    public List<HttpRequestNameValuePair> getCustomHeaders() {
        return customHeaders;
    }

	public String getOutputFile() {
		return outputFile;
	}

	public ResponseHandle getResponseHandle() {
		return responseHandle;
	}
	
	public String getSerializedDynamicFields() {
		return serializedDynamicFields;
	}

	@DataBoundSetter
	public void setSerializedDynamicFields(String serializedDynamicFields) {
		this.serializedDynamicFields = serializedDynamicFields;
	}

	@Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

	List<HttpRequestNameValuePair> resolveHeaders() {
		final List<HttpRequestNameValuePair> headers = new ArrayList<>();
		
		// ISPW
		headers.add(new HttpRequestNameValuePair("Content-type", MimeType.APPLICATION_JSON.toString()));
		
		if(StringUtils.containsIgnoreCase(url, Constants.HTTPS) && getCESCredentials() instanceof StandardCertificateCredentials)
		{
			StandardCertificateCredentials certificateCredentials = (StandardCertificateCredentials) getCESCredentials();				
			String base64 = StringUtils.EMPTY;
			try
			{
				base64 = new CpwrGlobalConfiguration().getCertificateString(certificateCredentials);
			}
			catch (CertificateEncodingException | KeyStoreException e)
			{
				e.printStackTrace();
			}
			String cesIspwHost = RestApiUtils.getIspwHostLabel(connectionId);
			String[] hostPort = {};
			if(cesIspwHost != null)
			{
				hostPort = cesIspwHost.split("-");
			}
			if (hostPort.length == 2)
			{
				headers.add(new HttpRequestNameValuePair("cpwr_hci_host", hostPort[0]));
				headers.add(new HttpRequestNameValuePair("cpwr_hci_port", hostPort[1]));
			}
			headers.add(new HttpRequestNameValuePair("javax.servlet.request.X509Certificate", base64));
		}
		else
		{
			headers.add(new HttpRequestNameValuePair("Authorization", getToken()));
		}

		
		if (acceptType != null && acceptType != MimeType.NOT_SET) {
			headers.add(new HttpRequestNameValuePair("Accept", acceptType.getValue()));
		}
		for (HttpRequestNameValuePair header : customHeaders) {
			String headerName = header.getName();
			String headerValue = header.getValue();
			boolean maskValue = headerName.equalsIgnoreCase("Authorization") ||
					header.getMaskValue();

			headers.add(new HttpRequestNameValuePair(headerName, headerValue, maskValue));
		}
		return headers;
	}

	@Extension
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {
		public static final boolean ignoreSslErrors =
				IspwRestApiRequest.DescriptorImpl.ignoreSslErrors;
		public static final HttpMode httpMode = IspwRestApiRequest.DescriptorImpl.httpMode;
		public static final String httpProxy = IspwRestApiRequest.DescriptorImpl.httpProxy;
		public static final String validResponseCodes =
				IspwRestApiRequest.DescriptorImpl.validResponseCodes;
		public static final String validResponseContent =
				IspwRestApiRequest.DescriptorImpl.validResponseContent;
		public static final MimeType acceptType = IspwRestApiRequest.DescriptorImpl.acceptType;
		public static final MimeType contentType = IspwRestApiRequest.DescriptorImpl.contentType;
		public static final int timeout = IspwRestApiRequest.DescriptorImpl.timeout;
		public static final Boolean quiet = IspwRestApiRequest.DescriptorImpl.quiet;
		public static final String authentication =
				IspwRestApiRequest.DescriptorImpl.authentication;
		public static final String requestBody = IspwRestApiRequest.DescriptorImpl.requestBody;
		public static final String token = IspwRestApiRequest.DescriptorImpl.token;
		public static final List<HttpRequestNameValuePair> customHeaders = Collections
				.<HttpRequestNameValuePair> emptyList();
		public static final String outputFile = "";
		public static final ResponseHandle responseHandle = ResponseHandle.STRING;

		// ISPW related
		public static final String connectionId = StringUtils.EMPTY;
		public static final String credentialsId = StringUtils.EMPTY;
		public static final String ispwAction = StringUtils.EMPTY;
		public static final String ispwRequestBody = "#The following messages are commented out to show how to use the 'Request' field.\n"
				+"#Click on the help button to the right of the screen for examples of how to populate this field based on 'Action' type\n"
				+"#\n"
				+"#For example, if you select GenerateTasksInAssignment for 'Action' field,\n"
				+"# you may populate the following properties in 'Request' field.\n"
				+"# The property value should be based on your own container ID and level.\n"
				+"#\n"
				+"#assignmentId=PLAY000313\n"
				+"#level=STG2\n";
		public static final Boolean consoleLogResponseBody =
				IspwRestApiRequest.DescriptorImpl.consoleLogResponseBody;
		public static final Boolean skipWaitingForSet = false;
		public static final String serializedDynamicFields= "";

		
        public DescriptorImpl() {
            super(Execution.class);
        }

		@Override
		public String getFunctionName() {
			return "ispwOperation";
		}

		@Override
		public String getDisplayName() {
			return "Perform a Compuware ISPW Rest API Request and return a JSON object";
		}

		// ISPW
		public ListBoxModel doFillIspwActionItems(@AncestorInPath Jenkins context, @QueryParameter String ispwAction,
				@AncestorInPath Item project) {
			return RestApiUtils.buildIspwActionItems(context, ispwAction, project);
		}

		public ListBoxModel doFillConnectionIdItems(@AncestorInPath Jenkins context, @QueryParameter String connectionId,
				@AncestorInPath Item project)
		{
			
			if(project == null) {
				//Checking Permission for admin user
				Jenkins.get().checkPermission(Jenkins.ADMINISTER);
			}
			else {
				project.checkPermission(Item.CONFIGURE);				
			}
			return RestApiUtils.buildConnectionIdItems(context,  connectionId, project);
		}
		
		public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Jenkins context, @QueryParameter String credentialsId,
				@AncestorInPath Item project) {
			if(project == null) {
				//Checking Permission for admin user
				Jenkins.get().checkPermission(Jenkins.ADMINISTER);
			}
			else {
				project.checkPermission(Item.CONFIGURE);				
			}
			return RestApiUtils.buildCredentialsIdItems(context, credentialsId, project);
		
		}
		
        public ListBoxModel doFillHttpModeItems() {
            return HttpMode.getFillItems();
        }
 
        public ListBoxModel doFillAcceptTypeItems() {
            return MimeType.getContentTypeFillItems();
        }

        public ListBoxModel doFillContentTypeItems() {
            return MimeType.getContentTypeFillItems();
        }

		public ListBoxModel doFillResponseHandleItems() {
			ListBoxModel items = new ListBoxModel();
			for (ResponseHandle responseHandle : ResponseHandle.values()) {
				items.add(responseHandle.name());
			}
			return items;
		}

        public ListBoxModel doFillAuthenticationItems(@AncestorInPath Item project,
													  @QueryParameter String url) {
            return IspwRestApiRequest.DescriptorImpl.fillAuthenticationItems(project, url);
        }

        public FormValidation doCheckValidResponseCodes(@QueryParameter String value) {
            return IspwRestApiRequest.DescriptorImpl.checkValidResponseCodes(value);
        }

	}

    public static final class Execution extends AbstractSynchronousNonBlockingStepExecution<ResponseContentSupplier> {

        @Inject
        private transient IspwRestApiRequestStep step;

		@StepContextParameter
		private transient Run<?, ?> run;
		@StepContextParameter
		private transient TaskListener listener;

		public ResponseContentSupplier runExec(HttpRequestExecution exec) throws InterruptedException, IOException {
			Launcher launcher = getContext().get(Launcher.class);
			ResponseContentSupplier supplier = null;
			if (launcher != null) {
				VirtualChannel channel = launcher.getChannel();
				if (channel != null)
					supplier = channel.call(exec);
			} else {
				supplier = exec.call();
			}

			return supplier;
		}
		
		@Override
		protected ResponseContentSupplier run() throws Exception {
			PrintStream logger = listener.getLogger();

			EnvVars envVars = getContext().get(hudson.EnvVars.class);
			File buildDirectory = run.getRootDir();
			logger.println("buildDirectory: " + buildDirectory.getAbsolutePath());
			String buildTag = envVars.get("BUILD_TAG");
			WebhookToken webhookToken = WebhookTokenManager.getInstance().get(buildTag);
			
			if(RestApiUtils.isIspwDebugMode())
				logger.println("...getting buildTag=" + buildTag + ", webhookToken=" + webhookToken);

			IAction action = ReflectUtils.createAction(step.ispwAction, logger);
			step.httpMode = action.getHttpMode();
			
			if (!ReflectUtils.isActionInstantiated(action))
			{
				String errorMsg =
						"Action:"
								+ step.ispwAction
								+ " is not implemented. Please make sure you have the correct ISPW action name.";
				logger.println(errorMsg);
				throw new IllegalStateException(new Exception(errorMsg));
			}
			
			if(RestApiUtils.isIspwDebugMode())
				logger.println("ispwAction=" + step.ispwAction + ", httpMode=" + step.httpMode);

			String cesUrl = RestApiUtils.getCesUrl(step.connectionId, logger);
			String cesIspwHost = RestApiUtils.getIspwHostLabel(step.connectionId);
			String cesIspwToken = RestApiUtils.getCesToken(step.credentialsId, run.getParent());
			this.step.cesCredentials = RestApiUtils.getCesCredentials(step.credentialsId, run.getParent());

			if (RestApiUtils.isIspwDebugMode())
				logger.println("...ces.url=" + cesUrl + ", ces.ispw.host=" + cesIspwHost
						+ ", ces.ispw.token=" + cesIspwToken);

			IspwRequestBean ispwRequestBean = null;
			FilePath buildParmPath = GitToIspwUtils.getFilePathInVirtualWorkspace(envVars, Constants.BUILD_PARAM_FILE_NAME);
			String realIspwRequestBody = StringUtils.EMPTY;
			realIspwRequestBody = action.preprocess(step.ispwRequestBody, buildParmPath, logger);
		
			if (realIspwRequestBody == null)
			{
				logger.println("The " + step.ispwAction + " operation is skipped since the build parameters cannot be captured.");
				return null;
			}
			else
			{	
				if(action instanceof UpdateGenParmAction) {
					ispwRequestBean = 	action.getIspwRequestBean(cesIspwToken, step.serializedDynamicFields, webhookToken);
	      		}else {
	      			ispwRequestBean = action.getIspwRequestBean(cesIspwHost, realIspwRequestBody, webhookToken);
	      		}
			}

			if (RestApiUtils.isIspwDebugMode())
				logger.println("ispwRequestBean=" + ispwRequestBean);

			step.url = cesUrl + ispwRequestBean.getContextPath(); // CES URL
			step.requestBody = ispwRequestBean.getJsonRequest();
			step.token = cesIspwToken; // CES TOKEN

			// This is a generated code for Visual Studio Code - REST Client
			if (Boolean.TRUE.equals(step.consoleLogResponseBody)) {
				logger.println();
				logger.println();
				logger.println("### [" + step.ispwAction + "] - " + "RFC 2616");
				logger.println();
				logger.println(step.httpMode + " " + step.url + " HTTP/1.1");
				logger.println("Content-type: "
						+ MimeType.APPLICATION_JSON.getContentType().toString());
				logger.println("Authorization: " + RestApiUtils.maskToken(step.token));
				logger.println("");
				logger.println(step.requestBody);
				logger.println();
				logger.println("###");
				logger.println();
				logger.println();
			}

			ArrayList<String> variables = RestApiUtils.getVariables(step.url);
			if (!variables.isEmpty())
			{
				String errorMsg = "Action failed, need to define the following: " + variables;
				logger.println(errorMsg);
				throw new IllegalStateException(new Exception(errorMsg));
			}
			
			logger.println("Starting ISPW Operations Plugin");
			action.startLog(logger, ispwRequestBean.getIspwContextPathBean(), ispwRequestBean.getJsonObject());

			HttpRequestExecution exec =
					HttpRequestExecution.from(step, listener, this);
			exec.setCESCredentials(this.step.cesCredentials);
			ResponseContentSupplier supplier = runExec(exec);
			if (supplier == null)
			{
				String errorMsg = "Supplier is null. Please verify the pipeline script is structured correctly.";
				logger.println(errorMsg);
				throw new IllegalStateException(new Exception(errorMsg));
			}
			else if (supplier.getAbortStatus() && !supplier.getAbortMessage().isEmpty())
			{
				throw new AbortException(supplier.getAbortMessage());
			}
			
			String responseJson = supplier.getContent();
			if (RestApiUtils.isIspwDebugMode())
				logger.println("responseJson=" + responseJson);

			Object respObject = action.endLog(logger, ispwRequestBean, responseJson);

			// Ouptut TTT change set if webhook callback and record TTT
			if (webhookToken != null)
			{
				if ((action instanceof GetSetInfoAction && respObject instanceof SetInfoResponse)
						&& StringUtils.isNotBlank(ispwRequestBean.getIspwContextPathBean().getLevel()))
				{
					SetInfoResponse setInfoResp = (SetInfoResponse) respObject;

					if (!saveTttChangeSet(logger, envVars, setInfoResp))
					{
						// try another way to save because workspace is not available
						if (run instanceof WorkflowRun)
						{
							ProgramList programList = RestApiUtils.convertSetInfoResp(setInfoResp);
							String tttJson = programList.toString();
							
							WorkflowRun workflowRun = (WorkflowRun) run;
							File rootDir = workflowRun.getRootDir();

							File tttChangeSet = new File(rootDir, "../../" + Constants.TTT_CHANGESET); //$NON-NLS-1$
							if (tttChangeSet.exists())
							{
								logger.println("Deleting the old changed program list at " //$NON-NLS-1$
										+ tttChangeSet.getCanonicalPath());
								boolean deleteSuccess = tttChangeSet.delete(); //NOSONAR
								
								if(deleteSuccess)
								{
									logger.println("delete successfully");
								} else {
									logger.println("failed to delete");
								}
							}

							logger.println("Saving the changed program list to " + tttChangeSet.getCanonicalPath()); //$NON-NLS-1$
							FileUtils.writeAllText(tttJson, tttChangeSet);
						}
					}
				}

				return supplier;
			}
			
			if(Boolean.TRUE.equals(step.skipWaitingForSet))
			{
				logger.println("Skip waiting for the completion of the set for this job...");
			}
			
			// polling status if no webhook, we know webhookToken == null at this point
			if (!step.skipWaitingForSet) {
				String setId = StringUtils.EMPTY;
				if (respObject instanceof TaskResponse)
				{
					TaskResponse taskResp = (TaskResponse) respObject;
					setId = taskResp.getSetId();
				}
				else if (respObject instanceof BuildResponse)
				{
					BuildResponse buildResp = (BuildResponse) respObject;
					setId = buildResp.getSetId();
				}
				if (StringUtils.isNotBlank(setId)
						&& (respObject instanceof TaskResponse || respObject instanceof BuildResponse))
				{
					HashSet<String> set = new HashSet<>();
					SetInfoResponse finalSetInfoResp = null;
					int i = 0;
					boolean isSetHeld = false;
					String setState = "Unknown";
					for (; i < 60; i++) {
						Thread.sleep(Constants.POLLING_INTERVAL);
						HttpRequestExecution poller = HttpRequestExecution
								.createPoller(setId, step, listener, this);
						ResponseContentSupplier pollerSupplier = runExec(poller);
						if (pollerSupplier == null)
						{
							String errorMsg = "ResponseContentSupplier for polling is null. Please verify that the pipeline script is structured correctly.";
							logger.println(errorMsg);
							throw new IllegalStateException(new Exception(errorMsg));
						}
						String pollingJson = pollerSupplier.getContent();

						JsonProcessor jsonProcessor = new JsonProcessor();
						SetInfoResponse setInfoResp =
								jsonProcessor.parse(pollingJson, SetInfoResponse.class);
						setState = StringUtils.trimToEmpty(setInfoResp.getState());
						if (!set.contains(setState))
						{
							logger.println("Set " + setInfoResp.getSetid() + " status - "
									+ setState);
							set.add(setState);

							if (setState.equals(Constants.SET_STATE_CLOSED) || setState.equals(Constants.SET_STATE_COMPLETE))
							{
								logger.println("ISPW: Action " + step.ispwAction + " completed");
								
								IspwContextPathBean ispwContextPathBean = ispwRequestBean.getIspwContextPathBean();
								if (ispwContextPathBean != null && StringUtils.isNotBlank(ispwContextPathBean.getLevel()))
								{
									String taskLevel = ispwContextPathBean.getLevel();
									HttpRequestExecution poller1 = HttpRequestExecution.createPoller(setId, taskLevel,
											step, listener, this);
									ResponseContentSupplier pollerSupplier1 = runExec(poller1);
									if (pollerSupplier1 == null)
									{
										String errorMsg = "ResponseContentSupplier for TTT file information is null. Please verify that the pipeline script is structured correctly.";
										logger.println(errorMsg);
										throw new IllegalStateException(new Exception(errorMsg));
									}
									String pollingJson1 = pollerSupplier1.getContent();

									JsonProcessor jsonProcessor1 = new JsonProcessor();
									finalSetInfoResp = jsonProcessor1.parse(pollingJson1,
											SetInfoResponse.class);
									if (finalSetInfoResp != null && finalSetInfoResp.getTasks() != null)
									{
										StringBuilder taskNames = new StringBuilder();
										finalSetInfoResp.getTasks().forEach(task -> taskNames.append(task.getModuleName() + ", "));
										logger.println("ISPW tasks: " + taskNames.substring(0, taskNames.lastIndexOf(",")));
									}
									
									saveTttChangeSet(logger, envVars, finalSetInfoResp);
								}
								
								break;
							}
							else if (Constants.SET_STATE_FAILED.equalsIgnoreCase(setState))
							{
								String actionName = ispwRequestBean.getIspwContextPathBean().getAction();
								if (StringUtils.isBlank(actionName))
								{
									actionName = action.getClass().getName();
								}

								if (StringUtils.isNotBlank(actionName))
								{
									logger.println(String.format("ISPW: Set " + setId + " - action [%s] failed", actionName));
								}
								
								break;
							}
							else if (Constants.SET_STATE_TERMINATED.equalsIgnoreCase(setState)
									&& SetOperationAction.SET_ACTION_TERMINATE.equalsIgnoreCase(ispwRequestBean.getIspwContextPathBean().getAction()))
							{
								logger.println("ISPW: Set " + setId + " - successfully terminated");
								break;
							}
							else if (Constants.SET_STATE_HELD.equalsIgnoreCase(setState)
									&& SetOperationAction.SET_ACTION_HOLD.equalsIgnoreCase(ispwRequestBean.getIspwContextPathBean().getAction()))
							{
								logger.println("ISPW: Set " + setId + " - successfully held");
								isSetHeld = true;
								break;
							}
							else if (Constants.SET_STATE_HELD.equalsIgnoreCase(setState)
									&& SetOperationAction.SET_ACTION_UNLOCK.equalsIgnoreCase(ispwRequestBean.getIspwContextPathBean().getAction()))
							{
								logger.println("ISPW: Set " + setId + " - successfully unlocked.  Set is currently held.");
								break;
							}
							else if ((Constants.SET_STATE_RELEASED.equalsIgnoreCase(setState)
									|| Constants.SET_STATE_WAITING_LOCK.equalsIgnoreCase(setState))
									&& SetOperationAction.SET_ACTION_RELEASE.equalsIgnoreCase(ispwRequestBean.getIspwContextPathBean().getAction()))
							{
								logger.println("ISPW: Set " + setId + " - successfully released");
								break;
							}
							
						}
					}

					if (i == Constants.POLLING_COUNT) {
						logger.println("Warn - max timeout reached");
						return supplier;
					}

					// Follow with post set execution logging for the tasks
					if (!isSetHeld)
					{
						if (respObject instanceof BuildResponse)
						{
							buildActionTaskInfoLogger(setId, setState, logger, respObject);
						}
						else if (finalSetInfoResp != null)
						{
							logActionResults(finalSetInfoResp, action, logger);
						}
					}
				}
			}

			return supplier;
		}

		private boolean saveTttChangeSet(PrintStream logger, EnvVars envVars, SetInfoResponse setInfoResp)
		{
			ProgramList programList = RestApiUtils.convertSetInfoResp(setInfoResp);

			String tttJson = programList.toString();
			if (Boolean.TRUE.equals(step.consoleLogResponseBody))
			{
				logger.println("tttJson=" + tttJson); //$NON-NLS-1$
			}

			FilePath tttChangeSet = GitToIspwUtils.getFilePathInVirtualWorkspace(envVars, Constants.TTT_CHANGESET);

			try
			{
				if (tttChangeSet != null)
				{
					if (tttChangeSet.exists())
					{
						logger.println("Deleting the old changed program list at " + tttChangeSet.getRemote()); //$NON-NLS-1$
						tttChangeSet.delete();
					}

					logger.println("Saving the changed program list to " + tttChangeSet.getRemote()); //$NON-NLS-1$
					tttChangeSet.write(tttJson, Constants.UTF_8);

					return true;
				}
				else
				{
					return false;
				}
			}
			catch (Exception x)
			{
				x.printStackTrace();
				return false;
			}
		}
		
		private void buildActionTaskInfoLogger(String setId, String setState, PrintStream logger, Object respObject)
				throws InterruptedException, IOException, RuntimeException
		{
			if (setState.equals(Constants.SET_STATE_CLOSED) || setState.equals(Constants.SET_STATE_COMPLETE)) 
			{
				
				Thread.sleep(Constants.POLLING_INTERVAL);
				HttpRequestExecution poller = HttpRequestExecution.createTaskInfoPoller(setId, step, listener, this);

				ResponseContentSupplier pollerSupplier = runExec(poller);
				String pollingJson = pollerSupplier.getContent();

				JsonProcessor jsonProcessor = new JsonProcessor();
				TaskListResponse taskListResp = jsonProcessor.parse(pollingJson, TaskListResponse.class);
				BuildResponse buildResponse = (BuildResponse) respObject;

				if (buildResponse.getTasksBuilt().size() == 1) 
				{
					logger.println("ISPW: Set " + setId + " - " + buildResponse.getTasksBuilt().size()  //$NON-NLS-1$ //$NON-NLS-2$
							+ " task will be built"); //$NON-NLS-1$ 
				} 
				else 
				{
					logger.println("ISPW: Set " + setId + " - " + buildResponse.getTasksBuilt().size()  //$NON-NLS-1$ //$NON-NLS-2$
							+ " tasks will be built"); //$NON-NLS-1$ 
				}

				List<TaskInfo> tasksBuilt = buildResponse.getTasksBuilt();
				// Used to hold the difference between tasks built and tasks within a closed set
				List<TaskInfo> tasksNotBuilt = tasksBuilt;
				// Get the tasks that were successfully generated (anything leftover in a set is
				// successful)
				List<TaskInfo> tasksInSet = taskListResp.getTasks();
				int numTasksToBeBuilt = tasksBuilt.size();
				Set<String> uniqueTasksInSet = new HashSet<>();
				
				//Retrieve the AssignmentId from the response and add it to the TaskInfo from the set
				for(TaskInfo task :  tasksBuilt)
				{
					for(TaskInfo task2 :  tasksInSet)
					{
						if(task.getTaskId().equals(task2.getTaskId()))
						{
							//Replace the TaskInfo object with the one from the build response
							task2.setAssignmentId(task.getAssignmentId());
						}
					}
				}

				if (!tasksInSet.isEmpty()) 
				{
					// audit trail is available
					for (TaskInfo task : tasksInSet) 
					{
						// take GP and G into consideration - new tasks without PGM=Y is GP.
						// New tasks without PGM=Y, if generate fails remains in the set so this code
						// doesn't work
						if (task.getOperation().startsWith("G")) //$NON-NLS-1$
						{
							logger.println("ISPW: Set " + setId + " - " + "The generate request completed successfully for " + 
									task.getModuleName() + " in " + task.getAssignmentId() + "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
						// Remove all successfully built tasks
						uniqueTasksInSet.add(task.getTaskId());
						tasksNotBuilt.removeIf(x -> x.getTaskId().equals(task.getTaskId())); // remove all successfully
																								// built
					}

					for (TaskInfo task : tasksNotBuilt) 
					{
						logger.println(
								"ISPW: Set " + setId + " - " + task.getModuleName() + " in " +  task.getAssignmentId()  +" did not generate successfully"); //$NON-NLS-1$ //$NON-NLS-2$ 
					}

					StringBuilder sb = new StringBuilder();
					sb.append("ISPW: " + uniqueTasksInSet.size() + " of " + numTasksToBeBuilt    //$NON-NLS-1$ //$NON-NLS-2$ 
							+ " generated successfully. " + tasksNotBuilt.size() + " of " + numTasksToBeBuilt  //$NON-NLS-1$ //$NON-NLS-2$ 
							+ " generated with errors.\n"); //$NON-NLS-1$ 

					if (!tasksNotBuilt.isEmpty()) 
					{
						
						logger.println(sb);
						logger.println("ISPW: The build process completed with generate errors.");  //$NON-NLS-1$ //$NON-NLS-2$ 
						throw new AbortException("ISPW: Set processing has not generated successfully.");  //$NON-NLS-1$
					} 
					else 
					{						
						logger.println(sb);						
					}
				} 
				

				logger.println("ISPW: The build process completed.");	  //$NON-NLS-1$

			} 
			else 
			{
				throw new AbortException(
						"ISPW: Set processing has not completed successfully. Set status is " + setState + "."); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		/**
		 * This method logs the results of any other SetInfoPostAction. In order to get
		 * the full logging, be sure the IAction has the getIspwOperation() method
		 * implemented.
		 * 
		 * @param finalSetInfoResp
		 *            The SetInfoResponse that was received after the polling was
		 *            complete
		 * @param action
		 *            the IAction that is being run
		 * @param logger
		 *            the logger
		 * @throws AbortException
		 */
		private void logActionResults(SetInfoResponse finalSetInfoResp, IAction action, PrintStream logger) throws AbortException
		{
			if (action instanceof SetInfoPostAction)
			{
				SetInfoPostAction setAction = (SetInfoPostAction) action;
				Operation operation  = setAction.getIspwOperation();
				List<TaskInfo> tasksInSet = finalSetInfoResp.getTasks();
				if (tasksInSet != null)
				{
					for (TaskInfo task : tasksInSet)
					{
						if (task.getOperation().startsWith(operation.getCode())) //$NON-NLS-1$
						{
							logger.println("ISPW: " + task.getModuleName() + " " + operation.getPastTenseDescription() + " successfully");
						}
					}
				}
				
				String setState = StringUtils.trimToEmpty(finalSetInfoResp.getState());
				if (setState.equals(Constants.SET_STATE_CLOSED) || setState.equals(Constants.SET_STATE_COMPLETE)
						|| setState.equals(Constants.SET_STATE_WAITING_APPROVAL))
				{
					logger.println("ISPW: The " + step.getIspwAction() + " process completed.");
				}
				else
				{
					throw new AbortException("ISPW: Set processing has not completed successfully. Set status is " + setState + "."); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		
		private static final long serialVersionUID = 1L;

		FilePath resolveOutputFile()
		{
			String outputFile = step.getOutputFile();
			if (outputFile == null || outputFile.trim().isEmpty())
			{
				return null;
			}

			try
			{
				FilePath workspace = getContext().get(FilePath.class);
				if (workspace == null)
				{
					throw new IllegalStateException("Could not find workspace to save file outputFile: " + outputFile
							+ ". You should use it inside a 'node' block");
				}
				
				return workspace.child(outputFile);
			}
			catch (IOException | InterruptedException e)
			{
				throw new IllegalStateException(e);
			}
		}

		public Item getProject()
		{
			return run.getParent();
		}

	}
}
