package task4.shortestPathsFloyd_Warshall;

public class Task extends Thread {
    int[][] graph;
    int vertices;
    int P;
    int id;

    public Task(int id, int[][] graph, int vertices, int P) {
        this.id = id;
        this.graph = graph;
        this.vertices = vertices;
        this.P = P;
    }

    public void run() {
        int start = (int) (id * (double) vertices / P);
        int end = (int) Math.min((id + 1) * (double) vertices / P, vertices);

        for (int k = start; k < end; k++) {
            for (int i = 0; i < vertices; i++) {
                for (int j = 0; j < vertices; j++) {
                    synchronized (graph) {
                        graph[i][j] = Math.min(graph[i][k] + graph[k][j], graph[i][j]);
                    }
                }
            }
        }
    }
}