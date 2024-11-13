package edu.yu.cs.com1320.project.impl;

import java.util.*;

public class TrieImpl<Value> implements edu.yu.cs.com1320.project.Trie<Value>{

    private static final int alphabetSize=128;
    private Node root;
    private Set<Value> temporary;

    public static class Node<Value>{
        protected Set<Value> vals = null;
        protected Node[] links = new Node[TrieImpl.alphabetSize];
    }

    public TrieImpl(){
        this.root = new Node();
        this.temporary = null;
    }

	/**
     * add the given value at the given key
     * @param key
     * @param val
     */
	@Override
    public void put(String key, Value val){
        //deleteAll the value from this key
        if(key==null){
            throw new IllegalArgumentException();
        }
        if(val == null){
            return;
        }
        else{
            this.root = put(this.root, key, val, 0);
        }
    }

    private Node put(Node x, String key, Value val, int d){
        //create a new node
        if(x == null){
            x = new Node();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length()){
            if(x.vals==null){
                x.vals = new HashSet<>();
            }
            x.vals.add(val);
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        x.links[c] = this.put(x.links[c], key, val, d + 1);
        return x;
    }

    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE SENSITIVE.
     * @param key
     * @param comparator used to sort  values
     * @return a List of matching Values, in descending order
     */
    @Override
    public List<Value> getAllSorted(String key, Comparator<Value> comparator){
        if(key==null || comparator==null){
            throw new IllegalArgumentException();
        }
        Node x = get(this.root, key, 0);
        if(x==null){
            return new ArrayList<>();
        }
        else{
            //sort using comparator
            if(x.vals==null){
                return new ArrayList<>();
            }
            List temp = new ArrayList<>(x.vals);
            Collections.sort(temp, comparator);
            return temp;
        }
    }

    private Node get(Node x, String key, int d){
        //link was null - return null, indicating a miss
        if (x == null){
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length()){
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[c], key, d + 1);
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE SENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator){
        if(prefix==null){
            throw new IllegalArgumentException();
        }
        Node x = get(this.root, prefix, 0);
        if(x==null){
            return new ArrayList<>();
        }
        else{
            //recursively get all the values, then sort with comparator
            temporary = new HashSet<>();
            traverseSubtree(x, false);
            List temp = new ArrayList<>(temporary);
            temporary=null;
            Collections.sort(temp, comparator);
            return temp;
        }
    }

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE SENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAllWithPrefix(String prefix){
        if(prefix==null){
            throw new IllegalArgumentException();
        }
        this.root = delete(this.root, prefix, 0, null, true);
        if(temporary==null){
            return new HashSet<>();
        }
        Set<Value> temp = temporary;
        temporary = null;
        return temp;
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     * @param key
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAll(String key){
        if(key==null){
            throw new IllegalArgumentException();
        }
        else{
            this.root = delete(this.root, key, 0, null, false);
        }
        if(temporary==null){
            return new HashSet<>();
        }
        else{
            Set<Value> temp = temporary;
            temporary = null;
            return temp;
        }
    }

    private Node delete(Node x, String key, int d, Value val, Boolean allWithPrefix){
        if (x == null){
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length()){
            if(allWithPrefix==false){
                if(val==null || x.vals==null){
                    temporary=x.vals;
                    x.vals = null;
                }
                else{
                    Boolean isValuePresent=x.vals.remove(val);
                    if(isValuePresent==false){
                        temporary=null;
                    }
                    else{
                        temporary=x.vals;
                    }
                    if(x.vals.isEmpty()){
                        x.vals=null;
                    }
                }
            }
            else{
                temporary=new HashSet<>();
                this.traverseSubtree(x, true);
            }
        }
        //continue down the trie to the target node
        else{
            char c = key.charAt(d);
            x.links[c] = this.delete(x.links[c], key, d + 1, val, allWithPrefix);
        }
        //this node has a val – do nothing, return the node
        if (x.vals != null){
            return x;
        }
        //remove subtrie rooted at x if it is completely empty  
        for (int c = 0; c <TrieImpl.alphabetSize; c++){
            if (x.links[c] != null){
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

    private void traverseSubtree(Node x, Boolean deleteNodes){
        for(int i=0; i<alphabetSize; i++){
            if(x.links[i]!=null){
                traverseSubtree(x.links[i], deleteNodes);
            }
        }
        if(x.vals!=null){
            temporary.addAll(x.vals);
        }
        if(deleteNodes){
            x=null;
        }
    }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    @Override
    public Value delete(String key, Value val){
        if(key==null || val==null){
            throw new IllegalArgumentException();
        }
        this.root=delete(this.root, key, 0, val, false);
        if(temporary==null){
            return null;
        }
        else{
            temporary=null;
            return val;
        }
    }
}