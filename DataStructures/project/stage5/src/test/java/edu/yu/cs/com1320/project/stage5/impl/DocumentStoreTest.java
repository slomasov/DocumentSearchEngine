package edu.yu.cs.com1320.project.stage5.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URI;
import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage5.impl.*;
import edu.yu.cs.com1320.project.stage5.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.io.File;

public class DocumentStoreTest{
	DocumentStoreImpl docStoreTest = new DocumentStoreImpl();

	private InputStream inputCreate(boolean isBinary) throws java.io.FileNotFoundException{
		byte[] binary = {1,1};
		byte[] text = {'a','b'};
		ByteArrayInputStream file = null;
		if(isBinary==true){
			file = new ByteArrayInputStream(binary);
		}
		else{
			file = new ByteArrayInputStream(text);
		}
		return file;
	}

	private URI uriCreate(String text) throws java.net.URISyntaxException{
        return new URI(text);
    }

    private void putInitialize() throws java.io.FileNotFoundException, java.net.URISyntaxException, java.io.IOException{
    	this.docStoreTest.put(inputCreate(true),uriCreate("http://java.sun.com/j2se/13"),DocumentStore.DocumentFormat.BINARY);
    	this.docStoreTest.put(inputCreate(false),uriCreate("http://java.com/j2se/13"),DocumentStore.DocumentFormat.TXT);
    }

	@Test
	public void putTest() throws java.io.FileNotFoundException, java.net.URISyntaxException, java.io.IOException{
		assertThrows(IllegalArgumentException.class, () -> {
			this.docStoreTest.put(inputCreate(true),null,DocumentStore.DocumentFormat.BINARY);
		});
		URI uriTest = uriCreate("http://java.sun.com/j2se/13");
		assertEquals(0, this.docStoreTest.put(inputCreate(true),uriTest,DocumentStore.DocumentFormat.BINARY));
		putInitialize();
		DocumentImpl docRetrieved = (DocumentImpl) this.docStoreTest.get(uriTest);
		assertEquals(docRetrieved.hashCode(),this.docStoreTest.put(inputCreate(true),uriTest,DocumentStore.DocumentFormat.BINARY),"the output should be the hashCode of the retrieved document");
	}

	@Test
	public void getTest() throws java.io.FileNotFoundException, java.net.URISyntaxException, java.io.IOException{
		putInitialize();
		URI uriTest = uriCreate("http://java.sun.com/j2se/13");
		DocumentImpl docTestBinary = new DocumentImpl(uriTest,inputCreate(true).readAllBytes());
		assertEquals(docTestBinary, this.docStoreTest.get(uriTest),"the underlying binary document should match");

		URI uriTest2 = uriCreate("http://java.com/j2se/13");
		DocumentImpl docTestTxt = new DocumentImpl(uriTest2,new String(inputCreate(false).readAllBytes()),null);
		assertEquals(docTestTxt, this.docStoreTest.get(uriTest2),"the underlying text document should match");

		List<Document> temp = new ArrayList<>();
		temp.add(docTestTxt);
		assertEquals(temp, this.docStoreTest.search("ab"),"the returned document for ab should be docTextTxt");
		assertEquals(temp, this.docStoreTest.searchByPrefix("ab"),"the returned document for ab should be docTextTxt");
	}

	@Test
	public void deleteTest() throws java.io.FileNotFoundException, java.net.URISyntaxException, java.io.IOException{
		URI uriTest = uriCreate("http://java.sun.com/j2se/13");
		assertEquals(false, this.docStoreTest.delete(uriTest),"The document with the passes URI should not be present");
		putInitialize();
		assertEquals(true, this.docStoreTest.delete(uriTest),"The document with the passes URI should not be present");

		URI uriTest2 = uriCreate("http://java.com/j2se/13");
		Set<URI> temp = new HashSet<>();
		temp.add(uriTest2);
		assertEquals(temp, this.docStoreTest.deleteAll("ab"),"the returned deleted document for ab should be docTextTxt");
		assertEquals(new HashSet<>(), this.docStoreTest.deleteAll("ab"),"the returned document for ab now that it's deleted should be an empty set");
	}

