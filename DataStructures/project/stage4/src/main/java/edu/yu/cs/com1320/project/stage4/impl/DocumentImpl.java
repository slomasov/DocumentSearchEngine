package edu.yu.cs.com1320.project.stage4.impl;

import java.net.URI;
import java.util.*;
import edu.yu.cs.com1320.project.stage4.Document;
import java.lang.System;

public class DocumentImpl implements Document{
	private URI uri;
	private String text;
	private byte[] binaryData;
	private long lastUsedTime;
	private Map<String,Integer> wordCount;

	@Override
	public int compareTo(Document o1){
		long k = this.lastUsedTime-o1.getLastUseTime();
		if(k>0){
			return 1;
		}
		if(k<0){
			return -1;
		}
		return 0;
	}

	public DocumentImpl(URI uri, String txt){
		if(uri==null || txt==null || txt.isEmpty()){
			throw new IllegalArgumentException();
		}
		this.uri=uri;
		this.text=txt;
		this.binaryData=null;
		this.wordCount = new HashMap<>();
		this.separateWords(txt);
	}

	public DocumentImpl(URI uri, byte[] binaryData){
		if(uri==null || binaryData==null || binaryData.length==0){
			throw new IllegalArgumentException();
		}
		this.uri=uri;
		this.binaryData=binaryData;
		this.text=null;
		this.wordCount=null;
	}

	private void separateWords(String text){
		char[] textChars = text.toCharArray();
		int firstLetter = 0;
		for(int i=0; i<textChars.length; i++){
			if(textChars[i]==32){
				putIntoMap(text.substring(firstLetter,i));
				while(i<textChars.length && textChars[i]==32){
					i++;
				}
				firstLetter=i;
			}
		}
		putIntoMap(text.substring(firstLetter));
	}

	private void putIntoMap(String word){
		char[] textChars = word.toCharArray();
		String temp = "";
		int count=0;
		for(int i=0; i<word.length(); i++){
			if(letterOrDigit(textChars[i])){
				temp=temp+textChars[i];
			}
		}
		if(temp.isEmpty()){
			return;
		}
		if(this.wordCount.containsKey(temp)){
			this.wordCount.put(temp, this.wordCount.get(temp)+1);
		}
		else{
			this.wordCount.put(temp, 1);
		}
	}

	private Boolean letterOrDigit(char c){
		if( (c>47 && c<58) || (c>64 && c<91) || (c>96 && c<123) ){
			return true;
		}
		else{
			return false;
		}
	}

	/**
     * @return content of text document
     */
	@Override
    public String getDocumentTxt(){
    	return this.text;
    }

    /**
     * @return content of binary data document
     */
    @Override
    public byte[] getDocumentBinaryData(){
    	return this.binaryData;
    }

    /**
     * @return URI which uniquely identifies this document
     */
    @Override
    public URI getKey(){
    	return this.uri;
    }

    /**
     * how many times does the given word appear in the document?
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    @Override
    public int wordCount(String word){
    	if(word==null){
    		throw new IllegalArgumentException();
    	}
    	if(this.text==null){
    		return 0;
    	}
    	Integer temp = this.wordCount.get(word);
    	if(temp==null){
    		return 0;
    	}
    	else{
    		return temp;
    	}
    }

    /**
     * @return all the words that appear in the document
     */
    @Override
    public Set<String> getWords(){
    	return this.wordCount.keySet();
    }

    @Override
    public int hashCode(){
    	int result = uri.hashCode();
    	result = 31*result + (text!=null ? text.hashCode() : 0);
    	result = 31*result + Arrays.hashCode(binaryData);
    	return result;
    }

    @Override
	public boolean equals(Object obj) {
		if(obj==null){
			return false;
		}
		if(this==obj){
			return true;
		}
		if(getClass() != obj.getClass()){
			return false;
		}
		DocumentImpl temp = (DocumentImpl) obj;
		return temp.hashCode()==this.hashCode();
	}

	    /**
     * return the last time this document was used, via put/get or via a search result
     * (for stage 4 of project)
     */
	@Override
    public long getLastUseTime(){
    	return this.lastUsedTime;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds){
    	this.lastUsedTime=timeInNanoseconds;
    }
}