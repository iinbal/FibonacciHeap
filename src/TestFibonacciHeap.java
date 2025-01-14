package src;

public class TestFibonacciHeap {
    public static void main(String[] args) {
        // Create a new Fibonacci Heap
        FibonacciHeap heap = new FibonacciHeap(null);

        // Insert some elements into the heap
        System.out.println("Inserting elements into the heap:");
        FibonacciHeap.HeapNode node1 = heap.insert(10, "Node 10");
        FibonacciHeap.HeapNode node2 = heap.insert(5, "Node 5");
        FibonacciHeap.HeapNode node3 = heap.insert(20, "Node 20");
        FibonacciHeap.HeapNode node4 = heap.insert(3, "Node 3");
        
        System.out.println("Heap size: " + heap.size());
        System.out.println("Min node: " + heap.findMin().key + " (" + heap.findMin().info + ")");

        // Test decreaseKey operation
        System.out.println("\nDecreasing key of Node 20 to 2:");
        heap.decreaseKey(node3, 18); // Reduce key from 20 to 2
        System.out.println("Min node after decreaseKey: " + heap.findMin().key + " (" + heap.findMin().info + ")");

        // Test deleteMin operation
        System.out.println("\nDeleting the minimum node:");
        heap.printHeap();
        heap.deleteMin();
        System.out.println("Min node after deleteMin: " + heap.findMin().key + " (" + heap.findMin().info + ")");
        System.out.println("Heap size after deleteMin: " + heap.size());

        // Create a second heap and test meld operation
        FibonacciHeap heap2 = new FibonacciHeap(null);
        heap2.insert(7, "Node 7");
        heap2.insert(15, "Node 15");
        heap.printHeap();
        heap2.printHeap();

        System.out.println("\nMelding two heaps:");
        heap.meld(heap2);
        heap.printHeap();
        System.out.println("Heap size after meld: " + heap.size());
        System.out.println("Min node after meld: " + heap.findMin().key + " (" + heap.findMin().info + ")");

        // Test delete operation
        System.out.println("\nDeleting Node 10:");
        heap.delete(node1);
        heap.printHeap();
        System.out.println("Heap size after delete: " + heap.size());
        System.out.println("Min node after delete: " + heap.findMin().key + " (" + heap.findMin().info + ")");

        //Final heap statistics
        System.out.println("\nFinal heap statistics:");
        System.out.println("Total number of cuts: " + heap.totalCuts());
        System.out.println("Total number of links: " + heap.totalLinks());
        System.out.println("Number of trees: " + heap.numTrees());
    }
}