package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HashTableTest{
	HashTableImpl<Integer,Character> htTest = new HashTableImpl<>();
	Character c1 = htTest.put(6,'c');
	Character c2 = htTest.put(1,'m');
	Character c3 = htTest.put(0,'c');
	Character c4 = htTest.put(11,'b');

	@Test
	public void rehashingPutTest(){
		for(int i=0;i<20;i++){
			this.htTest.put(i, (char)(98+i));
		}
		for(int i=20;i<22;i++){
			this.htTest.put(i, (char)(98+i));
		}
		for(int i=0; i<22; i++){
			assertEquals((char)(98+i),htTest.get(i),"wrong value while getting");
		}
	}

	@Test
	public void putTest(){
		assertEquals('m',htTest.put(1,'t'),"the old value of key 1 should be m");
		assertEquals(null,htTest.put(2,'t'), "the value of key 2 was not already present, hence null");
		assertEquals(null,htTest.put(16,'m'), "the value of key 16 was not already present, hence null");
	}

	@Test
	public void deleteTest(){
		assertEquals(true, htTest.containsKey(1),"the key 0 should be present in the table" );
		Character ct = htTest.put(1,null);
		assertEquals(false, htTest.containsKey(1),"the key 0 should have been deleted" );

	}

	@Test
	public void containsKeyTest(){
		assertEquals(false,htTest.containsKey(3),"the key 3 should not be present in the table" );
		assertEquals(true, htTest.containsKey(1),"the key 1 should be present in the table" );
		assertEquals(true, htTest.containsKey(6),"the key 6 should be present in the table" );
	}

	@Test
	public void getTest(){
		assertEquals('c',htTest.get(6),"the value of key 6 should be c");
		assertEquals('m',htTest.get(1),"the value of key 1 should be m");
		assertEquals('c',htTest.get(0),"the value of key 0 should be c");
		assertEquals(null,htTest.get(2),"the key 2 should not be present, hence null");
	}

	//@Test
	//public void hashFunctionTest(){
	//	assertEquals(1, htTest.hashFunction(-1));
	//	assertEquals(1, htTest.hashFunction(1));
	//	assertEquals(1, htTest.hashFunction(6));
	//}
}