	@Test
	public void undoTest() throws java.io.FileNotFoundException, java.net.URISyntaxException, java.io.IOException{
		URI uriTest = uriCreate("http://java.sun.com/j2se/13");
		URI uriTest2 = uriCreate("http://java.com/j2se/13");
		assertEquals(false, this.docStoreTest.delete(uriTest),"The document with the passes URI should not be present");
		putInitialize();
		assert(this.docStoreTest.get(uriTest2)!=null);
		assert(!this.docStoreTest.search("ab").isEmpty());
		this.docStoreTest.undo();
		assert(this.docStoreTest.get(uriTest2)==null);
		assert(this.docStoreTest.search("ab").isEmpty());
		DocumentImpl initialVal = (DocumentImpl) this.docStoreTest.get(uriTest);
		assert(initialVal!=null);
		boolean x = this.docStoreTest.delete(uriTest);
		assert(this.docStoreTest.get(uriTest)==null);
		this.docStoreTest.undo();
		assert(this.docStoreTest.get(uriTest)!=null);
		assert(this.docStoreTest.get(uriTest)==initialVal);
	}

	/*@Test
	public void undoUriTest() throws java.io.FileNotFoundException, java.net.URISyntaxException, java.io.IOException, IllegalStateException{
		URI uriTest = uriCreate("http://java.sun.com/j2se/13");
		URI uriTest2 = uriCreate("http://java.com/j2se/13");
		putInitialize();
		this.docStoreTest.put(inputCreate(false),uriCreate("http://java.com/13"),DocumentStore.DocumentFormat.TXT);
		assert(!this.docStoreTest.search("ab").isEmpty());
		this.docStoreTest.deleteAll("ab");
		assert(this.docStoreTest.search("ab").isEmpty());
		assert(this.docStoreTest.get(uriTest2)==null);
		this.docStoreTest.undo(uriTest2);
		assert(this.docStoreTest.search("ab").size()==1);
		assert(this.docStoreTest.get(uriTest2)!=null);
		assert(this.docStoreTest.get(uriTest)!=null);
		this.docStoreTest.undo(uriTest2);
		assertThrows(IllegalStateException.class,()->{
			this.docStoreTest.undo(uriTest2);
		});
	}
	*/

	/*@Test
	public void limitTest() throws java.io.FileNotFoundException, java.net.URISyntaxException, java.io.IOException, IllegalStateException{
		this.docStoreTest.setMaxDocumentCount(3);
		this.undoUriTest();
		this.docStoreTest.setMaxDocumentBytes(4);
		//this.undoUriTest();
	}*/

	/*
	@Test
	public void putLimitTest() throws java.io.FileNotFoundException, java.net.URISyntaxException, java.io.IOException, IllegalStateException{
		putInitialize();
		URI uriTest = uriCreate("http://java.sun.com/j2se/13");
		//assert(this.docStoreTest.get(uriTest)!=null);
		String tempDir = uriToPath(uriTest);
		File tempFile = new File(tempDir);
		assertEquals(false, tempFile.exists(), "file is not yet serialized and shouldn't be on disk");
		this.docStoreTest.setMaxDocumentCount(2);
    	this.docStoreTest.put(inputCreate(true),uriCreate("http://java/ss"),DocumentStore.DocumentFormat.BINARY);
		assertEquals(true, tempFile.exists(), "file must have been written to disk");
		//this.docStoreTest.put(inputCreate(false),uriTest,DocumentStore.DocumentFormat.TXT);
		Scanner myReader = new Scanner(tempFile);
        String temp = "";
        while(myReader.hasNextLine()){
            temp+= myReader.nextLine();
        }
        assert(temp!=null);
        //this.docStoreTest.delete(uriTest);
		DocumentImpl tempDoc = (DocumentImpl) this.docStoreTest.get(uriTest);
		assertEquals(false, tempFile.exists(), "file must have been restored into memory and removed from disk");
		assertEquals(tempDoc,this.docStoreTest.get(uriTest),"the value of key uriTest should be docTest");
	}
	*/

