package edu.yu.cs.com1320.project.stage5.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.io.File;
import java.net.URI;
import edu.yu.cs.com1320.project.stage5.*;
import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.*;
import com.google.gson.*;

public class PersistenceManagerTest{
	PersistenceManager<URI,Document> pmTest = new DocumentPersistenceManager(new File("/Users/semyonlomasov/gittest2"));
	PersistenceManager<URI,Document> pmTestEmpty = new DocumentPersistenceManager(null);
	URI uriTest;

	private DocumentImpl docCreate() throws java.net.URISyntaxException{
        uriTest = new URI("http://www.yu.edu/documents/doc1");
        DocumentImpl docTest = new DocumentImpl(uriTest, "testName", null);
        return docTest;
    }

    /*
	@Test
	public void deserializeTest() throws Exception{
		DocumentImpl docTest = docCreate();
		pmTest.serialize(uriTest, docTest);
		DocumentImpl temp = (DocumentImpl)pmTest.deserialize(uriTest);
		assert(docTest.equals(temp));
	}

	@Test
	public void deserializeTestEmpty() throws Exception{
		DocumentImpl docTest = docCreate();
		pmTestEmpty.serialize(uriTest, docTest);
		DocumentImpl temp = (DocumentImpl)pmTestEmpty.deserialize(uriTest);
		assert(docTest.equals(temp));
	}

	@Test
	public void byteTest() throws Exception{
		DocumentImpl docTest = docCreate();
		DocumentImpl docTestByte = new DocumentImpl(uriTest, "ab".getBytes());
		pmTest.serialize(uriTest, docTestByte);
		DocumentImpl temp = (DocumentImpl)pmTest.deserialize(uriTest);
		assert(docTestByte.equals(temp));
	}

	@Test
	public void deleteTest() throws Exception{
		DocumentImpl docTest = docCreate();
		boolean temp = pmTest.delete(uriTest);
		assert(!temp);
		pmTest.serialize(uriTest, docTest);
		temp = pmTest.delete(uriTest);
		assert(temp);
	}
	*/
}