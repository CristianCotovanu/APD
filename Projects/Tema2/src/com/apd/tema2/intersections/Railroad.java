package com.apd.tema2.intersections;

import com.apd.tema2.Main;
import com.apd.tema2.entities.Car;
import com.apd.tema2.entities.Intersection;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Railroad implements Intersection {
    private final BlockingQueue<Car> firstLane = new LinkedBlockingQueue<>();
    private final BlockingQueue<Car> secondLane = new LinkedBlockingQueue<>();
    private final CyclicBarrier trainBarrier = new CyclicBarrier(Main.carsNo);
    private final AtomicBoolean trainFlag = new AtomicBoolean(true);
    private final AtomicReference carToPass = new AtomicReference();

    @Override
    public void handle(Car car) {
        synchronized (this) {
            try {
                if (car.getStartDirection() == 0) {
                    firstLane.put(car);
                } else {
                    secondLane.put(car);
                }
                System.out.println("Car " + car.getId() + " from side number " + car.getStartDirection() + " has stopped by the railroad");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            trainBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        if (trainFlag.getAndSet(false)) {
            System.out.println("The train has passed, cars can now proceed");
        }

        synchronized (this) {
            if (!firstLane.isEmpty()) {
                carToPass.set(firstLane.remove());
                System.out.println("Car " + ((Car) carToPass.get()).getId() + " from side number " + ((Car) carToPass.get()).getStartDirection() + " has started driving");
            } else if (!secondLane.isEmpty()) {
                carToPass.set(secondLane.remove());
                System.out.println("Car " + ((Car) carToPass.get()).getId() + " from side number " + ((Car) carToPass.get()).getStartDirection() + " has started driving");
            }
        }
    }
}
