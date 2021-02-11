package multipleProducersMultipleConsumersNBuffer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * @author Gabriel Gutu <gabriel.gutu at upb.ro>
 */
public class Buffer {
    LinkedList queue;
    Semaphore producerSem;
    Semaphore consumerSem;

    public Buffer() {
        queue = new LimitedQueue(Main.N);
        producerSem = new Semaphore(Main.N);
        consumerSem = new Semaphore(0);
    }

     void put(int value) {
        try {
            producerSem.acquire();
            synchronized (this) {
                queue.add(value);
            }
            consumerSem.release();
        } catch (InterruptedException ignored) {
        }
    }

    synchronized int get() {
        int val = 0;
        if (!queue.isEmpty()) {
            try {
                consumerSem.acquire();
                synchronized (this) {
                    val = (int) queue.poll();
                }
                producerSem.release();
            } catch (InterruptedException ignored) {
            }
        }

        return val;
    }
}
