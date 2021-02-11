package task2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class GraphColoringRunnable implements Runnable {
    private final int[] colors;
    private final int step;
    private final ExecutorService tpe;
    private final AtomicInteger inQueue;

    GraphColoringRunnable(int[] colors, int step, ExecutorService tpe, AtomicInteger inQueue) {
        this.colors = colors;
        this.step = step;
        this.tpe = tpe;
        this.inQueue = inQueue;
    }

    @Override
    public void run() {
        if (step == Main.N) {
            Main.printColors(colors);
        } else {
            for (int i = 0; i < Main.COLORS; i++) {
                int[] newColors = colors.clone();
                newColors[step] = i;
                if (Main.verifyColors(newColors, step)) {
                    inQueue.incrementAndGet();
                    tpe.submit(new GraphColoringRunnable(newColors, step + 1, tpe, inQueue));
                }
            }
        }

        int left = inQueue.decrementAndGet();
        if (left == 0) {
            tpe.shutdown();
        }
    }
}
