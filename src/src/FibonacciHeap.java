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
	private boolean changeMin = true;

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

	///////////////////////////////////////// code starts here

	// set min and increase numOfRoots in case it's not null
	public FibonacciHeap(){
		minNode = null;
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
		node.next = node;
		node.prev = node;
		if (minNode == null) setMin(node); // If tree is empty set the heap and the new node as the minNode
		else addNodeToList(node, minNode); // Else, add the new node to the root list
		if (key < minNode.key) setMin(node); // Update the minNode pointer if necessary
		total_nodes++;
		numOfRoots++;
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
			// if the minNode has child, add childrens to root list
			if (minNode.child != null) addChildrenToRootList(minNode);
			// delete min node and set a new one
			HeapNode nodeToDelete = minNode;
			setMin(nodeToDelete.next);
			removeNodeFromCircularList(nodeToDelete);
			total_nodes--;
			numOfRoots--;
			if (total_nodes == 0) {
				minNode = null;
				return;
			}
			consolidate();
		}
	}

	// removing a root with children and replacing it with its children
	public void addChildrenToRootList(HeapNode node){
		HeapNode child = node.child;
		child.parent = null;
		HeapNode next = child.next;
		totalCutsCnt++;
		do {
			next.parent = null;
			totalCutsCnt++;
			next = next.next;
		} while (next != child);
		//detach connection between minNode and his children
		node.child = null;
		mergeCircularLists(minNode,child); //add the children to the root list
		numOfRoots += node.rank;
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
		node.key = node.key - diff; // decrease key
		if (node.parent != null){ // if the node is not a root
			if (node.key < node.parent.key){ // if the heap rule is violated
				detachNode(node);
			}
		} else if (changeMin) {
			// update min if necessary
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
		if (node == minNode) {
			deleteMin();
			return;
		}
		changeMin = false;
		decreaseKey(node, node.key - minNode.key + 1);
		changeMin = true;
		if (node.child != null) addChildrenToRootList(node);
		removeNodeFromCircularList(node);
		numOfRoots--;
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
			mergeCircularLists(minNode, heap2.minNode);
			// update the relevant fields
			if (heap2.minNode.key < this.minNode.key) setMin(heap2.minNode);
			this.total_nodes += heap2.total_nodes;
			this.numOfRoots += heap2.numOfRoots;
			this.totalCutsCnt += heap2.totalCutsCnt;
			this.totalLinksCnt += heap2.totalLinksCnt;
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


	// redefining the heap entirely
	private void clear() {
		minNode = null;
		this.total_nodes = 0;
		this.numOfRoots = 0;
		this.totalCutsCnt = 0;
		this.totalLinksCnt = 0;
	}


	// updating the minimum
	private void setMin(HeapNode node){
		minNode = node;
	}


	// adding a new root
	private void addNodeToList(HeapNode node, HeapNode other) {
		node.prev = other;
		node.next = other.next;
		other.next.prev = node;
		other.next = node;
	}

	private void mergeCircularLists(HeapNode a, HeapNode b) {
		if (a == null || b == null) return;
		HeapNode aNext = a.next;
		HeapNode bPrev = b.prev;
		a.next = b;
		b.prev = a;
		aNext.prev = bPrev;
		bPrev.next = aNext;
	}



	// removing a root with no children
	public void  removeNodeFromCircularList(HeapNode node) {
		node.prev.next = node.next;
		node.next.prev = node.prev;
		node.next = node;
		node.prev = node;
	}

	// recursive detaching nodes
	private void detachNode(HeapNode node){
		removeNodeFromCircularList(node);
		totalCutsCnt++;
		// save the new child of node parent after cut node
		HeapNode parent = node.parent;
		if (parent != null) {
			if (node.next == node) parent.child = null;
			else parent.child = node.next;
			parent.rank--;
		}
		node.parent = null;
		addNodeToList(node, minNode); // add node to root list
		numOfRoots++;
		node.mark = false;
		if (parent != null) {
			if (parent.mark && parent.parent != null) detachNode(parent);
			else if (parent.parent != null) parent.mark = true;
		}
		if (node.key < minNode.key && changeMin) setMin(node);
	}


	private void consolidate() {
		// create a list of all roots
		if (minNode == null) return;
		int rootNum = numTrees();
		HeapNode[] rootList = new HeapNode[rootNum];
		HeapNode current = minNode;
		for (int i = 0; i < rootNum; i++) {
			rootList[i] = current;
			current = current.next;
		}
		//create an array of degrees according to the maxDegree, all set to null
		int n = total_nodes;
		int maxDegree = (int) (Math.floor(Math.log(n) / Math.log(2.0))) + 1;
		HeapNode[] degreeArray = new HeapNode[maxDegree];

		for (HeapNode node : rootList) {
			if (node == null) continue;
			int rank = node.rank;
			while (degreeArray[rank] != null) {
				HeapNode other = degreeArray[rank];
				if (other.key < node.key) {
					HeapNode temp = node;
					node = other;
					other = temp;
				}
				link(other, node);
				degreeArray[rank] = null;
				rank++;
			}
			degreeArray[rank] = node;
		}
		afterConsolidation(degreeArray);
	}

	private void afterConsolidation(HeapNode[] roots) {
		minNode = null;
		for (HeapNode node : roots) {
			if (node != null) {
				if (minNode == null) {
					minNode = node;
					node.next = node;
					node.prev = node;
				} else {
					addNodeToList(node, minNode);
					if (node.key < minNode.key) {
						setMin(node);
					}
				}
			}
		}
	}

	private void link(HeapNode y, HeapNode x) {
		HeapNode xChild = x.child;
		y.parent = x;
		if (xChild == null) {
			x.child = y;
			y.next = y;
			y.prev = y;
		} else {
			addNodeToList(y, xChild);
		}
		x.rank++;
		numOfRoots--;
		y.mark = false;
		totalLinksCnt++;
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