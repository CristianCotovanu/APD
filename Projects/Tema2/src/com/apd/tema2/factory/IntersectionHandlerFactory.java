package com.apd.tema2.factory;

import com.apd.tema2.Main;
import com.apd.tema2.entities.*;
import com.apd.tema2.utils.IntersectionTypes;

import static java.lang.Thread.sleep;

/**
 * Clasa Factory ce returneaza implementari ale InterfaceHandler sub forma unor
 * clase anonime.
 */
public class IntersectionHandlerFactory {

    public static IntersectionHandler getHandler(String handlerType) {
        return switch (handlerType) {
            // simple semaphore intersection
            case "simple_semaphore" -> car -> {
                Main.intersection.handle(car);
            };
            // max random N cars roundabout (s time to exit each of them)
            case "simple_n_roundabout" -> car -> {
                Main.intersection = IntersectionFactory.getIntersection(IntersectionTypes.N_ROUNDABOUT);
                Main.intersection.handle(car);
            };
            // roundabout with exactly one car from each lane simultaneously
            case "simple_strict_1_car_roundabout" -> car -> {
                Main.intersection = IntersectionFactory.getIntersection(IntersectionTypes.STRICT_ONE_CAR_ROUNDABOUT);
                Main.intersection.handle(car);
            };
            // roundabout with exactly X cars from each lane simultaneously
            case "simple_strict_x_car_roundabout" -> car -> {
                Main.intersection = IntersectionFactory.getIntersection(IntersectionTypes.STRICT_X_CAR_ROUNDABOUT);
                Main.intersection.handle(car);
            };
            // roundabout with at most X cars from each lane simultaneously
            case "simple_max_x_car_roundabout" -> car -> {
                Main.intersection = IntersectionFactory.getIntersection(IntersectionTypes.MAX_CARS_ROUNDABOUT);

                try {
                    sleep(car.getWaitingTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } // NU MODIFICATI

                Main.intersection.handle(car);
            };
            // entering a road without any priority
            case "priority_intersection" -> car -> {
                Main.intersection = IntersectionFactory.getIntersection(IntersectionTypes.PRIORITY);

                try {
                    sleep(car.getWaitingTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } // NU MODIFICATI

                Main.intersection.handle(car);
            };
            // crosswalk activated on at least a number of people (s time to finish all of them)
            case "crosswalk" -> car -> {
                Main.intersection = IntersectionFactory.getIntersection(IntersectionTypes.CROSSWALK);
                Main.intersection.handle(car);
            };
            // road in maintenance - 2 ways 1 lane each, X cars at a time
            case "simple_maintenance" -> car -> {
                Main.intersection = IntersectionFactory.getIntersection(IntersectionTypes.SIMPLE_MAINTENANCE);
                Main.intersection.handle(car);
            };
            // road in maintenance - 1 way, M out of N lanes are blocked, X cars at a time
            case "complex_maintenance" -> car -> {
                Main.intersection = IntersectionFactory.getIntersection(IntersectionTypes.COMPLEX_MAINTENANCE);
                Main.intersection.handle(car);
            };
            // railroad blockage for s seconds for all the cars
            case "railroad" -> car -> {
                Main.intersection = IntersectionFactory.getIntersection(IntersectionTypes.RAILROAD);
                Main.intersection.handle(car);
            };

            default -> null;
        };
    }
}
