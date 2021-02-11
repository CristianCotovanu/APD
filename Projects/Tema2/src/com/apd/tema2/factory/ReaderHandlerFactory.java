package com.apd.tema2.factory;

import com.apd.tema2.Main;
import com.apd.tema2.entities.Pedestrians;
import com.apd.tema2.entities.ReaderHandler;
import com.apd.tema2.intersections.*;

/**
 * Returneaza sub forma unor clase anonime implementari pentru metoda de citire din fisier.
 */
public class ReaderHandlerFactory {

    public static ReaderHandler getHandler(String handlerType) {
        return switch (handlerType) {
            // simple semaphore intersection
            case "simple_semaphore" -> (handlerTypeParam, br) -> {
                Main.intersection = IntersectionFactory.getIntersection(handlerTypeParam);
            };
            // max random N cars roundabout (s time to exit each of them)
            case "simple_n_roundabout" -> (handlerTypeParam, br) -> {
                 String[] line = br.readLine().split(" ");
                 int maxCarsInRoundabout = Integer.parseInt(line[0]);
                 int timeInRoundabout = Integer.parseInt(line[1]);

                 Main.intersection = IntersectionFactory.getIntersection(handlerTypeParam);
                ((SimpleRoundabout)Main.intersection).init(maxCarsInRoundabout, timeInRoundabout);
            };
            // roundabout with exactly one car from each lane simultaneously
            case "simple_strict_1_car_roundabout" -> (handlerTypeParam, br) -> {
                String[] line = br.readLine().split(" ");
                int lanesNumber = Integer.parseInt(line[0]);
                int timeInRoundabout = Integer.parseInt(line[1]);

                Main.intersection = IntersectionFactory.getIntersection(handlerTypeParam);
                ((StrictOneCarRoundabout)Main.intersection).init(lanesNumber, timeInRoundabout);
            };
            // roundabout with exactly X cars from each lane simultaneously
            case "simple_strict_x_car_roundabout" -> (handlerTypeParam, br) -> {
                String[] line = br.readLine().split(" ");
                int lanesNumber = Integer.parseInt(line[0]);
                int timeInRoundabout = Integer.parseInt(line[1]);
                int carsPassingAtOnce = Integer.parseInt(line[2]);

                Main.intersection = IntersectionFactory.getIntersection(handlerTypeParam);
                ((StrictXCarsRoundabout)Main.intersection).init(lanesNumber, timeInRoundabout, carsPassingAtOnce);
            };
            // roundabout with at most X cars from each lane simultaneously
            case "simple_max_x_car_roundabout" -> (handlerTypeParam, br) -> {
                String[] line = br.readLine().split(" ");
                int lanesNumber = Integer.parseInt(line[0]);
                int timeInRoundabout = Integer.parseInt(line[1]);
                int maxCarsPassingAtOnce = Integer.parseInt(line[2]);

                Main.intersection = IntersectionFactory.getIntersection(handlerTypeParam);
                ((MaxCarsRoundabout)Main.intersection).init(lanesNumber, timeInRoundabout, maxCarsPassingAtOnce);
            };
            // entering a road without any priority
            case "priority_intersection" -> (handlerTypeParam, br) -> {
                String[] line = br.readLine().split(" ");
                int highPriorityCars = Integer.parseInt(line[0]);
                int lowPriorityCars = Integer.parseInt(line[1]);

                Main.intersection = IntersectionFactory.getIntersection(handlerTypeParam);
                ((PriorityIntersection)Main.intersection).init(highPriorityCars, lowPriorityCars);
            };
            // crosswalk activated on at least a number of people (s time to finish all of them)
            case "crosswalk" -> (handlerTypeParam, br) -> {
                String[] line = br.readLine().split(" ");
                int executeTime = Integer.parseInt(line[0]);
                int maxPedestrians = Integer.parseInt(line[1]);

                Main.intersection = IntersectionFactory.getIntersection(handlerTypeParam);
                Main.pedestrians = new Pedestrians(executeTime, maxPedestrians);
            };
            // road in maintenance - 1 lane 2 ways, X cars at a time
            case "simple_maintenance" -> (handlerTypeParam, br) -> {
                String[] line = br.readLine().split(" ");
                int carsAtOnce = Integer.parseInt(line[0]);

                Main.intersection = IntersectionFactory.getIntersection(handlerTypeParam);
                ((SimpleMaintenance)Main.intersection).init(Main.carsNo, carsAtOnce);
            };
            // road in maintenance - N lanes 2 ways, X cars at a time
            case "complex_maintenance" -> (handlerTypeParam, br) -> {
                String[] line = br.readLine().split(" ");
                int freeLanes = Integer.parseInt(line[0]);
                int totalInitialLanes = Integer.parseInt(line[1]);
                int passingCarsInOneGo = Integer.parseInt(line[2]);

                Main.intersection = IntersectionFactory.getIntersection(handlerTypeParam);
                ((ComplexMaintenance)Main.intersection).init(freeLanes, totalInitialLanes, passingCarsInOneGo);
            };
            // railroad blockage for T seconds for all the cars
            case "railroad" -> (handlerTypeParam, br) -> {
                Main.intersection = IntersectionFactory.getIntersection(handlerTypeParam);
            };

            default -> null;
        };
    }
}
