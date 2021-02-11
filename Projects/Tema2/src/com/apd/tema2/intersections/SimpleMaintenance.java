package com.apd.tema2.intersections;

import com.apd.tema2.entities.Car;
import com.apd.tema2.entities.Intersection;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class SimpleMaintenance implements Intersection {
    private Semaphore firstLaneSemaphore;
    private Semaphore secondLaneSemaphore;
    private CyclicBarrier barrier;
    private CyclicBarrier laneBarrier;

    public void init(int totalCars, int carsAtOnce) {
        this.barrier = new CyclicBarrier(totalCars);
        this.firstLaneSemaphore = new Semaphore(carsAtOnce);
        this.secondLaneSemaphore = new Semaphore(0);
        this.laneBarrier = new CyclicBarrier(carsAtOnce);
    }

    @Override
    public void handle(Car car) {
        try {
            System.out.println("Car " + car.getId() + " from side number " + car.getStartDirection() + " has reached the bottleneck");
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        if (car.getStartDirection() == 0) {
            try {
                firstLaneSemaphore.acquire();
                laneBarrier.await();
                System.out.println("Car " + car.getId() + " from side number " + car.getStartDirection() + " has passed the bottleneck");
                secondLaneSemaphore.release();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        } else {
            try {
                secondLaneSemaphore.acquire();
                laneBarrier.await();
                System.out.println("Car " + car.getId() + " from side number " + car.getStartDirection() + " has passed the bottleneck");
                firstLaneSemaphore.release();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
