package edu.yu.cs.com1320.project.stage2.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.util.Arrays;

public class DocumentTest{
    URI uriTest;

    private DocumentImpl docCreate() throws java.net.URISyntaxException{
        uriTest = new URI("http://java.sun.com/j2se/1.3/");
        DocumentImpl docTest = new DocumentImpl(uriTest, "testName");
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
        assertEquals(result,docCreate().hashCode(),"hashCode is wrong");
    }
}