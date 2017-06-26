package com.leon.dm.basic.rs;

import java.util.*;

/**
 * Created by ntcon on 6/20/2017.
 */
class CollaborativeFiltering {

    double computeBasedOnManhattanDistance(Map<String, List<Double>> similarRating) {
        double result = 0;

        for (Map.Entry<String, List<Double>> entry : similarRating.entrySet()) {
            result += Math.abs(entry.getValue().get(1) - entry.getValue().get(0));
        }

        return result;
    }

    double computeBasedOnEuclideanDistance(Map<String, List<Double>> similarRating) {
        double result = 0;

        for (Map.Entry<String, List<Double>> entry : similarRating.entrySet()) {
            result += Math.pow(entry.getValue().get(1) - entry.getValue().get(0), 2);
        }

        return Math.sqrt(result);
    }

    double computeBasedOnMinkowskiDistance(Map<String, List<Double>> similarRating, int r) {
        double result = 0;

        for (Map.Entry<String, List<Double>> entry : similarRating.entrySet()) {
            result += Math.pow(Math.abs(entry.getValue().get(1) - entry.getValue().get(0)), r);
        }

        return Math.pow(result, (1.0 / r));
    }

    Map<String, List<Double>> preFilter(List<Rating> person1, List<Rating> person2) {
        Map<String, List<Double>> similarRating = new HashMap<>();
        for (Rating rating1 : person1)
            for (Rating rating2 : person2) {
                if (rating2.getBand().equals(rating1.getBand())) {
                    similarRating.put(rating2.getBand(), Arrays.asList(rating1.getStar(), rating2.getStar()));
                    break;
                }
            }

        return similarRating;
    }

    List<Rating> makeRecommendation(String user, Map<String, List<Rating>> rawData) {
        String nearestNeighbor = "";
        double nearestDistance = Double.MAX_VALUE;

        for (Map.Entry<String, List<Rating>> entry : rawData.entrySet()) {
            if (!entry.getKey().equals(user)) {
                double currentDistance = computeBasedOnMinkowskiDistance(preFilter(rawData.get(user), rawData.get(entry.getKey())), 1);
                if (currentDistance < nearestDistance) {
                    nearestDistance = currentDistance;
                    nearestNeighbor = entry.getKey();
                }
            }
        }

        return retainRating(rawData.get(nearestNeighbor), rawData.get(user));
    }

    private List<Rating> retainRating(List<Rating> ratings1, List<Rating> ratings2) {
        List<Rating> result = new ArrayList<>();
        for (Rating rating : ratings1) {
            if (!ratings2.contains(rating)) {
                result.add(rating);
            }
        }

        return result;
    }

}
