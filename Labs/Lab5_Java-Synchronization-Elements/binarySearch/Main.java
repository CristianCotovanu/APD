package binarySearch;

import java.util.concurrent.CyclicBarrier;

public class Main {
    public final static int P = 8;
    public final static int SIZE = 100;
    public static int[] v = new int[SIZE];

    public final static int targetElement = 68;
    public static int positionElement = -1;
    public static volatile boolean foundElement = false;
    public static CyclicBarrier barrier = new CyclicBarrier(P);

    public static int left = 0;
    public static int right = SIZE - 1;

    public static void main(String[] args) {
        Thread[] threads = new Thread[P];

        for (int i = 0; i < SIZE; i++) {
            v[i] = i * 2;
        }

        for (int i = 0; i < P; i++) {
            threads[i] = new ParallelSearch(i);
            threads[i].start();
        }

        for (var thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (foundElement) {
            System.out.println("Found position = " + Main.positionElement);
        } else {
            System.out.println("Element not found");
        }
    }
}
