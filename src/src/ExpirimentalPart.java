package src;
import java.util.Random;

public class ExpirimentalPart {
    public static void main(String[] args) {
        // Test 1
        // Insert the HeapNodes 1,...,n to the heap in a random order, then do deleteMin() once
        System.out.println("Test 1");
        for (int i = 1; i <= 5; i++) {
            int n = (int) Math.pow(3, i + 7) - 1;

            long totalTime = 0;
            long totalHeapSize = 0;
            long totalTotalLinks = 0;
            long totalTotalCuts = 0;
            long totalNumTrees = 0;
            for (int j = 0; j < 20; j++) {
                int[] values = new int[n];
                for (int k = 0; k < n; k++) {
                    values[k] = k + 1;
                }
                shuffleArray(values);

                FibonacciHeap.HeapNode minNode = null;
                FibonacciHeap heap = new FibonacciHeap(); //needed to add a minNode null
                long startTime = System.currentTimeMillis();
                for (int k = 0; k < n; k++) {
                    heap.insert(values[k], "");
                }
                heap.deleteMin();
                long endTime = System.currentTimeMillis();
                totalTime += endTime - startTime;
                totalHeapSize += heap.size();
                totalTotalLinks += heap.totalLinks();
                totalTotalCuts += heap.totalCuts();
                totalNumTrees += heap.numTrees();
            }
            System.out.println("n = " + n);
            System.out.println("Average running time: " + totalTime / 20 + " ms");
            System.out.println("Average heap size: " + totalHeapSize / 20);
            System.out.println("Average total links: " + totalTotalLinks / 20);
            System.out.println("Average total cuts: " + totalTotalCuts / 20);
            System.out.println("Average number of trees: " + totalNumTrees / 20);
        }


        // Test 2
        // Insert the HeapNodes 1,...,n to the heap in a random order, then do deleteMin() n/2 times
        System.out.println("Test 2");
        for (int i = 1; i <= 5; i++) {
            int n = (int) Math.pow(3, i + 7) - 1;

            long totalTime = 0;
            long totalHeapSize = 0;
            long totalTotalLinks = 0;
            long totalTotalCuts = 0;
            long totalNumTrees = 0;
            for (int j = 0; j < 20; j++) {
                int[] values = new int[n];
                for (int k = 0; k < n; k++) {
                    values[k] = k + 1;
                }
                shuffleArray(values);

                FibonacciHeap.HeapNode minNode = new FibonacciHeap.HeapNode(1, "");
                FibonacciHeap heap = new FibonacciHeap(); //needed to add a minNode
                long startTime = System.currentTimeMillis();
                for (int k = 0; k < n; k++) {
                    heap.insert(values[k], "");
                }
                for (int k = 0; k < n / 2; k++) {
                    heap.deleteMin();
                }
                long endTime = System.currentTimeMillis();
                totalTime += endTime - startTime;
                totalHeapSize += heap.size();
                totalTotalLinks += heap.totalLinks();
                totalTotalCuts += heap.totalCuts();
                totalNumTrees += heap.numTrees();
            }
            System.out.println("n = " + n);
            System.out.println("Average running time: " + totalTime / 20 + " ms");
            System.out.println("Average heap size: " + totalHeapSize / 20);
            System.out.println("Average total links: " + totalTotalLinks / 20);
            System.out.println("Average total cuts: " + totalTotalCuts / 20);
            System.out.println("Average number of trees: " + totalNumTrees / 20);
        }


        // Test 3
        // Insert the HeapNodes 1,...,n to the heap in a random order, then do deleteMin() once.
        // Then, using a pointer to it, delete the maximum node until we have 2^5-1 nodes left
        // For this test, we will need an array of pointers to the nodes, where the i-th element is a pointer to a node with key i
        System.out.println("Test 3");
        for (int i = 1; i <= 5; i++) {
            int n = (int) Math.pow(3, i + 7) - 1;

            long totalTime = 0;
            long totalHeapSize = 0;
            long totalTotalLinks = 0;
            long totalTotalCuts = 0;
            long totalNumTrees = 0;
            for (int j = 0; j < 20; j++) {
                int[] values = new int[n];
                for (int k = 0; k < n; k++) {
                    values[k] = k + 1;
                }

                shuffleArray(values);
                FibonacciHeap.HeapNode minNode = null;
                FibonacciHeap heap = new FibonacciHeap(); //needed to add a minNode null
                FibonacciHeap.HeapNode[] pointers = new FibonacciHeap.HeapNode[n + 1];
                long startTime = System.currentTimeMillis();
                for (int k = 0; k < n; k++) {
                    pointers[values[k]] = heap.insert(values[k], "");
                }
                heap.deleteMin();
                int k = n;
                while (heap.size() > 31) {
                    heap.delete(pointers[k]);
                    k--;
                }
                long endTime = System.currentTimeMillis();
                totalTime += endTime - startTime;
                totalHeapSize += heap.size();
                totalTotalLinks += heap.totalLinks();
                totalTotalCuts += heap.totalCuts();
                totalNumTrees += heap.numTrees();
            }
            System.out.println("n = " + n);
            System.out.println("Average running time: " + totalTime / 20 + " ms");
            System.out.println("Average heap size: " + totalHeapSize / 20);
            System.out.println("Average total links: " + totalTotalLinks / 20);
            System.out.println("Average total cuts: " + totalTotalCuts / 20);
            System.out.println("Average number of trees: " + totalNumTrees / 20);
        }

    }
    

    private static void shuffleArray(int[] values) {
        Random rnd = new Random();
        for (int i = values.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = values[index];
            values[index] = values[i];
            values[i] = a;
        }
    }
}
