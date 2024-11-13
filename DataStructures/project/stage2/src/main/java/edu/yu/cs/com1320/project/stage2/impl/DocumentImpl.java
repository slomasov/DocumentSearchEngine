package edu.yu.cs.com1320.project.stage2.impl;

import java.net.URI;
import java.util.Arrays;

public class DocumentImpl implements edu.yu.cs.com1320.project.stage2.Document{
	private URI uri;
	private String text;
	private byte[] binaryData;

	public DocumentImpl(URI uri, String txt){
		if(uri==null || txt==null || txt.isEmpty()){
			throw new IllegalArgumentException();
		}
		this.uri=uri;
		this.text=txt;
		this.binaryData=null;
	}

	public DocumentImpl(URI uri, byte[] binaryData){
		if(uri==null || binaryData==null || binaryData.length==0){
			throw new IllegalArgumentException();
		}
		this.uri=uri;
		this.binaryData=binaryData;
		this.text=null;
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
}