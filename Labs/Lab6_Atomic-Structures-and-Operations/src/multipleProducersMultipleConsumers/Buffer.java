package multipleProducersMultipleConsumers;

import java.util.concurrent.ArrayBlockingQueue;

public class Buffer {
	ArrayBlockingQueue<Integer> queue;

	public Buffer() {
		queue = new ArrayBlockingQueue<>(Main.N);
	}

	void put(int value) {
		try {
			queue.put(value);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	int get() {
		return queue.poll();
	}
}
