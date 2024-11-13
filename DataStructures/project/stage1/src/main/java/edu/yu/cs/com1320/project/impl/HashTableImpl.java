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

	public HashTableImpl(){
		this.table = new ListElement[5];
	};
	//end of constructor

	private int hashFunction(Key k){
		return (k.hashCode() & 0x7fffffff) % this.table.length;
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
    	ListElement current = getListElement(k);
    	if(current==null){
    		this.table[hashFunction(k)] = new ListElement(null,null); //pointer
			this.table[hashFunction(k)].next = new ListElement(k,v);
			return null;
    	}

    	if(current.next == null){  //if the key is not present
    		current.next=new ListElement(k,v); 
    		return (Value)current.entry.value;
    	}

    	if(current.next.entry.key.equals(k)){ //if the key is already present
    		Value temp = (Value)current.next.entry.value;
    		if(v==null){
    			current.next=current.next.next;
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
     * @throws NullPointerException if the specified key is null
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
}