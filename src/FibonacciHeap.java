package src;

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
	public final int Infinity = Integer.MAX_VALUE;



///////////////////////////////////////// printers

	/**
 * Print the Fibonacci heap in a hierarchical format.
 */
public void printHeap() {
    if (minNode == null) {
        System.out.println("The heap is empty.");
        return;
    }

    System.out.println("Fibonacci Heap:");
    HeapNode current = minNode;
    do {
        printSubTree(current, 0); // Print each tree starting from the root
        current = current.next;
    } while (current != minNode);
}


/**
 * Helper method to print a subtree rooted at a specific node.
 *
 * @param node  The current node to print.
 * @param depth The depth of the current node (used for indentation).
 */
private void printSubTree(HeapNode node, int depth) {
    if (node == null) {
        return;
    }

    // Indent the node based on its depth
    System.out.println("  ".repeat(depth) + "Key: " + node.key + " (Rank: " + node.rank + ")");

    // Recursively print the children of the current node
    HeapNode child = node.child;
    if (child != null) {
        HeapNode currentChild = child;
        do {
            printSubTree(currentChild, depth + 1); // Increase indentation for children
            currentChild = currentChild.next;
        } while (currentChild != child);
    }
}
	
	/*
	public HeapNode findNodeWithKey(int key) {
	    if (minNode == null) {
	        return null; // Return null if the heap is empty
	    }

	    // Start a recursive search from the root list
	    return findNodeInSubtree(minNode, key);
	}

	private HeapNode findNodeInSubtree(HeapNode node, int key) {
	    if (node == null) {
	        return null;
	    }

	    HeapNode start = node; // Save the starting point to detect cycles
	    do {
	        if (node.key == key) {
	            return node; // Return the node if the key matches
	        }

	        // Recursively search the children of the current node
	        if (node.child != null) {
	            HeapNode found = findNodeInSubtree(node.child, key);
	            if (found != null) {
	                return found; // Return the node if found in the subtree
	            }
	        }

	        node = node.next; // Move to the next sibling
	    } while (node != start); // Stop when we loop back to the starting node

	    return null; // Return null if the key is not found
	}
	 */

