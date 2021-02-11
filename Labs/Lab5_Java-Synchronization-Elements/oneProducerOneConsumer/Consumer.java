package oneProducerOneConsumer;

public class Consumer implements Runnable {
	Buffer buffer;

	Consumer(Buffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public void run() {
		for (int i = 0; i < Main.N; i++) {
			synchronized (buffer) {
				int value = buffer.get();
				if (value == -1) {
					try {
						buffer.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				value = buffer.get();
				if (value != i) {
					System.out.println("Wrong value. I was supposed to get " + i + " but I received " + value);
					System.exit(1);
				}
				buffer.put(-1);

				buffer.notify();
			}
		}
		System.out.println("I finished Correctly");
	}

}
