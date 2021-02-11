package com.apd.tema2.intersections;

import com.apd.tema2.entities.Car;
import com.apd.tema2.entities.Intersection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class MaxCarsRoundabout implements Intersection {
    private int roundaboutWaitingTime;
    private List<Semaphore> laneSemaphores;

    public void init(int lanesNumber, int roundaboutWaitingTime, int maxCarsPassingAtOnce) {
        this.roundaboutWaitingTime = roundaboutWaitingTime;
        this.laneSemaphores = new ArrayList<>(lanesNumber);
        for (int lane = 0; lane < lanesNumber; lane++) {
            this.laneSemaphores.add(new Semaphore(maxCarsPassingAtOnce));
        }
    }

    @Override
    public void handle(Car car) {
        System.out.println("Car " + car.getId() + " has reached the roundabout from lane " + car.getStartDirection());
        try {
            laneSemaphores.get(car.getStartDirection()).acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Car " + car.getId() + " has entered the roundabout from lane " + car.getStartDirection());
        try {
            Thread.sleep(roundaboutWaitingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Car " + car.getId() + " has exited the roundabout after " + (roundaboutWaitingTime / 1000) + " seconds");
        laneSemaphores.get(car.getStartDirection()).release();
    }
}
