package com.apd.tema2.intersections;

import com.apd.tema2.entities.Car;
import com.apd.tema2.entities.Intersection;
import com.apd.tema2.utils.Constants;

import java.util.concurrent.Semaphore;

public class PriorityIntersection implements Intersection {
    private Semaphore lowPrioritySemaphore;
    private Semaphore highPrioritySemaphore;
    private int highPriorityCars;
    private int lowPriorityCars;
    private int waitingTimeRoundabout;

    public void init(int highPriorityCars, int lowPriorityCars) {
        this.highPrioritySemaphore = new Semaphore(highPriorityCars);
        this.lowPrioritySemaphore = new Semaphore(lowPriorityCars);
        this.waitingTimeRoundabout = 2000;
        this.highPriorityCars = highPriorityCars;
        this.lowPriorityCars = lowPriorityCars;
    }

    @Override
    public void handle(Car car) {
        if (car.getPriority() == Constants.LOW_PRIORITY) {
            System.out.println("Car " + car.getId() + " with low priority is trying to enter the intersection...");
            try {
                lowPrioritySemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Car " + car.getId() + " with low priority has entered the intersection");
            lowPrioritySemaphore.release();
        } else {
            System.out.println("Car " + car.getId() + " with high priority has entered the intersection");
            try {
                highPrioritySemaphore.acquire();
                lowPrioritySemaphore.drainPermits();
                Thread.sleep(waitingTimeRoundabout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Car " + car.getId() + " with high priority has exited the intersection");
            highPrioritySemaphore.release();

            if (highPrioritySemaphore.availablePermits() == highPriorityCars) {
                lowPrioritySemaphore.release(lowPriorityCars);
            }
        }
    }
}