///////////////////////////////////////// code starts here

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
	public HeapNode insert(int key, String info) {    
		HeapNode node = new HeapNode(key, info);
		// if the heap is not empty
        if (minNode == null) setNewHeap(node,this); // If tree is empty set the heap and the new node as the minNode
		else addNewRoot(node); // Else, add the new node to the root list
        if (key < minNode.key) this.minNode = node; // Update the minNode pointer if necessary
        total_nodes++;
		return node;
	}

	/**
	 * 
	 * Return the minimal HeapNode, null if empty.
	 *
	 */
	public HeapNode findMin() {
		return minNode;
	}

	/**
	 * 
	 * Delete the minimal item
	 *
	 */

	public void deleteMin() {
		// if the heap is not empty
		if (minNode != null){
			// if the minNode has child
			if (minNode.child != null) removeNodeWithChildren(minNode);
			else removeNode(minNode);
			if (minNode != null) consolidate();
			total_nodes--;
		}
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
		if (node == null) return;
		node.key = node.key - diff;
		if (node.parent != null){
			if (node.key < node.parent.key){
				detachNode(node);
			}
		} else {
			if(node.key < minNode.key) {
				setMin(node);
			}
		}
	}

	/**
	 * 
	 * Delete the node from the heap.
	 *
	 */
	public void delete(HeapNode node) {   
		if (node == null) return;
		detachNode(node); // detach the relevant node and encrease the totalcuts
		if (node.child != null){
			removeNodeWithChildren(node);
		}
		else removeNode(node);
		total_nodes--;		
	}


	/**
	 * 
	 * Return the total number of links.
	 * 
	 */
	public int totalLinks(){
		return totalLinksCnt;
	}


	/**
	 * 
	 * Return the total number of cuts.
	 * 
	 */
	public int totalCuts(){
		return totalCutsCnt;
	}


	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap heap2){
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
			HeapNode thisNext = minNode.next;
			HeapNode heap2Prev = heap2.minNode.prev;
			minNode.next = heap2.minNode;
			heap2.minNode.prev = minNode;
			thisNext.prev = heap2Prev;
			heap2Prev.next = thisNext;
			
			// update the relevant fields
			if (heap2.minNode.key < this.minNode.key) setMin(heap2.minNode);

			this.total_nodes += heap2.total_nodes;
			this.numOfRoots += heap2.numOfRoots;
		}
		// destroy heap2 so it won't be useable afterwards
		heap2.clear();
		return;    			
	}
	
	
	/**
	 * 
	 * Return the number of elements in the heap
	 *   
	 */
	public int size(){
		return total_nodes;
	}


	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees(){
		return numOfRoots;
	}


	//helper funcs 

	// redefining the heap entirely
	private void clear() {
		minNode = null;
		this.total_nodes = 0;
		this.numOfRoots = 0;
		this.totalCutsCnt = 0;
		this.totalLinksCnt = 0;
	}

	private void setNewHeap(HeapNode newMin,FibonacciHeap newHeap) {
		if (newMin == null) {
			// Handle the case where there is no node to set
			newHeap.minNode = null; // Set the minNode to null for an empty heap
			newHeap.numOfRoots = 0; // No roots in the heap
			newHeap.total_nodes = 0; // No nodes in the heap
			return; // Exit early since there's nothing to process
		}
		
		int rootsNum = 1;
		int nodesNum = newMin.rank + 1;
		if (nodesNum == 1){
			newHeap.minNode = newMin;
			newHeap.minNode.next = newMin;
			newHeap.minNode.prev = newMin; 
			newHeap.minNode.mark = false;
		} else { 
			HeapNode node = newMin.next;
			do {
				rootsNum++;
				nodesNum += (node.rank + 1);
				node.mark = false;
				node = node.next;
			} while (node != newMin);
		}
		newHeap.numOfRoots = rootsNum;
		newHeap.total_nodes = nodesNum;
	}

	// updating the minimum
	private void setMin(HeapNode node){
		minNode = node;
	}


	//search min key among the node and his brothers
	public HeapNode searchNewMin(HeapNode node)
	{
		if (node == null) return null;
		//if (node.next == node && node != minNode) return node;
		if (node == minNode && node.next == minNode) return null; //if the min node is the only node, new Min in null
		HeapNode next = node.next;
    	int minVal = next.key; 
		HeapNode newMin = next;
		// search the lowest key among all brothers
		do {
			if (next.key < minVal && next != minNode) {
				minVal = next.key;
				newMin = next;
			}
			next = next.next;
		} while (next != node);
		if (node != minNode && node.key < newMin.key) newMin = node; // if we are serachong among che children of the minNode, the one we have sent is also
		return newMin;
	}

	// changes in rootList
	
	// adding a new root
	private void addNewRoot(HeapNode node) {
		numOfRoots++;
		node.prev = minNode;
		node.next = minNode.next;
		minNode.next.prev = node;
		minNode.next = node;
		node.mark = false;	
	}

	// removing a root with children and replacing it with its children
	public void removeNodeWithChildren(HeapNode node){
		HeapNode newMin = searchNewMin(node.child); //search for the min key among the minNode's childrens
		node.child.parent = null; //detach connection between minNode and his child
		node.child = null;
		removeNode(node);
		totalCutsCnt++;
		FibonacciHeap newHeap = new FibonacciHeap(newMin); // create new heap from minNode's children
		setNewHeap(newMin, newHeap);
		meld(newHeap); // meld the original heap with minNode's childrens heap
	}
	
	// removing a root with no children
	public void removeNode(HeapNode node) {	
		if (node == null) return;
		if (node == minNode) setMin(searchNewMin(node)); //search and set a new min in the original heap

		if (node.next == node) clear();
		else { //remove node by changing pointers
		node.prev.next = node.next;
        node.next.prev = node.prev;
		numOfRoots--;
		}
		node.next = node.prev = null;
    }

	// recursive detaching nodes
	private void detachNode(HeapNode node){
		totalCutsCnt++;
		if (node.next != node) {
			node.next.prev = node.prev;
			node.prev.next = node.next;
		}
		HeapNode parent = node.parent;
		node.parent = null;
		addNewRoot(node);
		
		if (parent != null) {
			parent.child = null;
			parent.rank--;
			if (parent.mark && parent.parent != null) detachNode(parent);
			else if (parent.parent != null) parent.mark = true;
		}
		if (node.key < minNode.key) setMin(node);
	}

	
	private void consolidate() {
		// create a list of all roots
		System.out.println("consolidate " + numOfRoots + " before");
		
		HeapNode[] rootList = new HeapNode[numOfRoots]; 
		rootList[0] = minNode; 
		if (numOfRoots > 1) {
			HeapNode node = minNode.next;
			for (int i=1 ; i<numOfRoots ; i++){
				rootList[i] = node;
				node = node.next;
				}
		}
		//create an array of degrees according to the maxDegree, all set to null
		int n = total_nodes;
		int maxDegree = (int) (Math.floor(Math.log(n) / Math.log(2.0))) + 1;
		HeapNode[] degreeArray = new HeapNode[maxDegree];
		
		for (HeapNode currentNode : rootList){
			int currDegree = currentNode.rank;
			while (degreeArray[currDegree] != null){ // if there is a root with the same rank, link them
				HeapNode nodeToLink = degreeArray[currDegree];
				currentNode = link(nodeToLink, currentNode); // updates the linkcnt and updates currentNode to be the new root of the new tree 
				degreeArray[currDegree] = null; // set the rank index to null
				currDegree++; // continue to cheack if needed more linking		 
			} 
			degreeArray[currDegree] = currentNode; // place the new root in the right index after finished linking 	
		}		
		System.out.println("consolidate " + numOfRoots + " after");

	}

	public HeapNode link(HeapNode node1, HeapNode node2){
		// name the nodes based on their keys
		HeapNode smaller;
		HeapNode larger;
		if (node1.key <= node2.key){
			smaller = node1;
			larger = node2;
		} else {
			smaller = node2;
			larger = node1;
		} 
		
		// Remove the larger node from the root list and reduce numofroots
		removeNode(larger);

		// Make the larger node a child of the smaller node
		larger.parent = smaller;

		// If the smaller node has no children, set the larger node as its child
		if (smaller.child == null) {
			smaller.child = larger;
			larger.next = larger;
			larger.prev = larger;
		} else { // Add the larger node to the child list of the smaller node
			HeapNode child = smaller.child;
			larger.next = child;
			larger.prev = child.prev;
			child.prev.next = larger;
			child.prev = larger;
		}

		// Increase the rank of the smaller node anf totalinkscnt
		smaller.rank++;
		totalLinksCnt++;

		// Return the smaller node as the root of the resulting tree
		return smaller;
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
				this.next = this;
				this.prev = this;
		}
	}
}