package com.apd.tema2.intersections;

import com.apd.tema2.Main;
import com.apd.tema2.entities.Car;
import com.apd.tema2.entities.Intersection;

import java.util.concurrent.ConcurrentHashMap;

public class Crosswalk implements Intersection {
    private final ConcurrentHashMap<Integer, Boolean> carStates = new ConcurrentHashMap<>();

    @Override
    public void handle(Car car) {
        while (!Main.pedestrians.isFinished()) {
            Boolean crtState = carStates.get(car.getId());

            if (!Main.pedestrians.isPass()) {
                if (crtState == null || !crtState) {
                    carStates.put(car.getId(), true);
                    System.out.println("Car " + car.getId() + " has now green light");
                }
            } else {
                if (crtState == null || crtState) {
                    carStates.put(car.getId(), false);
                    System.out.println("Car " + car.getId() + " has now red light");
                }
            }
        }
    }
}
