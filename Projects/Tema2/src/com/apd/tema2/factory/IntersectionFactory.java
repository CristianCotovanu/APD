package com.apd.tema2.factory;

import com.apd.tema2.entities.Intersection;
import com.apd.tema2.intersections.*;
import com.apd.tema2.utils.IntersectionTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Prototype Factory: va puteti crea cate o instanta din fiecare tip de implementare de Intersection.
 */
public class IntersectionFactory {
    private static final Map<String, Intersection> cache = new HashMap<>();

    static {
        cache.put(IntersectionTypes.SEMAPHORE, new SimpleIntersection() { });
        cache.put(IntersectionTypes.N_ROUNDABOUT, new SimpleRoundabout() { });
        cache.put(IntersectionTypes.STRICT_ONE_CAR_ROUNDABOUT, new StrictOneCarRoundabout() { });
        cache.put(IntersectionTypes.STRICT_X_CAR_ROUNDABOUT, new StrictXCarsRoundabout() { });
        cache.put(IntersectionTypes.MAX_CARS_ROUNDABOUT, new MaxCarsRoundabout() { });
        cache.put(IntersectionTypes.PRIORITY, new PriorityIntersection() { });
        cache.put(IntersectionTypes.CROSSWALK, new Crosswalk() { });
        cache.put(IntersectionTypes.SIMPLE_MAINTENANCE, new SimpleMaintenance() { });
        cache.put(IntersectionTypes.COMPLEX_MAINTENANCE, new ComplexMaintenance() { });
        cache.put(IntersectionTypes.RAILROAD, new Railroad() { });
    }

    public static Intersection getIntersection(String intersectionType) {
        return cache.get(intersectionType);
    }
}
