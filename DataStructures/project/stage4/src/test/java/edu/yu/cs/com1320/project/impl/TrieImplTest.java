package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class TrieImplTest{

	public class IntComp<T> implements Comparator<T>{
		@Override
		public int compare(T o1, T o2){
			return (Integer)o2 - (Integer)o1;
		}
	}

	IntComp<Integer> intComp = new IntComp<>();
	TrieImpl<Integer> trieTest = new TrieImpl<>();
	Set<Integer> hiValSet = new HashSet<>();
	Set<Integer> helloValSet = new HashSet<>();
	List<Integer> hiValList = null;
	List<Integer> helloValList = null;

	public void initialize(){
		hiValSet.add(1);
		hiValSet.add(11);
		helloValSet.add(2);
		trieTest.put("hi", 1);
		trieTest.put("hello", 2);
		trieTest.put("hi", 11);
		hiValList = new ArrayList<>();
		hiValList.add(11);
		hiValList.add(1);
		helloValList = new ArrayList<>(helloValSet);
	}

	@Test
	public void getSortedTest(){
		initialize();
		assertEquals(new ArrayList<>(), trieTest.getAllSorted("h",intComp));
		assertEquals(new ArrayList<>(), trieTest.getAllSorted("a",intComp));
		assertEquals(hiValList,trieTest.getAllSorted("hi",intComp),"the value list of key hi should be (1, 11)");
		assertEquals(helloValList,trieTest.getAllSorted("hello",intComp),"the value list of key hello should be (2)");
		List<Integer> temp = new ArrayList<>(3);
		temp.add(11);
		temp.add(2);
		temp.add(1);
		assertEquals(temp,trieTest.getAllWithPrefixSorted("h",intComp),"the values of prefix h should be (11,2,1)");
	}

	
	@Test
	public void deleteAllTest(){
		initialize();
		assertEquals(new HashSet<>(), trieTest.deleteAll("h"));
		assertEquals(new HashSet<>(), trieTest.deleteAll("a"));
		assertEquals(hiValSet,trieTest.deleteAll("hi"),"the deleted value set of key hi should be (1, 11)");
		assertEquals(new ArrayList<>(), trieTest.getAllSorted("hi",intComp));
	}

	@Test
	public void deleteAllWithPrefixTest(){
		initialize();
		assertEquals(new HashSet<>(), trieTest.deleteAll("h"));
		assertEquals(new HashSet<>(), trieTest.deleteAll("a"));
		Set<Integer> temp = new HashSet<>();
		temp.add(1);
		temp.add(2);
		temp.add(11);
		assertEquals(temp,trieTest.deleteAllWithPrefix("h"),"the deleted value set of prefix h should be (1, 2, 11)");
	}

	@Test
	public void deleteTest(){
		initialize();
		assertEquals(null, trieTest.delete("h",1));
		assertEquals(null, trieTest.delete("a",1));
		assertEquals(null, trieTest.delete("hi",3));
		assertEquals(1, trieTest.delete("hi",1));
		assertEquals(11, trieTest.delete("hi",11));
	}
}