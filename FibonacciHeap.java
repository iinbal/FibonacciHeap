package src;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.source.doctree.SerialFieldTree;

/**
 * FibonacciHeap
 *
 * An implementation of Fibonacci heap over positive integers.
 *
 */
public class FibonacciHeap
{
	public HeapNode minNode;
	public int total_nodes = 0;
	public int numOfRoots = 0;
	private int totalCutsCnt = 0;
	private int totalLinksCnt = 0;

	// set min and increase numOfRoots in case it's not null
	public FibonacciHeap(HeapNode min){
		minNode = min;
		if (min != null){
			numOfRoots++;	
		}
	}

	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapNode.
	 *
	 */
	public HeapNode insert(int key, String info) 
	{    
		HeapNode node = new HeapNode(key, info);
		// if the heap is not empty
        if (minNode != null) { 
          	// Add the new node to the root list
			  addToRoots(node); 
			// Update the minNode pointer if necessary
            if (key < minNode.key) {
                minNode = node; 
            }
		// else, tree is empty
        } else {
          	// Set the new node as the minNode if the heap was empty
            minNode = node;
			minNode.next = node;
			minNode.prev = node; 
			numOfRoots++;
        }
        total_nodes++;
		return node;
	}

	/**
	 * 
	 * Return the minimal HeapNode, null if empty.
	 *
	 */
	public HeapNode findMin()
	{
		return minNode;
	}

	/**
	 * 
	 * Delete the minimal item
	 *
	 */
	public void deleteMin()
	{
		total_nodes--;
		// if the heap is not empty
		if (minNode != null){
			// if ths minNode has child
			if (minNode.child != null) {
				HeapNode newMin = searchMin(minNode.child); //search the min key among the minNode's childrens
				minNode.child.parent = null; //detach connection between minNode and his child
				FibonacciHeap newHeap = new FibonacciHeap(newMin); // create new heap from minNode's children
				HeapNode nodeToDelete = minNode;
				minNode = searchMin(nodeToDelete); //search new min in the original heap
				removeNodeFromRootList(nodeToDelete); 
				meld(newHeap); // meld the original heap with minNode's childrens heap
			}
		}
	}

	//remove node by changing pointers
	public void removeNodeFromRootList(HeapNode node) 
	{
        node.prev.next = node.next;
        node.next.prev = node.prev;
		numOfRoots--;
    }

	//search min key among the node and his brothers
	public HeapNode searchMin(HeapNode node)
	{
    	int minVal = node.key; 
    	HeapNode next = node.next;
		HeapNode newMin = node;
		// search the lowest key among all brothers
		do {
			if (next.key < minVal && next != minNode) {
				minVal = next.key;
				newMin = next;
			}
			next = next.next;
		} while (next != node);
		return newMin;
	}

	private void consolidate() {
		// create a list of all roots
		HeapNode[] rootList = new HeapNode[numOfRoots]; 
		rootList[0] = minNode; 
		if (numOfRoots > 1) {
			HeapNode node = minNode.next;
		for (i=1 ; i<rootList.length ; i++){
			rootList[i] = node;
			node = node.next;
			}
		}
		//create an array of degrees according to the maxDegree, all set to null
		int maxDegree = (int) Math.floor(Math.log(n) / Math.log(2.0));
		HeapNode[] degreeArray = new HeapNode[maxDegree + 1];
		degreeArray[minNode.rank] = minNode; // place minNode in the correct index according to it's degree
		int i = 1; //starting from second root in the list
		HeapNode currentNode = rootList[i];
		do {
			int currDegree = currentNode.rank;
			// if there is a root with the same rank, link them
			if (degreeArray[currDegree] != null){
				HeapNode nodeToLink = degreeArray[currDegree];
				HeapNode newRoot =  link(nodeToLink, currentNode);
				degreeArray[currDegree] = null; // set the rank index to null
				currDegree++; 
				degreeArray[currDegree] = newRoot; // place the new root in the right index				 
			} 
			// if the degreeArray is empty at this currentNode's rank, place the currentNode there
			else degreeArray[currDegree] = currentNode; // 
			currentNode = rootList[++i]; // continue to next root
		} while (currentNode != minNode);

		
	}
	//link two roots
	private HeapNode link(HeapNode node1, HeapNode node2){
		//set the one with the larger key as parent, and the one with the smaller key as child
		if (node1.key < node2.key){
			HeapNode parent = node1;
			HeapNode child = node2;
		}
		else {
			HeapNode parent = node2;
			HeapNode child = node1;
		}
		//make  the one with the smaller key as child of the one with the larger key
		parent.child = child;
		child.parent = parent;
		removeNodeFromRootList(child); // remove the child from the rootList
		parent.rank++; // increase parent rank
		return parent;
	}

	/**
	 * 
	 * pre: 0<diff<x.key
	 * 
	 * Decrease the key of x by diff and fix the heap. 
	 * 
	 */
	public void decreaseKey(HeapNode node, int diff) 
	{   
		node.key = node.key - diff;
		if (node.parent != null){
			if (node.key < node.parent.key){
				detachNode(node);
			}
		}

		else {
			if(node.key < minNode.key) {
				setMin(node);
			}
		}
	}

	private void detachNode(HeapNode node)
	{
		totalCutsCnt++;
		HeapNode parent = node.parent;
		if (node.prev != null) {
			node.prev.next = node.next;
		}
		if (node.next != null) {
			node.next.prev = node.prev;
		}
		node.parent = null;
		addToRoots(node);
		if (parent != null) {
			parent.child = null;
			if (parent.mark && parent.parent != null) detachNode(parent, false);
			else if (parent.parent != null) parent.mark = true;
		}
		if (node.key < minNode.key) setMin(node);
	}
		

	private void addToRoots(HeapNode node)
	{
		numOfRoots++;
		node.prev = minNode;
        node.next = minNode.next;
        minNode.next.prev = node;
        minNode.next = node;
		
	}

	private void setMin(HeapNode node)
	{
		minNode = node;
	}


	/**
	 * 
	 * Delete the node from the heap.
	 *
	 */
	public void delete(HeapNode node) 
	{   
		int negativeInfinity = Integer.MIN_VALUE;
		decreaseKey(node, negativeInfinity);
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
		if (heap2 == null || heap2.minNode == null){
			return;
		}

		// if this heap is empty, copy heap2 into it , later destroy heap2
		if (minNode == null){
			this.minNode = heap2.minNode;
			this.total_nodes = heap2.total_nodes;
			this.numOfRoots = heap2.numOfRoots;	
			this.totalCutsCnt = heap2.totalCutsCnt;
			this.totalLinksCnt = heap2.totalLinksCnt;
		
		// both heaps aren't empty, connect the rootLists
		} else {
			// HeapNode temp = minNode.next;
			addToRoots(heap2.minNode);

			// update the relevant fields
			if (heap2.minNode.key < this.minNode.key) setMin(heap2.minNode);

			this.total_nodes += heap2.total_nodes;
			this.numOfRoots += heap2.numOfRoots;
		}
		// destroy heap2 so it won't be useable afterwards
		heap2.clear();
		return;    		
		
	}
	
	private void clear() {
		minNode = null;
		// this.total_nodes = 0;
		// this.numOfRoots = 0;
		// this.totalCutsCnt = 0;
		// this.totalLinksCnt = 0;
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
		public int rank = 0;
		public boolean mark;

		public HeapNode(int key, String info){
				this.key = key;
				this.info = info;
				this.mark = false;
		}
	}
}
