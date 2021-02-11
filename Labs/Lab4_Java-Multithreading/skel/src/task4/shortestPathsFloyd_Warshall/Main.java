package task4.shortestPathsFloyd_Warshall;

/**
 * @author cristian.chilipirea
 */
public class Main {
	public static final int NUMBER_OF_THREADS = 2;

	public static void main(String[] args) {
        int M = 9;
        int[][] graph = {{0, 1, M, M, M},
						 {1, 0, 1, M, M},
						 {M, 1, 0, 1, 1},
						 {M, M, 1, 0, M},
						 {M, M, 1, M, 0}};

		floydWarshallSeq(graph);

		Thread[] threads = new Thread[NUMBER_OF_THREADS];
		for (int tid = 0; tid < NUMBER_OF_THREADS; tid++) {
			threads[tid] = new Task(tid, graph, graph.length, NUMBER_OF_THREADS);
			threads[tid].start();
		}

		for (int tid = 0; tid < NUMBER_OF_THREADS; tid++) {
			try {
				threads[tid].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		int[][] seqFWGraph = floydWarshallSeq(graph);

		if (graphsAreEqual(graph, seqFWGraph)) {
			System.out.println("Correct");
			printGraph(graph);
		} else {
			System.out.println("Graphs differ");
			printGraph(graph);
			System.out.println();
			printGraph(seqFWGraph);
		}
    }

	private static int[][] floydWarshallSeq(int[][] graph) {
		int[][] newGraph = copyGraph(graph);

		for (int k = 0; k < graph.length; k++) {
			for (int i = 0; i < graph.length; i++) {
				for (int j = 0; j < graph.length; j++) {
					newGraph[i][j] = Math.min(newGraph[i][k] + newGraph[k][j], newGraph[i][j]);
				}
			}
		}

		return newGraph;
	}

	private static boolean graphsAreEqual(int[][] g1, int[][] g2) {
		for (int i = 0; i < g1.length; i++) {
			for (int j = 0; j < g1[0].length; j++) {
				if (g1[i][j] != g2[i][j])
					return false;
			}
		}
		return true;
	}

	private static int[][] copyGraph(int[][] graph) {
		int[][] newGraph = new int[graph.length][graph.length];
		for (int i = 0; i < graph.length; i++) {
			for (int j = 0; j < graph.length; j++) {
				newGraph[i][j] = graph[i][j];
			}
		}
		return newGraph;
	}

	private static void printGraph(int[][] graph) {
		for (int i = 0; i < graph.length; i++) {
			for (int j = 0; j < graph[0].length; j++) {
				System.out.print(graph[i][j] + " ");
			}
			System.out.println();
		}
	}
}
