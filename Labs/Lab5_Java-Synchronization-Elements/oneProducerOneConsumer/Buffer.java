package oneProducerOneConsumer;
/**
 * @author gabriel.gutu@upb.ro
 *
 */
public class Buffer {
	int a;

	public Buffer() {
		a = -1;
	}

	void put(int value) {
		a = value;
	}

	int get() {
		return a;
	}
}