	/*
	@Test
	public void putSearchDeleteLimitTest() throws java.io.FileNotFoundException, java.net.URISyntaxException, java.io.IOException, IllegalStateException{
		putInitialize();
		URI uriTest = uriCreate("http://java.sun.com/j2se/13");
		//assert(this.docStoreTest.get(uriTest)!=null);
		String tempDir = uriToPath(uriTest);
		File tempFile = new File(tempDir);
		assertEquals(false, tempFile.exists(), "file is not yet serialized and shouldn't be on disk");
		this.docStoreTest.setMaxDocumentCount(2);
    	this.docStoreTest.put(inputCreate(true),uriCreate("http://java/ss"),DocumentStore.DocumentFormat.BINARY);
		assertEquals(true, tempFile.exists(), "file must have been written to disk");
		Scanner myReader = new Scanner(tempFile);
        String temp = "";
        while(myReader.hasNextLine()){
            temp+= myReader.nextLine();
        }
        assert(temp!=null);
        URI uriTest2=uriCreate("http://java.com/j2se/13");
        assert(this.docStoreTest.get(uriTest2)!=null);
        this.docStoreTest.delete(uriTest2);
        assert(this.docStoreTest.get(uriTest2)==null);
		DocumentImpl tempDoc = (DocumentImpl) this.docStoreTest.get(uriTest);
		assertEquals(false, tempFile.exists(), "file must have been restored into memory and removed from disk");
		assertEquals(tempDoc,this.docStoreTest.get(uriTest),"the value of key uriTest should be docTest");
	}
	*/

	@Test
	public void undoLimitTest() throws Exception{
		putInitialize();
		URI uriTest = uriCreate("http://java.sun.com/j2se/13");
		//assert(this.docStoreTest.get(uriTest)!=null);
		String tempDir = uriToPath(uriTest);
		File tempFile = new File(tempDir);
		assertEquals(false, tempFile.exists(), "file is not yet serialized and shouldn't be on disk");
		this.docStoreTest.setMaxDocumentCount(2);
		URI uriTest3 = uriCreate("http://java/ss");
    	this.docStoreTest.put(inputCreate(true), uriTest3 ,DocumentStore.DocumentFormat.BINARY);
		assertEquals(true, tempFile.exists(), "file must have been written to disk");
		Scanner myReader = new Scanner(tempFile);
        String temp = "";
        while(myReader.hasNextLine()){
            temp+= myReader.nextLine();
        }
        assert(temp!=null);
        URI uriTest2=uriCreate("http://java.com/j2se/13");
        assert(this.docStoreTest.get(uriTest2)!=null);
        this.docStoreTest.delete(uriTest2);
        assert(this.docStoreTest.get(uriTest2)==null);
		DocumentImpl tempDoc = (DocumentImpl) this.docStoreTest.get(uriTest);
		assertEquals(false, tempFile.exists(), "file must have been restored into memory and removed from disk");
		assertEquals(tempDoc,this.docStoreTest.get(uriTest),"the value of key uriTest should be docTest");
		tempDir=uriToPath(uriTest3);
		tempFile = new File(tempDir);
		assertEquals(false, tempFile.exists(), "file is not yet serialized and shouldn't be on disk");
		this.docStoreTest.undo();
		assertEquals(true, tempFile.exists(), "delete has been undone causing this to be pushed to disk");
	}

	private String uriToPath(URI uri){
        String temp = uri.toString();
        String tempScheme = uri.getScheme();
        int count = tempScheme.length()+1;
        return System.getProperty("user.dir") + temp.substring(count)+".json";
    }
}