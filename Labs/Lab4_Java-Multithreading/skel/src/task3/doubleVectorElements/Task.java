package task3.doubleVectorElements;

public class Task extends Thread {
    private final int id;
    private final int[] arr;
    private final int P;
    private final int N;

    public Task(int id, int P, int N, int[] arr) {
        this.id = id;
        this.P = P;
        this.N = N;
        this.arr = arr;
    }

    public void run() {
        int start = (int) (id * (double)N / P);
        int end = (int) Math.min((id + 1) * (double)N / P, N);

        for (int i = start; i < end; i++) {
            arr[i] *= 2;
        }
    }
}