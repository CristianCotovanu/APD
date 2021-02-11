package com.company;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Semaphore;

public class MyThread extends Thread{
    int tid;

    public MyThread(int tid) {
        this.tid = tid;
    }

    @Override
    public void run() {

//        // Barrier
//        try {
//            Main.barrier.await();
//        } catch (InterruptedException | BrokenBarrierException e) {
//            e.printStackTrace();
//        }
//        sem.release();
//
//        // Semaphore
//        try {
//            sem.acquire();
//        } catch (InterruptedException ignored) {
//        }
    }


}
