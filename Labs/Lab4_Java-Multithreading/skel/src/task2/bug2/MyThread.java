package task2.bug2;
/**
 * @author cristian.chilipirea
 * 
 *         Q: Why does this code not block? We took the same lock twice!
 *		   A: 2nd block uses the same lock that was acquired previously
 */
public class MyThread implements Runnable {
	static int i;

	@Override
	public void run() {
		synchronized (this) {
			synchronized (this) {
				i++;
			}
		}
	}
}
