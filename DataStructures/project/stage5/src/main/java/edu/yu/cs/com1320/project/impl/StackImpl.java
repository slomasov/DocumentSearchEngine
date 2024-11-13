package edu.yu.cs.com1320.project.impl;

public class StackImpl<T> implements edu.yu.cs.com1320.project.Stack<T>{

    private T[] data;
    private int top;

    public StackImpl(){
        data = (T[]) new Object[11];
        top = -1;
    }

    /**
     * @param element object to add to the Stack
     */
	@Override
    public void push(T element){
        if(element==null){
            return;
        }
        if(top==this.data.length-1){
            T[] temp = (T[]) new Object[this.data.length*2];
            for(int i = 0; i<this.data.length; i++){
                temp[i]=this.data[i];
            }
            this.data=temp;
        }
        top++;
        this.data[top]=element;
    }

    /**
     * removes and returns element at the top of the stack
     * @return element at the top of the stack, null if the stack is empty
     */
    @Override
    public T pop(){
        if(top==-1){
            return null;
        }
        else{
            T temp = this.data[top];
            this.data[top]=null;
            top--;
            return temp;
        }
    }

    /**
     *
     * @return the element at the top of the stack without removing it
     */
    @Override
    public T peek(){
        if(top==-1){
            return null;
        }
        return this.data[top];
    }

    /**
     *
     * @return how many elements are currently in the stack
     */
    @Override
    public int size(){
        return top+1;
    }
}