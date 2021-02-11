package com.company;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Main {
    public static int P = 4;
    public static int N = 100;

//    public static CyclicBarrier barrier;
//    public static Semaphore sem;

    public static void main(String[] args) {
        Thread[] threads = new Thread[P];
//        sem = new Semaphore(P); // lasa un P maxim sa intre
//        barrier = new CyclicBarrier(P);       // cand P thread ajung la await, le lasa sa treaca

        for (int tid = 0; tid < P; tid++) {
            threads[tid] = new MyThread(tid);
            threads[tid].start();
        }


        for (var thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
