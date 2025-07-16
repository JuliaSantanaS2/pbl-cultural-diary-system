package Test;

import Module.Genre;
import Module.Media;
import Module.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MediaTest {

    private Genre genreAction;
    private Genre genreComedy;
    private List<Genre> genresList;
    private Media mediaSeen;
    private Media mediaNotSeen;
    private Review review1;

    @BeforeEach
    void setUp() {
        genreAction = new Genre("Ação");
        genreComedy = new Genre("Comédia");
        genresList = new ArrayList<>(Arrays.asList(genreAction, genreComedy));
        mediaSeen = new Media(true, "Test Title", genresList, 2024);
        mediaNotSeen = new Media(false, "Another Title", Collections.singletonList(genreAction), 2023);
        review1 = new Review("Good one", 4.0f, "10/10/2024");
    }

    @Test
    @DisplayName("Constructor should set final fields correctly")
    void constructor() {
        assertTrue(mediaSeen.isSeen());
        assertEquals("Test Title", mediaSeen.getTitle());
        assertEquals(2024, mediaSeen.getYearRelease());
        assertIterableEquals(genresList, mediaSeen.getGenres());
        assertNotNull(mediaSeen.getReviews());
        assertTrue(mediaSeen.getReviews().isEmpty());

        assertFalse(mediaNotSeen.isSeen());
        assertEquals("Another Title", mediaNotSeen.getTitle());
        assertEquals(2023, mediaNotSeen.getYearRelease());
        assertEquals(1, mediaNotSeen.getGenres().size());
        assertEquals(genreAction, mediaNotSeen.getGenres().get(0));
        assertTrue(mediaNotSeen.getReviews().isEmpty());
    }

    @Test
    @DisplayName("Getters for final fields should return correct values")
    void getters() {
        assertEquals("Test Title", mediaSeen.getTitle());
        assertEquals(2024, mediaSeen.getYearRelease());
        assertTrue(mediaSeen.isSeen());
        assertIterableEquals(genresList, mediaSeen.getGenres());
    }

    @Test
    @DisplayName("addReview should add review to the list")
    void addReview() {
        assertTrue(mediaSeen.getReviews().isEmpty());
        mediaSeen.addReview(review1);
        assertEquals(1, mediaSeen.getReviews().size());
        assertTrue(mediaSeen.getReviews().contains(review1));
    }

    @Test
    @DisplayName("getReviews should return the list of reviews")
    void getReviews() {
        assertTrue(mediaSeen.getReviews().isEmpty());
        mediaSeen.addReview(review1);
        List<Review> reviews = mediaSeen.getReviews();
        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        assertEquals(review1, reviews.get(0));
    }

    @Test
    @DisplayName("toString should contain basic media info and reviews")
    void testToString() {
        String str = mediaSeen.toString();
        assertTrue(str.contains("Título: Test Title"));
        assertTrue(str.contains("Ano de Lançamento: 2024"));
        assertTrue(str.contains("Gêneros: [Ação, Comédia]"));
        assertTrue(str.contains("Review[]"));

        mediaSeen.addReview(review1);
        str = mediaSeen.toString();
        assertTrue(str.contains("Review[" + review1.toString() + "]"));
    }

}