/**
 * These materials contain confidential information and trade secrets of Compuware Corporation. You shall maintain the materials
 * as confidential and shall not disclose its contents to any third party except as may be required by law or regulation. Use,
 * disclosure, or reproduction is prohibited without the prior express written permission of Compuware Corporation.
 * 
 * All Compuware products listed within the materials are trademarks of Compuware Corporation. All other company or product
 * names are trademarks of their respective owners.
 * 
 * Copyright (c) 2019 Compuware Corporation. All rights reserved.
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
import com.compuware.ispw.restapi.HttpMode;
import com.compuware.ispw.restapi.IspwContextPathBean;
import com.compuware.ispw.restapi.IspwRequestBean;
import com.compuware.ispw.restapi.WebhookToken;
import hudson.FilePath;

/**
 * 
 */
@SuppressWarnings({"nls", "deprecation"})
public class IActionTest
{
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	private static Logger log = Logger.getLogger(IActionTest.class);
	PrintStream logger = System.out;
	
	@Before
	public void setUp() throws GitAPIException
	{
		System.out.println("IActionTest folder: " + tempFolder.getRoot().getAbsolutePath());
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
	 * Test method for {@link com.compuware.ispw.restapi.action.IBuildAction#getRequestBody(java.lang.String, java.io.File, java.io.PrintStream)}.
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testPreprocess_buildAutomaticallyNoAccess() throws IOException, InterruptedException
	{
		File buildDirectory = tempFolder.getRoot();
		String inputRequestBody = "buildautomatically = true\n###";
		BuildAction buildAction = new BuildAction();
		try {
			String outputRequestBody = buildAction.preprocess(inputRequestBody, new FilePath(buildDirectory), logger);
			assert(false);
		} catch (IOException | InterruptedException e) {
			assert(true);
		}
	}
	
	/**
	 * Test method for {@link com.compuware.ispw.restapi.action.IBuildAction#getRequestBody(java.lang.String, java.io.File, java.io.PrintStream)}.
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	@Test
	public void testPreprocess_buildAutomaticallyNoFile() throws IOException, InterruptedException
	{
		File buildDirectory = tempFolder.getRoot();
		File parmsFile = new File(buildDirectory, Constants.BUILD_PARAM_FILE_NAME);
		parmsFile.delete();
		String inputRequestBody = "BUILDautomatically true\n###";
		BuildAction buildAction = new BuildAction();
		String outputRequestBody = buildAction.preprocess(inputRequestBody, null, logger);
		String expectedOutput = "";
		assertEquals("Failure of this test indicates a change in behavior", expectedOutput, outputRequestBody);
		
		outputRequestBody = buildAction.preprocess(inputRequestBody, new FilePath(parmsFile), logger);
		expectedOutput = "";
		assertEquals("Failure of this test indicates a change in behavior", expectedOutput, outputRequestBody);
	}
	
	/**
	 * Test method for {@link com.compuware.ispw.restapi.action.IBuildAction#getRequestBody(java.lang.String, java.io.File, java.io.PrintStream)}.
	 * @throws InterruptedException 
	 */
	
	@Test
	public void testPreprocess_buildAutomaticallyHasFile() throws IOException, InterruptedException
	{
		File buildDirectory = tempFolder.getRoot();
		String inputRequestBody = "BuildAutomatically = true";
		BuildAction buildAction = new BuildAction();
		File parmFile = new File(buildDirectory.getAbsoluteFile(), Constants.BUILD_PARAM_FILE_NAME);
		// file is empty
		FileUtils.writeStringToFile(parmFile, "");
		String outputRequestBody = buildAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", StringUtils.EMPTY, outputRequestBody);
		
		// file contains build parms
		FileUtils.writeStringToFile(parmFile, "{\"containerId\":\"PLAY002455\",\"releaseId\":\"RELEASE666\",\"taskLevel\":\"DEV2\",\"taskIds\":[\"7E3A5D04D0C2\",\"7E3A5D04D3A3\"]}");
		outputRequestBody = buildAction.preprocess(inputRequestBody,  new FilePath(parmFile), logger);
		String expectedOutput = "assignmentId = PLAY002455\nlevel = DEV2\nreleaseId = RELEASE666\ntaskId = 7E3A5D04D0C2,7E3A5D04D3A3\n";
		assertEquals("Failure of this test indicates a change in behavior", expectedOutput, outputRequestBody);
		
		FileUtils.writeStringToFile(parmFile, "{\"containerId\":\"PLAY002455\",\"taskLevel\":\"DEV2\",\"taskIds\":[\"7E3A5D04D0C2\",\"7E3A5D04D3A3\"]}");
		outputRequestBody = buildAction.preprocess(inputRequestBody,  new FilePath(parmFile), logger);
		expectedOutput = "assignmentId = PLAY002455\nlevel = DEV2\ntaskId = 7E3A5D04D0C2,7E3A5D04D3A3\n";
		assertEquals("Failure of this test indicates a change in behavior", expectedOutput, outputRequestBody);
	}
	
	/**
	 * Test method for {@link com.compuware.ispw.restapi.action.IBuildAction#getRequestBody(java.lang.String, java.io.File, java.io.PrintStream)}.
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	@Test
	public void testPreprocess_notBuildAutomatically() throws IOException, InterruptedException
	{
		File buildDirectory = tempFolder.getRoot();
		File parmFile = new File(buildDirectory.getAbsoluteFile(), Constants.BUILD_PARAM_FILE_NAME);
		parmFile.delete();
		String inputRequestBody = "# comment";
		BuildAction buildAction = new BuildAction();
		String outputRequestBody = buildAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", inputRequestBody, outputRequestBody);
		
		inputRequestBody = "# buildautomatically = true \n# comment";
		outputRequestBody = buildAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", inputRequestBody, outputRequestBody);
		
		inputRequestBody = "buildautomatically = false \n# comment";
		outputRequestBody = buildAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", inputRequestBody, outputRequestBody);
		
		inputRequestBody = null;
		outputRequestBody = buildAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", inputRequestBody, outputRequestBody);
		
		inputRequestBody = "";
		outputRequestBody = buildAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", inputRequestBody, outputRequestBody);
		
	}
	
	/**
	 * Test method for {@link com.compuware.ispw.restapi.action.IBuildAction#getRequestBody(java.lang.String, java.io.File, java.io.PrintStream)}.
	 * @throws InterruptedException 
	 */
	@Test
	public void testPreprocess_replacingGivenParms() throws IOException, InterruptedException
	{
		File buildDirectory = tempFolder.getRoot();
		String inputRequestBody = "buildAutomatically=true\nevents.name=Completed\r\napplication = x\nevents.body=Deployed\r\n  assignmentID = y\n releaseid = z\n"
				+ "events.httpHeaders=Jenkins-Crumb:no-crumb\r\nTASKID=xyz\n\nmname=f\nmtype =hij\r\nlevel = qwerty\nevents.credentials=admin:library";
		BuildAction buildAction = new BuildAction();
		
		File parmFile = new File(buildDirectory.getAbsoluteFile(), Constants.BUILD_PARAM_FILE_NAME);
		FileUtils.writeStringToFile(parmFile,
				"{\"containerId\":\"PLAY002455\",\"releaseId\":\"RELEASE666\",\"taskLevel\":\"DEV2\",\"taskIds\":[\"7E3A5D04D0C2\",\"7E3A5D04D3A3\"]}");
		String expectedOutput = "assignmentId = PLAY002455\nlevel = DEV2\nreleaseId = RELEASE666\ntaskId = 7E3A5D04D0C2,7E3A5D04D3A3\n\nevents.name=Completed\r\n\nevents.body=Deployed\r\n\n"
				+ "\nevents.httpHeaders=Jenkins-Crumb:no-crumb\r\n\n\n\n\r\n\nevents.credentials=admin:library";
		
		String outputRequestBody = buildAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", expectedOutput, outputRequestBody);
	}

	/**
	 * Test method for {@link com.compuware.ispw.restapi.action.IBuildAction#getRequestBody(java.lang.String, java.io.File, java.io.PrintStream)}.
	 * @throws InterruptedException 
	 */
	@Test
	public void testPreprocess_containsEventData() throws IOException, InterruptedException
	{
		File buildDirectory = tempFolder.getRoot();
		String inputRequestBody = "buildAutomatically=true\nevents.name=Completed\r\nevents.body=Deployed\r\n"
				+ "events.httpHeaders=Jenkins-Crumb:no-crumb\r\nevents.credentials=admin:library";
		BuildAction buildAction = new BuildAction();
		
		File parmFile = new File(buildDirectory.getAbsoluteFile(), Constants.BUILD_PARAM_FILE_NAME);
		FileUtils.writeStringToFile(parmFile,
				"{\"containerId\":\"PLAY002455\",\"releaseId\":\"RELEASE666\",\"taskLevel\":\"DEV2\",\"taskIds\":[\"7E3A5D04D0C2\",\"7E3A5D04D3A3\"]}");
		String expectedOutput = "assignmentId = PLAY002455\nlevel = DEV2\nreleaseId = RELEASE666\ntaskId = 7E3A5D04D0C2,7E3A5D04D3A3\n\nevents.name=Completed\r\nevents.body=Deployed\r\n"
				+ "events.httpHeaders=Jenkins-Crumb:no-crumb\r\nevents.credentials=admin:library";
		String outputRequestBody = buildAction.preprocess(inputRequestBody, new FilePath(parmFile), logger);
		assertEquals("Failure of this test indicates a change in behavior", expectedOutput, outputRequestBody);
	}
	
	private class BuildAction extends SetInfoPostAction
	{
		public BuildAction() {
			super(logger);
			// TODO Auto-generated constructor stub
		}

		@Override
		public IspwRequestBean getIspwRequestBean(String srid, String ispwRequestBody, WebhookToken webhookToken) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public PrintStream getLogger() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startLog(PrintStream logger, IspwContextPathBean ispwContextPathBean, Object jsonObject) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object endLog(PrintStream logger, IspwRequestBean ispwRequestBean, String responseJson) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public HttpMode getHttpMode() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String preprocess(String ispwRequestBody, FilePath pathToParmFile, PrintStream logger) throws IOException, InterruptedException
		{
			String automaticRegex = "(?i)(?m)(^(?!#)(.+)?buildautomatically.+true(.+)?$)";
			return super.preprocess(automaticRegex, ispwRequestBody, pathToParmFile, logger, "test", "test");
		}
		
	}
}
