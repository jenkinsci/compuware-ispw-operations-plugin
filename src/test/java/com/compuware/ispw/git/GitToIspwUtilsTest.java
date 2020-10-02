/**
 * These materials contain confidential information and trade secrets of Compuware Corporation. You shall maintain the materials
 * as confidential and shall not disclose its contents to any third party except as may be required by law or regulation. Use,
 * disclosure, or reproduction is prohibited without the prior express written permission of Compuware Corporation.
 * 
 * All Compuware products listed within the materials are trademarks of Compuware Corporation. All other company or product
 * names are trademarks of their respective owners.
 * 
 * Copyright (c) 2020 Compuware Corporation. All rights reserved.
 */
package com.compuware.ispw.git;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.Test;
import hudson.plugins.git.Branch;
import hudson.plugins.git.Revision;

/**
 * 
 */
public class GitToIspwUtilsTest
{

	@Test
	public void testIsSameRevision()
	{
		
		String SHA1 = "3725b67f3daa6621dd01356c96c08a1f85b90c61"; //$NON-NLS-1$
		ObjectId objectId1 = ObjectId.fromString(SHA1);
		Revision revision1 = new Revision(objectId1);

		String SHA2 = "9ac446c472a6433fe503d294ebb7d5691b590269"; //$NON-NLS-1$
		ObjectId objectId2 = ObjectId.fromString(SHA2);
		Revision revision2 = new Revision(objectId2);		
		
		ObjectId objectId3 = ObjectId.fromString(SHA1);
		Revision revision3 = new Revision(objectId3);
		
		Revision revision4 = null;
		
		Revision revision5 = new Revision(objectId3, new ArrayList<Branch>());

        String branchName = "origin/tests/getSubmodules"; //$NON-NLS-1$
        Branch branch = new Branch(branchName, ObjectId.fromString(SHA1));
        List<Branch> branchCollection = new ArrayList<Branch>();
        branchCollection.add(branch);
        Revision revision6 = new Revision(objectId1, branchCollection);
        
        Revision revision7 = new Revision(objectId1, null);
        Revision revision8 = new Revision(objectId2, null);
		
		assertFalse(GitToIspwUtils.isSameRevision(revision1, revision2));
		assertTrue(GitToIspwUtils.isSameRevision(revision1, revision3));	
		assertFalse(GitToIspwUtils.isSameRevision(revision1, revision4));		
		assertFalse(GitToIspwUtils.isSameRevision(revision4, revision3));
		assertTrue(GitToIspwUtils.isSameRevision(revision1, revision5));
		assertTrue(GitToIspwUtils.isSameRevision(revision5, revision1));
		assertTrue(GitToIspwUtils.isSameRevision(revision5, revision6));
		assertTrue(GitToIspwUtils.isSameRevision(revision6, revision5));
		assertFalse(GitToIspwUtils.isSameRevision(revision7, revision8));
		assertFalse(GitToIspwUtils.isSameRevision(revision1, revision7));		
	}

	@Test
	public void testEscapeArgumentWithSpecialCharacter()
	{
		String uri = "https://test.user%40compuware.com@evolve.compuware.com/scm/~test.user_compuware.com/ispwgitplaytest.git"; //$NON-NLS-1$

		String escapedUri = GitToIspwUtils.escapeArgument(uri);

		assertNotEquals(uri, escapedUri);
		assertTrue(escapedUri.contains("%%")); //$NON-NLS-1$
	}

	@Test
	public void testEscapeArgumentWIthNoSpecialCharacter()
	{
		String uri = "https://test.user40compuware.com@evolve.compuware.com/scm/~test.user_compuware.com/ispwgitplaytest.git"; //$NON-NLS-1$

		String escapedUri = GitToIspwUtils.escapeArgument(uri);

		assertEquals(uri, escapedUri);
		assertFalse(escapedUri.contains("%%")); //$NON-NLS-1$
	}

}
