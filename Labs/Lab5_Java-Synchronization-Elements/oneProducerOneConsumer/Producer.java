package oneProducerOneConsumer;
/**
 * @author gabriel.gutu@upb.ro
 * 
 */
public class Producer implements Runnable {
	Buffer buffer;

	Producer(Buffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public void run() {
		for (int i = 0; i < Main.N; i++) {
			synchronized (buffer) {
				buffer.put(i);

				buffer.notify();

				if (buffer.get() != -1) {
					try {
						buffer.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
