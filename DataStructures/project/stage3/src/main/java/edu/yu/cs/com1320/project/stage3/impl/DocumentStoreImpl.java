package edu.yu.cs.com1320.project.stage3.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage3.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage3.Document;
import java.util.*;

public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage3.DocumentStore{

    private HashTableImpl<URI,DocumentImpl> docTable;
    private StackImpl<Undoable> commandStack;
    private TrieImpl<DocumentImpl> docTrie;

    class docComparator<T> implements java.util.Comparator<T>{
        String word;

        docComparator(String s){
            this.word=s;
        }

        @Override
        public int compare(T o1, T o2){
            if(o1==null || o2==null){
                throw new IllegalArgumentException();
            }
            if(o1==o2){
                return 0;
            }
            return ((DocumentImpl)o2).wordCount(word) - ((DocumentImpl)o1).wordCount(word);
        }
    }

    public DocumentStoreImpl(){
    	this.docTable = new HashTableImpl<>();
        this.commandStack = new StackImpl<>();
        this.docTrie = new TrieImpl<>();
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
            for(String s : doc.getWords()){
                this.docTrie.put(s, doc);
            }
        }
        DocumentImpl docPrevious = this.docTable.put(uri,doc);
        this.commandStack.push(new GenericCommand<>(uri, (URI) -> this.putOldValue(URI,docPrevious)));
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
            this.commandStack.push(new GenericCommand<>(uri, (URI) -> this.putOldValue(URI,oldValue)));
            this.docTable.put(uri,null);
            deleteDocumentFromTrie(oldValue);
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
        Undoable temp = this.commandStack.pop();
        if(temp instanceof GenericCommand){
            temp.undo();
        }
        else{
            ((CommandSet)temp).undoAll();
        }
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
        StackImpl<Undoable> temp = new StackImpl<>();
        while(this.commandStack.size()!=0 && !commandContainsUri(commandStack.peek(),uri)){
            temp.push(this.commandStack.pop());
        }
        if(this.commandStack.size()==0){
            throw new IllegalStateException();
        }
        if(this.commandStack.peek() instanceof GenericCommand){
            this.commandStack.pop().undo();
        }
        else{
            ((CommandSet)this.commandStack.peek()).undo(uri);
            if(((CommandSet)this.commandStack.peek()).size()==0){
                this.commandStack.pop();
            }
        }
        while(temp.size()!=0){
            this.commandStack.push(temp.pop());
        }
    }

    private boolean commandContainsUri(Undoable command, URI uri){
        if(command instanceof GenericCommand){
            return ((URI)(((GenericCommand)command).getTarget())).equals(uri);
        }
        else{
            return ((CommandSet)command).containsTarget(uri);
        }
    }

    /**
     * finds a value in the docTable for a given uri and puts it back
     */    
    private boolean putOldValue(URI uri, DocumentImpl oldValue){
        DocumentImpl temp = this.docTable.put(uri,oldValue);
        if(oldValue==null){
            deleteDocumentFromTrie(temp);
        }
        else{
            putDocumentIntoTrie(oldValue);
        }
        return true;
    }

    private void putDocumentIntoTrie(DocumentImpl doc){
        if(doc.getDocumentTxt()==null){
            return;
        }
        for(String s : doc.getWords()){
            this.docTrie.put(s, doc);
        }
    }

    private void deleteDocumentFromTrie(DocumentImpl doc){
        if(doc.getDocumentTxt()==null){
            return;
        }
        for(String s : doc.getWords()){
            this.docTrie.delete(s, doc);
        }
    }

    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> search(String keyword){
        return this.docTrie.getAllSorted(keyword, new docComparator(keyword));
    }

    /**
     * Retrieve all documents that contain text which starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE SENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByPrefix(String keywordPrefix){
        return this.docTrie.getAllWithPrefixSorted(keywordPrefix, new docComparator(keywordPrefix));
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * Search is CASE SENSITIVE.
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAll(String keyword){
        Set<DocumentImpl> deletedDocs = this.docTrie.deleteAll(keyword);
        return manageDeleteAll(deletedDocs);
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE SENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix){
        Set<DocumentImpl> deletedDocs = this.docTrie.deleteAllWithPrefix(keywordPrefix);
        return manageDeleteAll(deletedDocs);
    }

    private Set<URI> manageDeleteAll(Set<DocumentImpl> docSet){
        if(docSet.isEmpty()){
            return new HashSet<>();
        }
        CommandSet comSet = new CommandSet();
        Set<URI> temp = new HashSet<>();
        for(DocumentImpl d : docSet){
            URI uri = d.getKey();
            this.docTable.put(uri, null);
            temp.add(uri);
            comSet.addCommand(new GenericCommand<>(uri, (URI) -> this.putOldValue(URI,d)));
        }
        this.commandStack.push(comSet);
        return temp;   
    }
}