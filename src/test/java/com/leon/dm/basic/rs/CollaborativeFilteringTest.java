package com.leon.dm.basic.rs;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ntcon on 6/20/2017.
 */
public class CollaborativeFilteringTest {

    private Map<String, List<Rating>> testData;

    private CollaborativeFiltering collaborativeFiltering;

    @Before
    public void prepareData() {
        testData = new HashMap<>();
        testData.put("Angelica", new ArrayList<>(Arrays.asList(new Rating("Blues Traveler", 3.5), new Rating("Broken Bells", 2.0),
                new Rating("Norah Jones", 4.5), new Rating("Phoenix", 5.0),
                new Rating("Slightly Stoopid", 1.5), new Rating("The Strokes", 2.5),
                new Rating("Vampire Weekend", 2.0))));
        testData.put("Bill", new ArrayList<>(Arrays.asList(new Rating("Blues Traveler", 2.0), new Rating("Broken Bells", 3.5),
                new Rating("Deadmau5", 4.0), new Rating("Phoenix", 2.0),
                new Rating("Slightly Stoopid", 3.5), new Rating("Vampire Weekend", 3.0))));
        testData.put("Chan", new ArrayList<>(Arrays.asList(new Rating("Blues Traveler", 5.0), new Rating("Broken Bells", 1.0),
                new Rating("Deadmau5", 1.0), new Rating("Norah Jones", 3.0),
                new Rating("Slightly Stoopid", 1.0), new Rating("Phoenix", 5.0))));
        testData.put("Dan", new ArrayList<>(Arrays.asList(new Rating("Blues Traveler", 3.0), new Rating("Broken Bells", 4.0),
                new Rating("Deadmau5", 4.5), new Rating("Phoenix", 3.0),
                new Rating("Slightly Stoopid", 4.5), new Rating("Vampire Weekend", 2.0),
                new Rating("The Strokes", 4.0))));
        testData.put("Hailey", new ArrayList<>(Arrays.asList(new Rating("Norah Jones", 4.0), new Rating("Broken Bells", 4.0),
                new Rating("Deadmau5", 1.0), new Rating("Vampire Weekend", 1.0),
                new Rating("The Strokes", 4.0))));
        testData.put("Jordyn", new ArrayList<>(Arrays.asList(new Rating("Norah Jones", 5.0), new Rating("Broken Bells", 4.5),
                new Rating("Deadmau5", 4.0), new Rating("Phoenix", 5.0),
                new Rating("Slightly Stoopid", 4.5), new Rating("Vampire Weekend", 4.0),
                new Rating("The Strokes", 4.0))));
        testData.put("Sam", new ArrayList<>(Arrays.asList(new Rating("Blues Traveler", 5.0), new Rating("Broken Bells", 2.0),
                new Rating("Norah Jones", 3.0), new Rating("Phoenix", 5.0),
                new Rating("Slightly Stoopid", 4.0), new Rating("The Strokes", 5.0))));
        testData.put("Veronica", new ArrayList<>(Arrays.asList(new Rating("Blues Traveler", 3.0), new Rating("Norah Jones", 5.0),
                new Rating("Phoenix", 4.0), new Rating("Slightly Stoopid", 2.5),
                new Rating("The Strokes", 3.0))));

        collaborativeFiltering = new CollaborativeFiltering();
    }

    @Test
    public void testComputeBasedOnManhattanDistance_Case1() {
        Map<String, List<Double>> similarRating = collaborativeFiltering.preFilter(testData.get("Hailey"), testData.get("Veronica"));
        assertEquals(collaborativeFiltering.computeBasedOnManhattanDistance(similarRating), 2.0, 0.001);
    }

    @Test
    public void testComputeBasedOnManhattanDistance_Case2() {
        Map<String, List<Double>> similarRating = collaborativeFiltering.preFilter(testData.get("Hailey"), testData.get("Jordyn"));
        assertEquals(collaborativeFiltering.computeBasedOnMinkowskiDistance(similarRating, 1), 7.5, 0.001);
    }

    @Test
    public void testMakeRecommendation_Case1() {
        List<Rating> expected = Arrays.asList(new Rating("Phoenix", 4.0), new Rating("Blues Traveler", 3.0),
                new Rating("Slightly Stoopid", 2.5));

        List<Rating> actual = collaborativeFiltering.makeRecommendation("Hailey", testData)
                .stream()
                .sorted(Comparator.comparing(Rating::getStar).reversed())
                .collect(toList());

        assertEquals(expected, actual);
    }

