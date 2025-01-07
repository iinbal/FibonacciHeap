
import com.sun.source.doctree.SerialFieldTree;

/**
 * FibonacciHeap
 *
 * An implementation of Fibonacci heap over positive integers.
 *
 */
public class FibonacciHeap
{
	public HeapNode min;
	public int total_nodes = 0;
	public int numOfRoots;
	private int totalCutsCnt = 0;
	private int totalLinksCnt = 0;
	public HeapNode[] rootList;


	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapNode.
	 *
	 */
	public HeapNode insert(int key, String info) 
	{    
		HeapNode x = new HeapNode(key, info);
		addToRoots(x);
		total_nodes++;
		return x;
	}

	/**
	 * 
	 * Return the minimal HeapNode, null if empty.
	 *
	 */
	public HeapNode findMin()
	{
		return min;
	}

	/**
	 * 
	 * Delete the minimal item
	 *
	 */
	public void deleteMin()
	{
		total_nodes--;
		if (minChild != null) {
			totalCutsCnt++;
			HeapNode next = minChild;
			do {
				HeapNode temp = next;
				next = next.next;
				temp.prev = null;
				addToRoots(temp);
			} while (next != child);
		}

		if (min.next == min) {
			this.min = null;
			return;
		}

		if (min.prev != null) {
			min.prev.next = min.next;
		}
		if (min.next != null) {
			min.next.prev = min.prev;
		}

		HeapNode newMin = updateMin(min);
		setMin(newMin);

	}

	private HeapNode updateMin(HeapNode min){
		HeapNode newMin = min;
    	float minVal = Float.POSITIVE_INFINITY;
    	HeapNode next = min.next;
    
		do {
			if (next.key < minVal) {
				minVal = next.key;
				newMin = next;
			}
			next = next.next;
		} while (next != min);
		return newMin;
	}


	/**
	 * 
	 * pre: 0<diff<x.key
	 * 
	 * Decrease the key of x by diff and fix the heap. 
	 * 
	 */
	public void decreaseKey(HeapNode x, int diff) 
	{    
		x.key = x.key - diff;
		if (x.parent != null){
			if (x.key < x.parent.key){
				detachNode(x, false);
			}
		}

		else {
			if(x.key < this.min.key) {
				setMin(x);
			}
		}
	}

	private void detachNode(HeapNode x, boolean isDelete)
	{
		totalCutsCnt++;
		HeapNode parent = x.parent;
		if (x.prev != null) {
			x.prev.next = x.next;
		}
		if (x.next != null) {
			x.next.prev = x.prev;
		}
		x.parent = null;
		if (!isDelete) addToRoots(x);
		if (parent != null) {
			parent.child = null;
			if (parent.mark && parent.parent != null) detachNode(parent, false);
			else if (parent.parent != null) parent.mark = true;
		}
	}
		

	private void addToRoots(HeapNode x)
	{
		numOfRoots++;
		HeapNode next = this.min.next;
		this.min.next = x;
		x.next = next;
		x.prev = this.min;
		next.prev = x;
		if (x.key < this.min.key) setMin(x);
	}

	private void setMin(HeapNode x)
	{
		this.min = x;
	}

	/**
	 * 
	 * Delete the x from the heap.
	 *
	 */
	public void delete(HeapNode x) 
	{    
		decreaseKey(x, Float.POSITIVE_INFINITY);
		deleteMin();
	}


	/**
	 * 
	 * Return the total number of links.
	 * 
	 */
	public int totalLinks()
	{
		return totalLinksCnt;
	}


	/**
	 * 
	 * Return the total number of cuts.
	 * 
	 */
	public int totalCuts()
	{
		return totalCutsCnt;
	}


	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap heap2)
	{
		// if heap2 is clear, no changed needed
		if (heap2 == null || heap2.min == null){
			return;
		}

		// if this heap is empty, copy heap2 into it , later destroy heap2
		if (this.min == null){
			this.min = heap2.min;
			this.total_nodes = heap2.total_nodes;
			this.numOfRoots = heap2.numOfRoots;	
			this.rootList = heap2.rootList;	
		
		// both heaps aren't empty, connect the rootLists
		} else {
			HeapNode firstNode1 = this.min;
			HeapNode lastNode1 = getLastRoot(firstNode1, this);
			HeapNode firstNode2 = heap2.min;
			connectNodes(lastNode1, firstNode2);

			HeapNode lastNode2 = heap2.min;
			lastNode2 = getLastRoot(lastNode2, heap2);
			connectNodes(lastNode2, this.min);
		
			// update the relevant fields
			if (heap2.min.key < this.min.key){
				this.setMin(heap2.min);
			}

			this.total_nodes += heap2.total_nodes;
			this.numOfRoots += heap2.numOfRoots;
		}
		
		// destroy heap2 so it won't be useable afterwards
		heap2.min = null;
		heap2.total_nodes = 0;
		heap2.numOfRoots = 0;
		heap2.totalCutsCnt = 0;
		heap2.totalLinksCnt = 0;
		heap2.rootList = null;
		return;    		
		
	}

	private HeapNode getLastRoot(HeapNode currRoot, FibonacciHeap heap){
		while (currRoot.next!=heap.min) { 
			currRoot = currRoot.next;
		}
		return currRoot;
	}

	private void connectNodes(HeapNode node1, HeapNode node2){
		node1.next = node2;
		node2.prev = node1;
	}

	/**
	 * 
	 * Return the number of elements in the heap
	 *   
	 */
	public int size()
	{
		return total_nodes;
	}


	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees()
	{
		return numOfRoots;
	}

	/**
	 * Class implementing a node in a Fibonacci Heap.
	 *  
	 */
	public static class HeapNode{
		public int key;
		public String info;
		public HeapNode child;
		public HeapNode next;
		public HeapNode prev;
		public HeapNode parent;
		public int rank;
		public boolean mark;

		public HeapNode(int key, String info){
				this.key = key;
				this.info = info;
				this.mark = false;
		}
	}
}
