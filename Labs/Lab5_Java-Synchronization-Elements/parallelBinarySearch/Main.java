package parallelBinarySearch;

import java.util.concurrent.CyclicBarrier;

public class Main {
    public static final int P = 4;
    public static CyclicBarrier barrier;

    public static final int[] v = new int[]{1, 1, 2, 3, 5, 5, 6, 6, 7, 7, 7, 8, 8, 9, 9, 9};
    public static final int N = v.length;
    public static final int searchingFor = 3;

    public static final int[] checked = new int[v.length + 1];

    public static int Q = 1;
    public static int R = N;
    public static int G = (int) (Math.log(v.length + 1) / Math.log(P + 1));

    public static void main(String[] args) {
        BinarySearchThread[] threads = new BinarySearchThread[P + 1];
        barrier = new CyclicBarrier(P + 1);

        checked[0] = 1;           // left  = -1
        checked[v.length] = -1;   // right =  1

        for (int tid = 0; tid <= P; tid++) {
            int pos = Math.min((tid + 1) * (R - Q + 1) / P, R - 1);
            threads[tid] = new BinarySearchThread(tid, pos);
            threads[tid].start();
        }

        for (int tid = 0; tid <= P; tid++) {
            try {
                threads[tid].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < N; i++) {
            if (checked[i] == 0) {
                System.out.println();
                break;
            }
        }
    }
}
