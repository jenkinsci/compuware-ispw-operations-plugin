/**
* THESE MATERIALS CONTAIN CONFIDENTIAL INFORMATION AND TRADE SECRETS OF BMC SOFTWARE, INC. YOU SHALL MAINTAIN THE MATERIALS AS
* CONFIDENTIAL AND SHALL NOT DISCLOSE ITS CONTENTS TO ANY THIRD PARTY EXCEPT AS MAY BE REQUIRED BY LAW OR REGULATION. USE,
* DISCLOSURE, OR REPRODUCTION IS PROHIBITED WITHOUT THE PRIOR EXPRESS WRITTEN PERMISSION OF BMC SOFTWARE, INC.
*
* ALL BMC SOFTWARE PRODUCTS LISTED WITHIN THE MATERIALS ARE TRADEMARKS OF BMC SOFTWARE, INC. ALL OTHER COMPANY PRODUCT NAMES
* ARE TRADEMARKS OF THEIR RESPECTIVE OWNERS.
*
* (c) Copyright 2020 BMC Software, Inc.
*/
package com.compuware.ispw.restapi.action;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.compuware.ispw.restapi.Constants;
import hudson.FilePath;

@SuppressWarnings({"nls", "deprecation"})
public class DeployTaskActionTest
{
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	private static Logger log = Logger.getLogger(DeployTaskActionTest.class);
	PrintStream logger = System.out;
	
	@Before
	public void setUp() throws GitAPIException
	{
		System.out.println("DeployTaskActionTest folder: " + tempFolder.getRoot().getAbsolutePath());
	}
	
