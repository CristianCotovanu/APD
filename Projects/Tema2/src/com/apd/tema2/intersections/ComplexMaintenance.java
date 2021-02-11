package com.apd.tema2.intersections;

import com.apd.tema2.Main;
import com.apd.tema2.entities.Car;
import com.apd.tema2.entities.Intersection;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ComplexMaintenance implements Intersection {
    private int freeLanes;
    private int passingCarInOneGo;
    private int chunkSize;
    private CyclicBarrier totalBarrier;
    private CyclicBarrier laneBarrier;
    private final ConcurrentHashMap<Integer, BlockingQueue<Integer>> road = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, BlockingQueue<Car>> carsForOldLanes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Semaphore> semaphoresForLanes = new ConcurrentHashMap<>();

    public void init(int freeLanes, int totalInitialLanes, int passingCarsInOneGo) {
        this.passingCarInOneGo = passingCarsInOneGo;
        this.freeLanes = freeLanes;
        this.totalBarrier = new CyclicBarrier(Main.carsNo);
        this.laneBarrier = new CyclicBarrier(passingCarsInOneGo);
        this.chunkSize = (int) Math.ceil((double) totalInitialLanes / freeLanes);

        for (int newLane = 0; newLane < freeLanes; newLane++) {
            var oldLanes = new LinkedBlockingQueue<Integer>();
            int stOldLane = newLane * chunkSize;
            int enOldLane = Math.min((newLane + 1) * chunkSize - 1, totalInitialLanes - 1);

            for (int j = stOldLane; j <= enOldLane; j++) {
                oldLanes.add(j);
            }
            road.put(newLane, oldLanes);
        }

        semaphoresForLanes.putIfAbsent(0, new Semaphore(passingCarsInOneGo));
        for (int i = 1; i < freeLanes; i++) {
            semaphoresForLanes.putIfAbsent(i, new Semaphore(0));
        }
    }

    @Override
    public void handle(Car car) {
        synchronized (this) {
            carsForOldLanes.putIfAbsent(car.getStartDirection(), new LinkedBlockingQueue<>());
            System.out.println("Car " + car.getId() + " has come from the lane number " + car.getStartDirection());
            carsForOldLanes.get(car.getStartDirection()).add(car);
        }

        try {
            totalBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        var newLaneForCar = Math.min(car.getStartDirection() / chunkSize, freeLanes - 1);
        try {
            semaphoresForLanes.get(newLaneForCar).acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        var lanesToFillNewLane = road.get(newLaneForCar);
        Integer laneToGetXCarsOut = lanesToFillNewLane.poll();

        if (laneToGetXCarsOut != null) {
            var carsForLane = carsForOldLanes.get(laneToGetXCarsOut);
            var carsToGetOut = new AtomicInteger(this.passingCarInOneGo);

            while (!carsForLane.isEmpty() && carsToGetOut.get() != 0) {
                var carTmp = carsForLane.poll();
                if (carTmp != null) {
                    System.out.println("Car " + carTmp.getId() + " from the lane " + carTmp.getStartDirection() + " has entered lane number " + newLaneForCar);
                }
                carsToGetOut.decrementAndGet();
            }

            if (carsForLane.isEmpty()) {
                System.out.println("The initial lane " + laneToGetXCarsOut + " has been emptied and removed from the new lane queue");
            } else {
                try {
                    lanesToFillNewLane.put(laneToGetXCarsOut);
                    System.out.println("The initial lane " + laneToGetXCarsOut + " has no permits and is moved to the back of the new lane queue");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                laneBarrier.await();
                semaphoresForLanes.get((newLaneForCar + 1) % freeLanes).release();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        } else {
            //TODO: Solve deadlock caused by this semaphorethingy
            for (var lane : carsForOldLanes.keySet()) {
                var carsInLane = carsForOldLanes.get(lane);
                if (carsInLane != null && !carsInLane.isEmpty())
                    semaphoresForLanes.get(lane % freeLanes).release();
            }
        }
    }
}
