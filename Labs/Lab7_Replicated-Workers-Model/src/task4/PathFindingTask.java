package task4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class PathFindingTask extends RecursiveTask {
    private final ArrayList<Integer> partialPath;
    private final int destination;
    private final int source;

    PathFindingTask(int source, int destination, ArrayList<Integer> partialPath) {
        this.partialPath = partialPath;
        this.destination = destination;
        this.source = source;
    }

    @Override
    protected Object compute() {
        if (partialPath.get(partialPath.size() - 1) == destination) {
            System.out.println(partialPath);
            return null;
        } else {
            int lastNodeInPath = partialPath.get(partialPath.size() - 1);
            List<PathFindingTask> tasks = new ArrayList<>();

            for (int[] ints : Main.graph) {
                if (ints[0] == lastNodeInPath) {
                    if (partialPath.contains(ints[1]))
                        continue;
                    ArrayList<Integer> newPartialPath = new ArrayList<>(partialPath);
                    newPartialPath.add(ints[1]);
                    PathFindingTask t = new PathFindingTask(source, destination, newPartialPath);
                    tasks.add(t);
                    t.fork();
                }
            }
            for (PathFindingTask task : tasks) {
                task.join();
            }
        }
        return null;
    }
}