	@After
	public void tearDown()
	{
		try
		{
			FileUtils.deleteDirectory(tempFolder.getRoot());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Test method for {@link com.compuware.ispw.restapi.action.IDeployAction#getRequestBody(java.lang.String, java.io.File, java.io.PrintStream)}.
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testPreprocess_deployAutomaticallyNoAccess() throws IOException, InterruptedException
	{
		File buildDirectory = tempFolder.getRoot();
		String inputRequestBody = "deployautomatically = true\n###";
		DeployAction deployAction = new DeployAction();
		try {
			String outputRequestBody = deployAction.preprocess(inputRequestBody, new FilePath(buildDirectory), logger);
			assert(false);
		} catch (IOException | InterruptedException e) {
			assert(true);
		}
	}
	
	/**
	 * Test method for {@link com.compuware.ispw.restapi.action.IDeployAction#getRequestBody(java.lang.String, java.io.File, java.io.PrintStream)}.
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	@Test
	public void testPreprocess_deployAutomaticallyNoFile() throws IOException, InterruptedException
	{
		File buildDirectory = tempFolder.getRoot();
		File parmsFile = new File(buildDirectory, Constants.BUILD_PARAM_FILE_NAME);
		parmsFile.delete();
		String inputRequestBody = "DEPLOYautomatically true\n###";
		DeployAction deployAction = new DeployAction();
		String outputRequestBody = deployAction.preprocess(inputRequestBody, null, logger);
		String expectedOutput = "";
		assertEquals("Failure of this test indicates a change in behavior", expectedOutput, outputRequestBody);
		
		outputRequestBody = deployAction.preprocess(inputRequestBody, new FilePath(parmsFile), logger);
		expectedOutput = "";
		assertEquals("Failure of this test indicates a change in behavior", expectedOutput, outputRequestBody);
	}
	
	/**
	 * Test method for {@link com.compuware.ispw.restapi.action.IDeployAction#getRequestBody(java.lang.String, java.io.File, java.io.PrintStream)}.
	 * @throws InterruptedException 
	 */
	
	@Test
	public void testPreprocess_deployAutomaticallyHasFile() throws IOException, InterruptedException
	{
		File buildDirectory = tempFolder.getRoot();
		String inputRequestBody = "DeployAutomatically = true";
		DeployAction deployAction = new DeployAction();
		File parmFile = new File(buildDirectory.getAbsoluteFile(), Constants.BUILD_PARAM_FILE_NAME);
		// file is empty
		FileUtils.writeStringToFile(parmFile, "");
		String outputRequestBody = deployAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", StringUtils.EMPTY, outputRequestBody);
		
		// file contains build parms
		FileUtils.writeStringToFile(parmFile, "{\"containerId\":\"PLAY002455\",\"releaseId\":\"RELEASE666\",\"taskLevel\":\"DEV2\",\"taskIds\":[\"7E3A5D04D0C2\",\"7E3A5D04D3A3\"]}");
		outputRequestBody = deployAction.preprocess(inputRequestBody,  new FilePath(parmFile), logger);
		String expectedOutput = "assignmentId = PLAY002455\nlevel = DEV2\nreleaseId = RELEASE666\ntaskId = 7E3A5D04D0C2,7E3A5D04D3A3\n";
		assertEquals("Failure of this test indicates a change in behavior", expectedOutput, outputRequestBody);
		
		FileUtils.writeStringToFile(parmFile, "{\"containerId\":\"PLAY002455\",\"taskLevel\":\"DEV2\",\"taskIds\":[\"7E3A5D04D0C2\",\"7E3A5D04D3A3\"]}");
		outputRequestBody = deployAction.preprocess(inputRequestBody,  new FilePath(parmFile), logger);
		expectedOutput = "assignmentId = PLAY002455\nlevel = DEV2\ntaskId = 7E3A5D04D0C2,7E3A5D04D3A3\n";
		assertEquals("Failure of this test indicates a change in behavior", expectedOutput, outputRequestBody);
	}
	
	/**
	 * Test method for {@link com.compuware.ispw.restapi.action.IDeployAction#getRequestBody(java.lang.String, java.io.File, java.io.PrintStream)}.
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	@Test
	public void testPreprocess_notDeployAutomatically() throws IOException, InterruptedException
	{
		File buildDirectory = tempFolder.getRoot();
		File parmFile = new File(buildDirectory.getAbsoluteFile(), Constants.BUILD_PARAM_FILE_NAME);
		parmFile.delete();
		String inputRequestBody = "# comment";
		DeployAction deployAction = new DeployAction();
		String outputRequestBody = deployAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", inputRequestBody, outputRequestBody);
		
		inputRequestBody = "# deployautomatically = true \n# comment";
		outputRequestBody = deployAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", inputRequestBody, outputRequestBody);
		
		inputRequestBody = "deployautomatically = false \n# comment";
		outputRequestBody = deployAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", inputRequestBody, outputRequestBody);
		
		inputRequestBody = null;
		outputRequestBody = deployAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", inputRequestBody, outputRequestBody);
		
		inputRequestBody = "";
		outputRequestBody = deployAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", inputRequestBody, outputRequestBody);
		
	}
	
	/**
	 * Test method for {@link com.compuware.ispw.restapi.action.IDeployAction#getRequestBody(java.lang.String, java.io.File, java.io.PrintStream)}.
	 * @throws InterruptedException 
	 */
	@Test
	public void testPreprocess_replacingGivenParms() throws IOException, InterruptedException
	{
		File buildDirectory = tempFolder.getRoot();
		String inputRequestBody = "deployAutomatically=true\nevents.name=Completed\r\napplication = x\nevents.body=Deployed\r\n  assignmentID = y\n releaseid = z\n"
				+ "events.httpHeaders=Jenkins-Crumb:no-crumb\r\nTASKID=xyz\n\nmname=f\nmtype =hij\r\nlevel = qwerty\nevents.credentials=admin:library";
		DeployAction deployAction = new DeployAction();
		
		File parmFile = new File(buildDirectory.getAbsoluteFile(), Constants.BUILD_PARAM_FILE_NAME);
		FileUtils.writeStringToFile(parmFile,
				"{\"containerId\":\"PLAY002455\",\"releaseId\":\"RELEASE666\",\"taskLevel\":\"DEV2\",\"taskIds\":[\"7E3A5D04D0C2\",\"7E3A5D04D3A3\"]}");
		String expectedOutput = "assignmentId = PLAY002455\nlevel = DEV2\nreleaseId = RELEASE666\ntaskId = 7E3A5D04D0C2,7E3A5D04D3A3\n\nevents.name=Completed\r\n\nevents.body=Deployed\r\n\n"
				+ "\nevents.httpHeaders=Jenkins-Crumb:no-crumb\r\n\n\n\n\r\n\nevents.credentials=admin:library";
		
		String outputRequestBody = deployAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", expectedOutput, outputRequestBody);
	}

	/**
	 * Test method for {@link com.compuware.ispw.restapi.action.IDeployAction#getRequestBody(java.lang.String, java.io.File, java.io.PrintStream)}.
	 * @throws InterruptedException 
	 */
	@Test
	public void testPreprocess_containsEventData() throws IOException, InterruptedException
	{
		File buildDirectory = tempFolder.getRoot();
		String inputRequestBody = "deployAutomatically=true\nevents.name=Completed\r\nevents.body=Deployed\r\n"
				+ "events.httpHeaders=Jenkins-Crumb:no-crumb\r\nevents.credentials=admin:library";
		DeployAction deployAction = new DeployAction();
		
		File parmFile = new File(buildDirectory.getAbsoluteFile(), Constants.BUILD_PARAM_FILE_NAME);
		FileUtils.writeStringToFile(parmFile,
				"{\"containerId\":\"PLAY002455\",\"releaseId\":\"RELEASE666\",\"taskLevel\":\"DEV2\",\"taskIds\":[\"7E3A5D04D0C2\",\"7E3A5D04D3A3\"]}");
		String expectedOutput = "assignmentId = PLAY002455\nlevel = DEV2\nreleaseId = RELEASE666\ntaskId = 7E3A5D04D0C2,7E3A5D04D3A3\n\nevents.name=Completed\r\nevents.body=Deployed\r\n"
				+ "events.httpHeaders=Jenkins-Crumb:no-crumb\r\nevents.credentials=admin:library";
		String outputRequestBody = deployAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", expectedOutput, outputRequestBody);
	}
	
	private class DeployAction extends DeployTaskAction
	{
		public DeployAction() {
			super(logger);
		}		
	}
}
