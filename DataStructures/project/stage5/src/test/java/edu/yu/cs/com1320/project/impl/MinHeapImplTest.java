package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.NoSuchElementException;
import edu.yu.cs.com1320.project.MinHeap;

public class MinHeapImplTest{
	MinHeapImpl<String> heapTest = new MinHeapImpl();

	public void initialize(){
		this.heapTest.insert("Hi");
		this.heapTest.insert("Hello");
		this.heapTest.insert("Zoe");
		this.heapTest.insert("Aaron");
	}

	@Test
	public void getArrayIndexTest(){
		initialize();
		assertEquals(4, this.heapTest.getArrayIndex("Hi"), "index of hi should be 4");
		assertThrows(NoSuchElementException.class,()->{
			this.heapTest.getArrayIndex("bbb");
		});
	}

	@Test
	public void doubleArraySizeTest(){
		for(int i=1; i<13; i++){
			this.heapTest.insert("" + i);
		}
		assertEquals(1, this.heapTest.getArrayIndex("1"));
	}

	/*
	@Test
	public void reHeapifyTest(){
		initialize();
		int k = this.heapTest.getArrayIndex("Hi");
		this.heapTest.elements[k]="Aaaa";
		this.heapTest.reHeapify("Aaaa");
		k=this.heapTest.getArrayIndex("Aaaa");
		assertEquals(1, k, "Aaaa should be at the first place after re-heapify");
		this.heapTest.elements[k]="Zzzz";
		this.heapTest.reHeapify("Zzzz");
		assert(this.heapTest.getArrayIndex("Zzzz")!=1);
	}
	*/
}