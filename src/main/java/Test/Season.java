package Test;

import Module.Review;
import Module.Season;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class SeasonTest {

    private Season season1;
    private Season season10;
    private Review reviewS1_1;
    private Review reviewS1_2;

    @BeforeEach
    void setUp() {
        season1 = new Season(1, 10, "01/01/2020");
        season10 = new Season(10, 8, "15/09/2023");
        reviewS1_1 = new Review("Great start!", 4.5f, "02/01/2020");
        reviewS1_2 = new Review("Episode 3 was slow", 3.0f, "05/01/2020");
    }

    @Test
    @DisplayName("Constructor should set final values and init empty review list")
    void constructor() {
        assertEquals(1, season1.getSeasonNumber());
        assertEquals(10, season1.getEpisodeCount());
        assertEquals("01/01/2020", season1.getReleaseDate());
        assertNotNull(season1.getReviews());
        assertTrue(season1.getReviews().isEmpty());

        assertEquals(10, season10.getSeasonNumber());
        assertEquals(8, season10.getEpisodeCount());
        assertEquals("15/09/2023", season10.getReleaseDate());
        assertTrue(season10.getReviews().isEmpty());
    }

    @Test
    @DisplayName("Getters for final fields should return correct values")
    void getters() {
        assertEquals(1, season1.getSeasonNumber());
        assertEquals(10, season1.getEpisodeCount());
        assertEquals("01/01/2020", season1.getReleaseDate());
    }


    @Test
    @DisplayName("addReview should add review to the season's list")
    void addReview() {
        assertTrue(season1.getReviews().isEmpty());
        season1.addReview(reviewS1_1);
        assertEquals(1, season1.getReviews().size());
        assertTrue(season1.getReviews().contains(reviewS1_1));

        season1.addReview(reviewS1_2);
        assertEquals(2, season1.getReviews().size());
        assertTrue(season1.getReviews().contains(reviewS1_2));
    }

    @Test
    @DisplayName("getReviews should return the correct list of reviews for the season")
    void getReviews() {
        assertTrue(season1.getReviews().isEmpty());
        season1.addReview(reviewS1_1);
        season1.addReview(reviewS1_2);
        List<Review> reviews = season1.getReviews();
        assertNotNull(reviews);
        assertEquals(2, reviews.size());
        assertIterableEquals(Arrays.asList(reviewS1_1, reviewS1_2), reviews);
    }
}