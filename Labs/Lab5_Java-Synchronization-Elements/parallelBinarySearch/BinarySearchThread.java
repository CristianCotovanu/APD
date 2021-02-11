package parallelBinarySearch;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Semaphore;

public class BinarySearchThread extends Thread {
    public int tid;
    public int pos;

    public BinarySearchThread(int tid, int pos) {
        this.tid = tid;
        this.pos = pos;
    }

    public void run() {
        if (tid == Main.P) {
            // Thread that calculates Q & R
            for (int i = 1; i < Main.N; i++) {
                if (Main.checked[i - 1] == Main.checked[i] && Main.checked[i] != 0) {
                    Main.Q = Main.Q + (i - 1) * (Main.N + 1) * Main.G - 1 + 1;
                    Main.R = Main.Q + i * (Main.N + 1) * Main.G - 1 - 1;
                }
            }
        } else {
            Main.checked[pos] = Integer.compare(Main.searchingFor, Main.v[pos]);
        }

        try {
            Main.barrier.await();
        } catch (InterruptedException | BrokenBarrierException ignored) {
        }
    }
}
