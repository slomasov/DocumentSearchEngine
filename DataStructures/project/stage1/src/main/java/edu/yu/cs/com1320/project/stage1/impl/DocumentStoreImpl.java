package edu.yu.cs.com1320.project.stage1.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage1.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage1.Document;

public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage1.DocumentStore{

    private HashTableImpl<URI,DocumentImpl> docTable;

    public DocumentStoreImpl(){
    	this.docTable = new HashTableImpl<>();
    }

    /**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws IOException if there is an issue reading input
     * @throws IllegalArgumentException if uri or format are null
     */
    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException{
    	if(format == null || uri ==null){
    		throw new IllegalArgumentException();
    	}
    	if(input==null){
    		throw new IOException();
    	}
        byte[] bytes = input.readAllBytes();
        input.close();
        boolean isBinary = true;
        DocumentImpl doc = null;
        if(format==DocumentFormat.TXT){
            isBinary=false;
        }
        if(isBinary==true){
            doc = new DocumentImpl(uri, bytes);
        }
        if(isBinary==false){
            doc = new DocumentImpl(uri, new String(bytes));
        }
        DocumentImpl docPrevious = this.docTable.put(uri,doc);
        if(docPrevious==null){
            return 0;
        }
        else{
            return docPrevious.hashCode();
        }
    }
    //end of put

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public Document get(URI uri){
    	return this.docTable.get(uri);
    }
  	//end of put

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean delete(URI uri){
    	boolean present = this.docTable.containsKey(uri);
    	if(present==false){
    		return present;
    	}
    	this.docTable.put(uri,null);
    	return true;
    }
    //end of put
}