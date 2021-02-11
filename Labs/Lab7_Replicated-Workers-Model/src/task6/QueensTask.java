package task6;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class QueensTask extends RecursiveTask {
    private final int step;
    private final int[] graph;

    QueensTask(int[] graph, int step) {
        this.graph = graph;
        this.step = step;
    }

    @Override
    protected Object compute() {
        if (Main.N == step) {
            Main.printQueens(graph);
            return null;
        } else {
            List<QueensTask> tasks = new ArrayList<>();
            for (int i = 0; i < Main.N; i++) {
                int[] newGraph = graph.clone();
                newGraph[step] = i;
                if (Main.check(newGraph, step)) {
                    QueensTask t = new QueensTask(newGraph, step + 1);
                    tasks.add(t);
                    t.fork();
                }
            }

            for (QueensTask task : tasks) {
                task.join();
            }
        }

        return null;
    }
}