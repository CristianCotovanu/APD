package binarySearch;

import java.util.concurrent.BrokenBarrierException;

public class ParallelSearch extends Thread {
    private final int id;
    private int previousLeft;
    private int previousRight;


    public ParallelSearch(int id) {
        super();
        this.id = id;
        this.previousLeft = 0;
        this.previousRight = 0;
    }

    @Override
    public void run() {
        while (!Main.foundElement) {
            int start = Main.left + id * (int) Math.ceil((double) (1 + Main.right - Main.left) / Main.P);
            int end = Math.min(Main.left + (id + 1) * (int) Math.ceil((double) (1 + Main.right - Main.left) / Main.P),
                    Main.SIZE) - 1;

            try {
                Main.barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }

            if (start > end || (start == previousLeft && end == previousRight)) {
                break;
            }

            previousRight = end;
            previousLeft = start;

            if (Main.v[start] == Main.targetElement) {
                Main.foundElement = true;
                Main.positionElement = start;
            } else if (Main.v[end] == Main.targetElement) {
                Main.foundElement = true;
                Main.positionElement = end;
            } else if (Main.v[start] <= Main.targetElement && Main.v[end] >= Main.targetElement) {
                Main.left = start;
                Main.right = end;
            }

            try {
                Main.barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
