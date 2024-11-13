package edu.yu.cs.com1320.project.stage5.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.util.*;

public class DocumentTest{
    URI uriTest;
    Set<String> words;

    private DocumentImpl docCreate() throws java.net.URISyntaxException{
        uriTest = new URI("http://java.sun.com/j2se/13");
        DocumentImpl docTest = new DocumentImpl(uriTest, "testName", null);
        words = new HashSet<>();
        words.add("testName");
        return docTest;
    }

    //@Test
    //public void documentConstructor(){
    //    assertThrows(NullPointerException.class, ()->{
    //        docTest.hashCode(); 
    //    });
    //}

    @Test
    public void documentAttributes() throws java.net.URISyntaxException{
        assertEquals("testName",docCreate().getDocumentTxt(), "the key of docTest should be testName");
        assertEquals(null, docCreate().getDocumentBinaryData(),"binary data should be null");
        assertEquals(uriTest,docCreate().getKey(), "the URI should be uriTest");
    }

    @Test
    public void hashCodeTest() throws java.net.URISyntaxException{
        String txt = docCreate().getDocumentTxt();
        int result = docCreate().getKey().hashCode();
        result = 31*result + (txt !=null ? txt.hashCode() : 0);
        result = 31*result + Arrays.hashCode(docCreate().getDocumentBinaryData());
        assertEquals(Math.abs(result),docCreate().hashCode(),"hashCode is wrong");
    }

    @Test
    public void wordsTest() throws java.net.URISyntaxException{
        DocumentImpl docTest1 = docCreate();
        DocumentImpl docTest2 = new DocumentImpl(uriTest, " test  nam@@e name ", null);
        Set<String> words2 = new HashSet<>();
        words2.add("test");
        words2.add("name");
        assertEquals(words, docTest1.getWords(), "the document should only contain testName");
        assertEquals(words2, docTest2.getWords(), "the document should only contain test, name");
        assertEquals(2, docTest2.wordCount("name"));
        assertEquals(1, docTest2.wordCount("test"));
        assertEquals(0, docTest2.wordCount("n"));
    }
}