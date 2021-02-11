package task1;

public class Main {
    public static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        Task[] threads = new Task[NUMBER_OF_THREADS];

        for (int i = 0; i < NUMBER_OF_THREADS; ++i) {
            threads[i] = new Task(i);
            threads[i].start();
        }

        for (int i = 0; i < NUMBER_OF_THREADS; ++i) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
