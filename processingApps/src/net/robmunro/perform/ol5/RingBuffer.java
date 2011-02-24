package net.robmunro.perform.ol5;


/*************************************************************************
 *  Compilation:  javac RingBuffer.java
 *  Execution:    java RingBuffer
 *  
 *  Ring buffer (fixed size queue) implementation using a circular array
 *  (array with wrap-around).
 *
 *************************************************************************/

import java.util.Iterator;
import java.util.NoSuchElementException;

// suppress unchecked warnings in Java 1.5.0_6 and later
@SuppressWarnings("unchecked")

public class RingBuffer<Item> implements Iterable<Item> {
    private Item[] a;            // queue elements
    private int N = 0;           // number of elements on queue
    private int pos = 0;
    
    // cast needed since no generic array creation in Java
    public RingBuffer(int capacity) {
        a = (Item[]) new Object[capacity];
    }

    public boolean isEmpty() { return N == 0; }
    public int size()        { return N;      }

    public void enqueue(Item item) {
    	pos = (pos + 1) % a.length;     // wrap-around
        a[pos] = item;
        if (N<a.length)N++;
    }

    public Iterator<Item> iterator() { return new RingBufferIterator(); }

    // an iterator, doesn't implement remove() since it's optional
    private class RingBufferIterator implements Iterator<Item> {
        private int i = pos;
        public boolean hasNext()  { return i < N;                               }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            i++;
            int buffPos= pos-i;
            if (buffPos<0) {buffPos+=a.length;}
            //System.out.println(buffPos);
            return a[buffPos];
        }
    }

    public Item get(int i) {//get the ith pos back for the most rrecently added;
    	int buffPos= pos-i;
        if (buffPos<0 ) {buffPos+=N;}
    	return  a[buffPos];
    }


}


