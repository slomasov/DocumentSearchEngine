package edu.yu.cs.com1320.project.stage2.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage2.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage2.Document;

public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage2.DocumentStore{

    private HashTableImpl<URI,DocumentImpl> docTable;
    private StackImpl<Command> commandStack;

    public DocumentStoreImpl(){
    	this.docTable = new HashTableImpl<>();
        this.commandStack = new StackImpl<>();
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
        this.commandStack.push(new Command(uri, (URI) -> this.putOldValue(URI,docPrevious)));
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
  	//end of get

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
    	else{
            DocumentImpl oldValue = this.docTable.get(uri);
            this.commandStack.push(new Command(uri, (URI) -> this.putOldValue(URI,oldValue)));
            this.docTable.put(uri,null);
    	   return true;
        }
    }
    //end of delete

    /**
     * undo the last put or delete command
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    @Override
    public void undo() throws IllegalStateException{
        if(this.commandStack.size()==0){
            throw new IllegalStateException();
        }
        this.commandStack.pop().undo();
    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    @Override
    public void undo(URI uri) throws IllegalStateException{
        //search URI method: if this command stack doesn't contain the URI then throw the exception;
        //once I've found the command, call regular undo() since it's on top of the stack
        StackImpl<Command> temp = new StackImpl<>();
        while(this.commandStack.size()!=0 && !this.commandStack.peek().getUri().equals(uri)){
            temp.push(this.commandStack.pop());
        }
        this.undo();
        while(temp.size()!=0){
            this.commandStack.push(temp.pop());
        }
    }

    /**
     * finds a value in the docTable for a given uri and puts it back
     */    
    private boolean putOldValue(URI uri, DocumentImpl oldValue){
        this.docTable.put(uri,oldValue);
        return true;
    }
}