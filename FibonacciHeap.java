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
				removeMin();
				meld(newHeap); // meld the original heap with minNode's childrens heap
			}
			else removeMin();
			consolidate();
		}
		
	}

	private void removeMin()
	{
		HeapNode nodeToDelete = minNode;
		HeapNode newMin = searchMin(nodeToDelete); //search new min in the original heap
		removeNodeFromRootList(nodeToDelete);
		setMin(newMin);
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
		HeapNode next = node.next;
    	int minVal = next.key; 
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
			for (int i=1 ; i<rootList.length ; i++){
				rootList[i] = node;
				System.out.println("rootList in index " + i + "is " + node.key);
				node = node.next;
				}
		}
		//create an array of degrees according to the maxDegree, all set to null
		int n = total_nodes;
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
				HeapNode newParent;
        		HeapNode newChild;
				if (nodeToLink.key < currentNode.key) {
					newParent = nodeToLink;
					newChild = currentNode;
				}
				else {
					newParent = currentNode;
					newChild = nodeToLink;
				}
				HeapNode newRoot = link(newParent, newChild);
				degreeArray[currDegree] = null; // set the rank index to null
				currDegree++; 
				degreeArray[currDegree] = newRoot; // place the new root in the right index				 
			} 
			// if the degreeArray is empty at this currentNode's rank, place the currentNode there
			else degreeArray[currDegree] = currentNode; // 
			i++;
			if (i < numOfRoots) currentNode = rootList[++i]; // continue to next root
			else break;
		} while (currentNode != minNode);

		
	}

	public HeapNode link(HeapNode smaller, HeapNode larger){
		// Remove the larger node from the root list
		removeNodeFromRootList(larger);

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

		// Increase the rank of the smaller node
		smaller.rank++;

		// Return the smaller node as the root of the resulting tree
		totalLinksCnt++;
		return smaller;
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
			parent.rank--;
			if (parent.mark && parent.parent != null) detachNode(parent);
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
		int Infinity = Integer.MAX_VALUE;
		decreaseKey(node, Infinity);
		System.out.println("after deacrese key:");
		printHeap();
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