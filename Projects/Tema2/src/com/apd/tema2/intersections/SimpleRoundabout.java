package com.apd.tema2.intersections;

import com.apd.tema2.entities.Car;
import com.apd.tema2.entities.Intersection;

import java.util.concurrent.Semaphore;

public class SimpleRoundabout implements Intersection {
    public int roundaboutWaitingTime;
    private Semaphore semaphore;

    public SimpleRoundabout() { }

    public void init(int maxCarsPermitted, int roundaboutWaitingTime) {
        this.roundaboutWaitingTime = roundaboutWaitingTime;
        this.semaphore = new Semaphore(maxCarsPermitted);
    }

    @Override
    public void handle(Car car) {
        System.out.println("Car " + car.getId() + " has reached the roundabout, now waiting...");
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Car " + car.getId() + " has entered the roundabout");
        try {
            Thread.sleep(roundaboutWaitingTime);
        } catch (Exception e) {
            System.out.println(e.getCause() + " " + e.getMessage());
        }

        System.out.println("Car " + car.getId() + " has exited the roundabout after " + (roundaboutWaitingTime / 1000) + " seconds");
        semaphore.release();
    }
}
