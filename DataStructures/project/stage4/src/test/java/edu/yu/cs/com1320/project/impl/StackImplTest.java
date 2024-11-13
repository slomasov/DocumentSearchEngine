package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StackImplTest{
	StackImpl<Integer> st = new StackImpl<>();

	@Test
	public void creationTest(){
		assert(st.pop()==null);
		assert(st.peek()==null);
		assert(st.size()==0);
		st.push(null);
		assert(st.size()==0);
	}

	@Test
	public void allTest(){
		st.push(0);
		assert(st.peek()==0);
		assert(st.size()==1);
		for(int i=1;i<11;i++){
			st.push(i);
		}
		assert(st.peek()==10);
		assert(st.size()==11);
		st.push(11);
		st.push(12);
		assert(st.peek()==12);
		assert(st.size()==13);
		int temp = 0;
		for(int i=12; i>8; i--){
			temp = st.pop();
			assert(temp == i);
		}
		assert(st.peek()==8);
		assert(st.size()==9);
	}
}