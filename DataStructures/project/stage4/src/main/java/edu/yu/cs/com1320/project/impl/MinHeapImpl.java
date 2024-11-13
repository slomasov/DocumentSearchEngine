package edu.yu.cs.com1320.project.impl;

import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends edu.yu.cs.com1320.project.MinHeap<E>{

	public MinHeapImpl(){
		elements = (E[])new Comparable[11];
	}

	@Override
	public void reHeapify(E element){
		int k = getArrayIndex(element);
		upHeap(k);
		downHeap(k);
	}

	@Override
    protected int getArrayIndex(E element){
    	for(int i=1; i<count+1; i++){
    		if(elements[i].equals(element)){
    			return i;
    		}
    	}
    	throw new NoSuchElementException("Element not found");
    }

    @Override
    protected void doubleArraySize(){
    	E[] temp = (E[])new Comparable[elements.length*2];
    	for(int i=1;i<elements.length;i++){
    		temp[i]=elements[i];
    	}
    	elements=temp;
    }
}