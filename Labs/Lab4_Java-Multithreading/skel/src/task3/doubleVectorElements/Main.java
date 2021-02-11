package task3.doubleVectorElements;

/**
 * @author cristian.chilipirea
 */
public class Main {
    public static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final int N = 100000013;

    public static void main(String[] args) {
        int[] v = new int[N];

        for (int i = 0; i < N; i++)
            v[i] = i;

        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        for (int tid = 0; tid < NUMBER_OF_THREADS; tid++) {
            threads[tid] = new Task(tid, NUMBER_OF_THREADS, N, v);
            threads[tid].start();
        }

        for (int tid = 0; tid < NUMBER_OF_THREADS; tid++) {
            try {
                threads[tid].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < N; i++) {
            if (v[i] != i * 2) {
                System.out.println("Wrong answer");
                System.exit(1);
            }
        }

        System.out.println("Correct");
    }

    private static void arrMultiplySeq(int[] v) {
        for (int i = 0; i < N; i++) {
            v[i] = v[i] * 2;
        }
    }
}
