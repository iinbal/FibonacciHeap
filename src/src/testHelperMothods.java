public int potential() {
		int t = 0; // Number of trees (roots)
		int m = 0; // Number of marked nodes
		HeapNode current = min;

		if (current != null) {
			// Traverse the circular linked list of roots
			do {
				t++; // Each root is a tree
				m += countMarkedNodes(current); // Count marked nodes in the tree
				current = current.next;
			} while (current != min);
		}

		return t + 2 * m; // Potential is t + 2 * m
	}

	// Helper method to count marked nodes in a tree
	private int countMarkedNodes(HeapNode node) {
		int count = 0;
		while (node != null) {
			if (node.mark) count++; // Increment if the node is marked
			node = node.child;
		}
		return count;
	}

	public int[] countersRep() {
		int[] counters = new int[calculateMaxRank()]; // Array to store the number of trees of each rank
		HeapNode current = min;

		// Traverse the root list and count trees by rank
		if (current != null) {
			do {
				int rank = current.rank;
				counters[rank]++; // Increment the count of trees of this rank
				current = current.next;
			} while (current != min);
		}

		return counters;
	}

	// Helper method to calculate the maximum possible rank (based on the number of nodes in the heap)
	private int calculateMaxRank() {
		return (int) Math.ceil(Math.log(size) / Math.log(2)) + 1;
	}