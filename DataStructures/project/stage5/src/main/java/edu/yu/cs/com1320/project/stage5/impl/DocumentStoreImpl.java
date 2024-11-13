package edu.yu.cs.com1320.project.stage5.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage5.impl.*;
import edu.yu.cs.com1320.project.stage5.*;
import java.util.*;
import java.io.File;

public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage5.DocumentStore{

    private BTreeImpl<URI,Document> docBTree;
    private StackImpl<Undoable> commandStack;
    private TrieImpl<URI> docTrie;
    private MinHeapImpl<Document> docHeap;
    private int maxQuantity = -1;
    private int heapCount = 0;
    private int maxByte = -1;
    private int heapByteSize = 0;

    class docComparator<T> implements java.util.Comparator<T>{
        String word;
        BTreeImpl<URI,Document> bTree;

        docComparator(String s, BTreeImpl<URI,Document> b){
            this.word=s;
            this.bTree=b;
        }

        @Override
        public int compare(T o1, T o2){
            if(o1==null || o2==null){
                throw new IllegalArgumentException();
            }
            if(o1==o2){
                return 0;
            }
            DocumentImpl temp1 = (DocumentImpl) this.bTree.get((URI)o1);
            DocumentImpl temp2 = (DocumentImpl) this.bTree.get((URI)o2);
            return temp2.wordCount(word) - temp1.wordCount(word);
        }
    }

    public DocumentStoreImpl(){
        this(null);
    }

    public DocumentStoreImpl(File baseDir){
        this.docBTree = new BTreeImpl<>();
        PersistenceManager<URI,Document> temp = new DocumentPersistenceManager(baseDir);
        this.docBTree.setPersistenceManager(temp);
        this.commandStack = new StackImpl<>();
        this.docTrie = new TrieImpl<>();
        this.docHeap = new MinHeapImpl<>();
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

        //adding bytes and count
        this.heapByteSize+=bytes.length;
        this.heapCount++;

        if(format==DocumentFormat.TXT){
            isBinary=false;
        }
        if(isBinary==true){
            doc = new DocumentImpl(uri, bytes);
        }
        if(isBinary==false){
            doc = new DocumentImpl(uri, new String(bytes), null);
            for(String s : doc.getWords()){
                this.docTrie.put(s, uri);
            }
        }
        doc.setLastUseTime(System.nanoTime());
        this.docHeap.insert(doc);
        this.manageLimit();
        DocumentImpl docPrevious = (DocumentImpl)this.docBTree.put(uri,doc);
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
    	DocumentImpl doc = (DocumentImpl) this.docBTree.get(uri);
        if(doc!=null){
            doc.setLastUseTime(System.nanoTime());
            try{
                this.docHeap.reHeapify(doc);
            }
            catch(NoSuchElementException e){
                this.heapCount++;
                this.heapByteSize+=getDocumentBytes(doc);
                this.docHeap.insert(doc);
                this.putDocumentIntoTrie(doc);
                this.manageLimit();
            }
        }
        return doc;
    }
  	//end of get

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean delete(URI uri){
        Object present = this.docBTree.get(uri);
        if(present==null){
            return false;
        }
        else{
            DocumentImpl oldValue = (DocumentImpl) present;
            this.commandStack.push(new GenericCommand<>(uri, (URI) -> this.putOldValue(URI,oldValue)));
            this.docBTree.put(uri,null);
            deleteDocumentFromTrie(oldValue);
            deleteDocumentFromHeap(oldValue);
            return true;
        }
    }
    //end of delete

    private void deleteDocumentFromHeap(Document doc){
        /*MinHeapImpl<Document> temp = new MinHeapImpl<>();
        int count = 0;
        while(!this.docHeap.remove().equals(doc)){
            temp.insert(this.docHeap.remove());
            count++;
        }
        this.docHeap.remove();
        for(int i=0; i<count; i++){
            this.docHeap.insert(temp.remove());
        }
        */
        try{
            doc.setLastUseTime(-1);
            this.docHeap.reHeapify(doc);
            this.docHeap.remove();
            heapCount--;
            heapByteSize-=getDocumentBytes(doc);
            return;
        }
        catch(NoSuchElementException e){
            return;
        }
    }

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
     * finds a value in the docBTree for a given uri and puts it back
     */    
    private boolean putOldValue(URI uri, DocumentImpl oldValue){
        DocumentImpl temp = (DocumentImpl) this.docBTree.put(uri,oldValue);
        if(oldValue==null){
            deleteDocumentFromTrie(temp);
            deleteDocumentFromHeap(temp);
        }
        else{
            oldValue.setLastUseTime(System.nanoTime());
            try{
                this.docHeap.reHeapify(oldValue);
            }
            catch(NoSuchElementException e){
                this.heapCount++;
                this.heapByteSize+=getDocumentBytes(oldValue);
                this.docHeap.insert(oldValue);
                this.putDocumentIntoTrie(oldValue);
                this.manageLimit();
            }
        }
        return true;
    }

    private void putDocumentIntoTrie(DocumentImpl doc){
        if(doc.getDocumentTxt()==null){
            return;
        }
        for(String s : doc.getWords()){
            this.docTrie.put(s, doc.getKey());
        }
    }

    private void deleteDocumentFromTrie(DocumentImpl doc){
        if(doc.getDocumentTxt()==null){
            return;
        }
        for(String s : doc.getWords()){
            this.docTrie.delete(s, doc.getKey());
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
        List<URI> docList = this.docTrie.getAllSorted(keyword, new docComparator(keyword, this.docBTree));
        long k = System.nanoTime();
        Document x = null;
        List<Document> temp = new ArrayList<>();
        for(URI doc : docList){
            x=this.docBTree.get(doc);
            temp.add(x);
            x.setLastUseTime(k);
            try{
                this.docHeap.reHeapify(x);
            }
            catch(NoSuchElementException e){
                this.docHeap.insert(x);
                this.heapCount++;
                this.heapByteSize+=getDocumentBytes(x);
            }
        }
        this.manageLimit();
        return temp;
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
        List<URI> docList = this.docTrie.getAllWithPrefixSorted(keywordPrefix, new docComparator(keywordPrefix, this.docBTree));
        long k = System.nanoTime();
        Document x = null;
        List<Document> temp = new ArrayList<>();
        for(URI doc : docList){
            x=this.docBTree.get(doc);
            temp.add(x);
            x.setLastUseTime(k);
            try{
                this.docHeap.reHeapify(x);
            }
            catch(NoSuchElementException e){
                this.docHeap.insert(x);
                this.heapCount++;
                this.heapByteSize+=getDocumentBytes(x);
            }
        }
        this.manageLimit();
        return temp;
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * Search is CASE SENSITIVE.
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAll(String keyword){
        Set<URI> deletedDocs = this.docTrie.deleteAll(keyword);
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
        Set<URI> deletedDocs = this.docTrie.deleteAllWithPrefix(keywordPrefix);
        return manageDeleteAll(deletedDocs);
    }

    private Set<URI> manageDeleteAll(Set<URI> docSet){
        if(docSet.isEmpty()){
            return new HashSet<>();
        }
        CommandSet comSet = new CommandSet();
        //Set<URI> temp = new HashSet<>();
        long k = System.nanoTime();
        for(URI d : docSet){
            //deleteDocumentFromHeap(d);
            //URI uri = d.getKey();
            this.docBTree.put(d, null);
            //temp.add(uri);
            //this.docBTree.get(d).setLastUseTime(k);
            comSet.addCommand(new GenericCommand<>(d, (URI) -> this.putOldValue(URI, (DocumentImpl)this.docBTree.get(d))));
        }
        this.commandStack.push(comSet);
        return docSet;   
    }

    private void manageLimit(){
        if(this.maxQuantity==-1){
            if(this.maxByte==-1){
                return;
            }
            else{
                maintainHeap(false);
            }
        }
        else{
            if(this.maxByte==-1){
                maintainHeap(true);
            }
            else{
                maintainHeap(false);
                maintainHeap(true);
            }
        }
    }

    private void maintainHeap(boolean byQuantity){
        if(byQuantity){
            while(this.heapCount>this.maxQuantity){
                DocumentImpl doc = (DocumentImpl)this.docHeap.remove();
                this.heapByteSize-=getDocumentBytes(doc);
                this.heapCount--;
                deleteDocumentFromTrie(doc);
                deleteDocumentCommands(doc);
                try{
                    this.docBTree.moveToDisk(doc.getKey());
                }
                catch(Exception e){
                    this.docBTree.put(doc.getKey(),null);
                    //throw new IllegalArgumentException("couldn't move to disk");
                    return;
                }
                
            }
        }
        else{
            while(this.heapByteSize>this.maxByte){
                DocumentImpl doc = (DocumentImpl)this.docHeap.remove();
                this.heapByteSize-=getDocumentBytes(doc);
                this.heapCount--;
                deleteDocumentFromTrie(doc);
                deleteDocumentCommands(doc); 
                try{
                    this.docBTree.moveToDisk(doc.getKey());
                }
                catch(Exception e){
                    this.docBTree.put(doc.getKey(),null);
                    return;
                    //throw new IllegalArgumentException("couldn't move to disk");
                }
                
            }
        }
    }

    private void deleteDocumentCommands(Document doc){
    StackImpl<Undoable> temp = new StackImpl<>();
    URI uri = doc.getKey();
    while(this.commandStack.size()!=0){
        while(this.commandStack.size()!=0 && !commandContainsUri(commandStack.peek(),uri)){
            temp.push(this.commandStack.pop());
        }
        if(this.commandStack.size()==0){
            return;
        }
        if(this.commandStack.peek() instanceof GenericCommand){
            this.commandStack.pop();
        }
        else{
            List<Document> tempList = new ArrayList<>();
            tempList.add(doc);
            ((CommandSet)this.commandStack.peek()).removeAll(tempList);
            if(((CommandSet)this.commandStack.peek()).size()==0){
                this.commandStack.pop();
            }
        }
    }
    while(temp.size()!=0){
        this.commandStack.push(temp.pop());
    }
    }

    //private List<Document> removeFromCommandSet(Document doc,CommandSet comSet){
    //    Iterator it = comSet.iterator();
    //    List<Document> temp = new ArrayList<>();
    //    while(it.hasNext){
    //        if(it.next.equals(doc)){
    //
    //        }
    //    }
    //}

    private int getDocumentBytes(Document doc){
        DocumentImpl temp = (DocumentImpl)doc;
        if(temp.getDocumentTxt()==null){
            return temp.getDocumentBinaryData().length;
        }
        else{
            return temp.getDocumentTxt().getBytes().length;
        }
    }

    /**
     * set maximum number of documents that may be stored
     * @param limit
     */
    @Override
    public void setMaxDocumentCount(int limit){
        if(limit<0){
            throw new IllegalArgumentException("Can't set negative limit");
        }
        this.maxQuantity=limit;
        this.manageLimit();
    }

    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     * @param limit
     */
    @Override
    public void setMaxDocumentBytes(int limit){
        if(limit<0){
            throw new IllegalArgumentException("Can't set negative limit");
        }
        this.maxByte=limit;
        this.manageLimit();
    }
}