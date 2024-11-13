package edu.yu.cs.com1320.project.impl;

public class HashTableImpl<Key,Value> implements edu.yu.cs.com1320.project.HashTable<Key,Value>{

	class Entry<Key,Value>{ //sub-class denoting key-value pairs as entries
		Key key;
		Value value;

		Entry(Key k, Value v){
			this.key=k;
			this.value=v;
		}
	}
	//end of Entry

	class ListElement<E>{ //sub-class denoting linked lists for chaining
		Entry entry;
		ListElement next;

		ListElement(Key k, Value v){
			this.entry=new Entry(k,v);
			this.next=null;
		}
	}
	//end of LinkedList
	
	private ListElement<?>[] table;
	private int elementCount = 0;

	public HashTableImpl(){
		this.table = new ListElement[5];
	};
	//end of constructor

	private int hashFunction(Key k){
		return Math.abs(k.hashCode()) % this.table.length;
	}

	/**
     * @param k the key whose value should be returned
     * @return the value that is stored in the HashTable for k, or null if there is no such key in the table
     */
	@Override
    public Value get(Key k){
    	if(k == null){
    		return null;
    	}
    	ListElement current = getListElement(k);
    	if(current==null){
    		return null;
    	}
    	if(current.next == null){
    		return null;
    	}
    	if(current.next.entry.key.equals(k)){
    		return (Value)current.next.entry.value;
    	}
    	return null;
    }
    //end of get

    /**
     * @param k the key at which to store the value
     * @param v the value to store.
     * To delete an entry, put a null value.
     * @return if the key was already present in the HashTable, return the previous value stored for the key. If the key was not already present, return null.
     */

    @Override
    public Value put(Key k, Value v){
    	if(k==null){
			throw new IllegalArgumentException();
		}
		if(elementCount>4*this.table.length){
			this.doubleAndRehash();
		}
    	ListElement current = getListElement(k);
    	if(current==null){
    		if(v==null){
    			return null;
    		}
    		this.table[hashFunction(k)] = new ListElement(null,null); //pointer
			this.table[hashFunction(k)].next = new ListElement(k,v);
			elementCount++;
			return null;
    	}
    	if(current.next == null){  //if the key is not present
    		current.next=new ListElement(k,v);
    		elementCount++;
    		return null;
    	}
    	if(current.next.entry.key.equals(k)){ //if the key is already present
    		Value temp = (Value)current.next.entry.value;
    		if(v==null){
    			current.next=current.next.next;
    			elementCount--;
    			return temp;
    		}
    		current.next.entry.value=v;
    		return temp;
    	}
    	return null;
    }
    //end of put

    /**
     * @param key the key whose presence in the hashtabe we are inquiring about
     * @return true if the given key is present in the hashtable as a key, false if not
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public boolean containsKey(Key key){
    	if(key==null){
    		throw new NullPointerException();
    	}
    	if(this.table[hashFunction(key)] == null){
    		return false;
    	}
    	ListElement current = getListElement(key);
    	if(current.next==null){
    		return false;
    	}
    	return true;
    }
    //end of contsinsKey;

    /**
     * @param k the key whose corresponding previous element we want to return
     * @return ListElement whose next is key, null if the key is not there
     * throws IllegalArgumentException if the specified key is null
     */
    private ListElement getListElement(Key k){
    	if(k==null){
			throw new IllegalArgumentException();
		}
    	if(this.table[hashFunction(k)] == null){
    		return null;
    	}
    	ListElement current = this.table[hashFunction(k)];
    	while((current.next!= null) && !(current.next.entry.key.equals(k))){
    		current = current.next;
    	}
    	return current;
    }

    private void doubleAndRehash(){
    	ListElement<?>[] temp = java.util.Arrays.copyOf(this.table, this.table.length);
    	int len = this.table.length;
    	this.table = new ListElement[len*2];

    	//Iterating over the whole table; resetting elementCount so that it doesn't mess up the additional put command below
    	elementCount=0;
    	ListElement current=null;
    	for(int i=0;i<temp.length;i++){ //iterating over table elements
    		if(temp[i]!=null){ //checking that there is an element in spot i
    			current=temp[i].next;
    			while(current!=null){ //iterating over the chained list at spot i
    				this.put((Key)current.entry.key, (Value)current.entry.value);
    				current=current.next;
    			}//end of while
    		}//end of if
    	}//end of for
    }

}