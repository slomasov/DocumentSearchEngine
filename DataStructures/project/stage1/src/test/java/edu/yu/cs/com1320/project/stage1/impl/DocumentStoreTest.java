package edu.yu.cs.com1320.project.stage1.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URI;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage1.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;

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
    	this.docStoreTest.put(inputCreate(true),uriCreate("http://java.sun.com/j2se/1.3/"),DocumentStore.DocumentFormat.BINARY);
    	this.docStoreTest.put(inputCreate(false),uriCreate("http://java.com/j2se/1.3/"),DocumentStore.DocumentFormat.TXT);
    }

	@Test
	public void putTest() throws java.io.FileNotFoundException, java.net.URISyntaxException, java.io.IOException{
		assertThrows(IllegalArgumentException.class, () -> {
			this.docStoreTest.put(inputCreate(true),null,DocumentStore.DocumentFormat.BINARY);
		});
		URI uriTest = uriCreate("http://java.sun.com/j2se/1.3/");
		assertEquals(0, this.docStoreTest.put(inputCreate(true),uriTest,DocumentStore.DocumentFormat.BINARY));
		putInitialize();
		DocumentImpl docRetrieved = (DocumentImpl) this.docStoreTest.get(uriTest);
		assertEquals(docRetrieved.hashCode(),this.docStoreTest.put(inputCreate(true),uriTest,DocumentStore.DocumentFormat.BINARY),"the output should be the hashCode of the retrieved document");
	}

	@Test
	public void getTest() throws java.io.FileNotFoundException, java.net.URISyntaxException, java.io.IOException{
		putInitialize();
		URI uriTest = uriCreate("http://java.sun.com/j2se/1.3/");
		DocumentImpl docTestBinary = new DocumentImpl(uriTest,inputCreate(true).readAllBytes());
		assertEquals(docTestBinary, this.docStoreTest.get(uriTest),"the underlying binary document should match");

		URI uriTest2 = uriCreate("http://java.com/j2se/1.3/");
		DocumentImpl docTestTxt = new DocumentImpl(uriTest2,new String(inputCreate(false).readAllBytes()));
		assertEquals(docTestTxt, this.docStoreTest.get(uriTest2),"the underlying text document should match");
	}

	@Test
	public void deleteTest() throws java.io.FileNotFoundException, java.net.URISyntaxException, java.io.IOException{
		URI uriTest = uriCreate("http://java.sun.com/j2se/1.3/");
		assertEquals(false, this.docStoreTest.delete(uriTest),"The document with the passes URI should not be present");
		putInitialize();
		assertEquals(true, this.docStoreTest.delete(uriTest),"The document with the passes URI should not be present");
	}
}