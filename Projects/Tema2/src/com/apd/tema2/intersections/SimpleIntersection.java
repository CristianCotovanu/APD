package com.apd.tema2.intersections;

import com.apd.tema2.entities.Car;
import com.apd.tema2.entities.Intersection;

public class SimpleIntersection implements Intersection {
    @Override
    public void handle(Car car) {
        try {
            System.out.println("Car " + car.getId() + " has reached the semaphore, now waiting...");
            Thread.sleep(car.getWaitingTime());
            System.out.println("Car " + car.getId() + " has waited enough, now driving...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}