    @Test
    public void testMakeRecommendation_Case2() {
        List<Rating> expected = Arrays.asList(new Rating("The Strokes", 4.0), new Rating("Vampire Weekend", 1.0));

        List<Rating> actual = collaborativeFiltering.makeRecommendation("Chan", testData)
                .stream()
                .sorted(Comparator.comparing(Rating::getStar).reversed())
                .collect(toList());

        assertEquals(expected, actual);
    }

    @Test
    public void testMakeRecommendation_Case3() {
        List<Rating> actual = collaborativeFiltering.makeRecommendation("Sam", testData)
                .stream()
                .sorted(Comparator.comparing(Rating::getStar).reversed())
                .collect(toList());

        assertTrue(actual.contains(new Rating("Deadmau5", 1.0)));
    }

    @Test
    public void testMakeRecommendation_Case4() {
        List<Rating> expected = new ArrayList<>();

        List<Rating> actual = collaborativeFiltering.makeRecommendation("Angelica", testData)
                .stream()
                .sorted(Comparator.comparing(Rating::getStar).reversed())
                .collect(toList());

        assertEquals(expected, actual);
    }

    @Test
    public void testComputeBasedOnEuclideanDistance_Case1() {
        Map<String, List<Double>> similarRating = collaborativeFiltering.preFilter(testData.get("Angelica"), testData.get("Bill"));
        assertEquals(collaborativeFiltering.computeBasedOnEuclideanDistance(similarRating), Math.sqrt(18.5), 0.001);
    }

    @Test
    public void testComputeBasedOnEuclideanDistance_Case2() {
        Map<String, List<Double>> similarRating = collaborativeFiltering.preFilter(testData.get("Hailey"), testData.get("Veronica"));
        assertEquals(collaborativeFiltering.computeBasedOnMinkowskiDistance(similarRating, 2), Math.sqrt(2), 0.001);
    }

    @Test
    public void testComputeBasedOnEuclideanDistance_Case3() {
        Map<String, List<Double>> similarRating = collaborativeFiltering.preFilter(testData.get("Hailey"), testData.get("Jordyn"));
        assertEquals(collaborativeFiltering.computeBasedOnEuclideanDistance(similarRating), Math.sqrt(19.25), 0.001);
    }

    @Test
    public void testComputePearsonCorrelationCoefficient_Case1() {
        Map<String, List<Double>> similarRating = collaborativeFiltering.preFilter(testData.get("Angelica"), testData.get("Bill"));
        assertEquals(collaborativeFiltering.computePearsonCorrelationCoefficient(similarRating), -0.90405349906826993, 0.001);
    }

    @Test
    public void testComputePearsonCorrelationCoefficient_Case2() {
        Map<String, List<Double>> similarRating = collaborativeFiltering.preFilter(testData.get("Angelica"), testData.get("Hailey"));
        assertEquals(collaborativeFiltering.computePearsonCorrelationCoefficient(similarRating), 0.42008402520840293, 0.001);
    }

    @Test
    public void testComputePearsonCorrelationCoefficient_Case3() {
        Map<String, List<Double>> similarRating = collaborativeFiltering.preFilter(testData.get("Angelica"), testData.get("Jordyn"));
        assertEquals(collaborativeFiltering.computePearsonCorrelationCoefficient(similarRating), 0.76397486054754316, 0.001);
    }

    @Test
    public void testComputeCosineSimilarity_Case1() {
        Map<String, List<Rating>> testDataForCosineSimilarity = new HashMap<>();
        testDataForCosineSimilarity.put("Angelica", new ArrayList<>(Arrays.asList(new Rating("Blues Traveler", 3.5), new Rating("Broken Bells", 2.0),
                new Rating("Norah Jones", 4.5), new Rating("Phoenix", 5.0),
                new Rating("Slightly Stoopid", 1.5), new Rating("The Strokes", 2.5),
                new Rating("Vampire Weekend", 2.0), new Rating("Deadmau5", 0))));
        testDataForCosineSimilarity.put("Veronica", new ArrayList<>(Arrays.asList(new Rating("Blues Traveler", 3.0), new Rating("Norah Jones", 5.0),
                new Rating("Phoenix", 4.0), new Rating("Slightly Stoopid", 2.5),
                new Rating("The Strokes", 3.0), new Rating("Broken Bells", 0),
                new Rating("Deadmau5", 0), new Rating("Vampire Weekend", 0))));

        Map<String, List<Double>> similarRating = collaborativeFiltering.preFilter(testDataForCosineSimilarity.get("Angelica"),
                testDataForCosineSimilarity.get("Veronica"));
        assertEquals(collaborativeFiltering.computeCosineSimilarity(similarRating), 0.9246, 0.001);
    }

}
