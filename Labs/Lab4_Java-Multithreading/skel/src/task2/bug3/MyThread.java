package task2.bug3;
/**
 * @author cristian.chilipirea
 * 
 *         Q: Why is value set correct even though we use different locks in
 *         different threads?
 *         A: Because of string pool, a and b refer to the same object so that makes 'a' and 'b' the same lock
 */
public class MyThread implements Runnable {
	static String a = "LOCK";
	static String b = "LOCK";
	int id;
	static int value = 0;

	MyThread(int id) {
		this.id = id;
	}

	@Override
	public void run() {
		if (id == 0) {
			synchronized (a) {
				for (int i = 0; i < Main.N; i++)
					value = value + 3;
			}
		} else {
			synchronized (b) {
				for (int i = 0; i < Main.N; i++)
					value = value + 3;
			}
		}
	}
}
