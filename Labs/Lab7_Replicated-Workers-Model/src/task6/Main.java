package task6;

import task3.QueensRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static int N = 4;

    public static void main(String[] args) {
        queensFjkParallel();
    }

    private static void queensFjkParallel() {
        int[] graph = new int[N];
        ForkJoinPool fjp = new ForkJoinPool(4);
        fjp.invoke(new QueensTask(graph, 0));
        fjp.shutdown();
    }

    private static void queensSequencial() {
        int[] graph = new int[N];
        queens(graph, 0);
    }

    public static boolean check(int[] arr, int step) {
        for (int i = 0; i <= step; i++) {
            for (int j = i + 1; j <= step; j++) {
                if (arr[i] == arr[j] || arr[i] + i == arr[j] + j || arr[i] + j == arr[j] + i)
                    return false;
            }
        }
        return true;
    }

    public static void printQueens(int[] sol) {
        StringBuilder aux = new StringBuilder();
        for (int i = 0; i < sol.length; i++) {
            aux.append("(").append(sol[i] + 1).append(", ").append(i + 1).append("), ");
        }
        aux = new StringBuilder(aux.substring(0, aux.length() - 2));
        System.out.println("[" + aux + "]");
    }

    public static void queens(int[] graph, int step) {
        if (Main.N == step) {
            printQueens(graph);
            return;
        }
        for (int i = 0; i < Main.N; ++i) {
            int[] newGraph = graph.clone();
            newGraph[step] = i;

            if (check(newGraph, step)) {
                queens(newGraph, step + 1);
            }
        }
    }
}