package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.net.URI;
import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage5.impl.*;
import edu.yu.cs.com1320.project.stage5.*;
import java.io.File;

public class BTreeTest{
	BTreeImpl<URI,Document> btTest = new BTreeImpl<>();
	URI uriTest = null;
    BTreeImpl<Integer,Character> htTest = new BTreeImpl<>();
	Character c1 = htTest.put(6,'c');
	Character c2 = htTest.put(1,'m');
	Character c3 = htTest.put(0,'c');
	Character c4 = htTest.put(11,'b');

	private DocumentImpl docCreate() throws java.net.URISyntaxException{
        uriTest = new URI("http://java.sun.com/j2se/13");
        DocumentImpl docTest = new DocumentImpl(uriTest, "testName", null);
        return docTest;
    }

	@Test
	public void putTest(){
		assertEquals('m',htTest.put(1,'t'),"the old value of key 1 should be m");
		assertEquals(null,htTest.put(2,'t'), "the value of key 2 was not already present, hence null");
		assertEquals(null,htTest.put(16,'m'), "the value of key 16 was not already present, hence null");
	}

	@Test
	public void deleteTest(){
		assertEquals('c', htTest.get(0),"the key 0 should be present in the table" );
		Character ct = htTest.put(0,null);
		assertEquals(null, htTest.get(0),"the key 0 should have been deleted" );

	}

	//@Test
	//public void containsKeyTest(){
	//	assertEquals(false,htTest.containsKey(3),"the key 3 should not be present in the table" );
	//	assertEquals(true, htTest.containsKey(1),"the key 1 should be present in the table" );
	//	assertEquals(true, htTest.containsKey(6),"the key 6 should be present in the table" );
	//}

	@Test
	public void getTest(){
		assertEquals('c',htTest.get(6),"the value of key 6 should be c");
		assertEquals('m',htTest.get(1),"the value of key 1 should be m");
		assertEquals('c',htTest.get(0),"the value of key 0 should be c");
		assertEquals(null,htTest.get(2),"the key 2 should not be present, hence null");
	}

	/*
	@Test
	public void moveToDiskTest() throws Exception{
		this.btTest.setPersistenceManager(new DocumentPersistenceManager(null));
		DocumentImpl docTest = docCreate();
		this.btTest.put(uriTest,docTest);
		assertEquals(docTest,btTest.get(uriTest),"the value of key uriTest should be docTest");
		this.btTest.moveToDisk(uriTest);
		assertEquals(docTest,btTest.get(uriTest),"the value of key uriTest should be docTest");
	}
	*/

	
	/*@Test
	public void moveToDiskTest() throws Exception{
		this.btTest.setPersistenceManager(new DocumentPersistenceManager(null));
		DocumentImpl docTest = docCreate();
		this.btTest.put(uriTest,docTest);
		String tempDir = uriToPath(uriTest);
		File tempFile = new File(tempDir);
		assertEquals(false, tempFile.exists(), "file is not yet serialized and shouldn't be on disk");
		assertEquals(docTest,btTest.get(uriTest),"the value of key uriTest should be docTest");
		this.btTest.moveToDisk(uriTest);
		assertEquals(true, tempFile.exists(), "file must have been written to disk");
		DocumentImpl tempDoc = (DocumentImpl) btTest.get(uriTest);
		assertEquals(false, tempFile.exists(), "file must have been restored into memory and removed from disk");
		assertEquals(docTest,btTest.get(uriTest),"the value of key uriTest should be docTest");
	}
	*/

	private String uriToPath(URI uri){
        String temp = uri.toString();
        String tempScheme = uri.getScheme();
        int count = tempScheme.length()+1;
        return System.getProperty("user.dir") + temp.substring(count)+".json";
    }
}