package task2.bug4;
/**
 * @author cristian.chilipirea
 *
 *         Q: Why doesn't this program end? (Hint: volatile)
 *         A: Because keepRunning was set to true initially and did not modify in RAM,
 *         value was only cached, if we add volatile the keepRunning variable is updated when main reaches
 *         "t.keepRunning = false;"
 */
public class Main extends Thread {
	volatile boolean keepRunning = true;

	public void run() {
		long count = 0;
		while (keepRunning) {
			count++;
		}

		System.out.println("Thread terminated." + count);
	}

	public static void main(String[] args) throws InterruptedException {
		Main t = new Main();
		t.start();
		Thread.sleep(1000);
		t.keepRunning = false;
		System.out.println("keepRunning set to false.");
	}
}
