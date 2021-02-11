package task1;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class PathFindingRunnable implements Runnable {
    private final ArrayList<Integer> partialPath;
    private final int destination;
    private final int source;
    private final ExecutorService tpe;
    private final AtomicInteger inQueue;

    public PathFindingRunnable(int source, int destination, ArrayList<Integer> partialPath, ExecutorService tpe, AtomicInteger inQueue) {
        this.partialPath = partialPath;
        this.destination = destination;
        this.source = source;
        this.tpe = tpe;
        this.inQueue = inQueue;
    }

    @Override
    public void run() {
        if (partialPath.get(partialPath.size() - 1) == destination) {
            System.out.println(partialPath);
        } else {
            int lastNodeInPath = partialPath.get(partialPath.size() - 1);
            for (int[] ints : Main.graph) {
                if (ints[0] == lastNodeInPath) {
                    if (partialPath.contains(ints[1]))
                        continue;
                    ArrayList<Integer> newPartialPath = new ArrayList<>(partialPath);
                    newPartialPath.add(ints[1]);
                    inQueue.incrementAndGet();
                    tpe.submit(new PathFindingRunnable(source, destination, newPartialPath, tpe, inQueue));
                }
            }
        }

        int left = inQueue.decrementAndGet();
        if (left == 0) {
            tpe.shutdown();
        }
    }
}
