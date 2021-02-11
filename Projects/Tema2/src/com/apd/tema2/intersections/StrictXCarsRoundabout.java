package com.apd.tema2.intersections;

import com.apd.tema2.Main;
import com.apd.tema2.entities.Car;
import com.apd.tema2.entities.Intersection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class StrictXCarsRoundabout implements Intersection {
    private int roundaboutWaitingTime;
    private Semaphore totalCarsSemaphore;
    private List<Semaphore> laneSemaphores;
    private CyclicBarrier reachRoundaboutBarrier;
    private CyclicBarrier roundaboutBarrier;

    public void init(int lanesNumber, int roundaboutWaitingTime, int carsPassingAtOnce) {
        this.roundaboutWaitingTime = roundaboutWaitingTime;
        this.totalCarsSemaphore = new Semaphore(lanesNumber * carsPassingAtOnce);
        this.laneSemaphores = new ArrayList<>(lanesNumber);
        for (int lane = 0; lane < lanesNumber; lane++) {
            this.laneSemaphores.add(new Semaphore(carsPassingAtOnce));
        }
        this.reachRoundaboutBarrier = new CyclicBarrier(Main.carsNo);
        this.roundaboutBarrier = new CyclicBarrier(lanesNumber * carsPassingAtOnce);
    }

    @Override
    public void handle(Car car) {
        System.out.println("Car " + car.getId() + " has reached the roundabout, now waiting...");
        try {
            reachRoundaboutBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        try {
            laneSemaphores.get(car.getStartDirection()).acquire();
            totalCarsSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Car " + car.getId() + " was selected to enter the roundabout from lane " + car.getStartDirection());
        try {
            roundaboutBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        System.out.println("Car " + car.getId() + " has entered the roundabout from lane " + car.getStartDirection());
        try {
            Thread.sleep(roundaboutWaitingTime);
        } catch (Exception e) {
            System.out.println(e.getCause() + " " + e.getMessage());
        }

        System.out.println("Car " + car.getId() + " has exited the roundabout after " + (roundaboutWaitingTime / 1000) + " seconds");
        laneSemaphores.get(car.getStartDirection()).release();
        try {
            roundaboutBarrier.await();
            totalCarsSemaphore.release();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
