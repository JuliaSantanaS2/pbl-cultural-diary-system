package Test;

import Module.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    private Review reviewGood;
    private Review reviewBad;
    private String todayDate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    @BeforeEach
    void setUp() {
        LocalDate date = LocalDate.now();
        todayDate = date.format(DATE_FORMATTER);

        reviewGood = new Review("Excellent!", 5.0f, todayDate);
        reviewBad = new Review("Not great.", 1.5f, "01/01/2023");
    }

    @Test
    @DisplayName("Constructor should initialize final fields correctly")
    void constructor() {
        assertEquals("Excellent!", reviewGood.comment, "Comment should be set by constructor");
        assertEquals(5.0f, reviewGood.getStars(), "Stars should be set by constructor");
        assertEquals(todayDate, reviewGood.reviewDate, "Review date should be set by constructor");

        assertEquals("Not great.", reviewBad.comment);
        assertEquals(1.5f, reviewBad.getStars());
        assertEquals("01/01/2023", reviewBad.reviewDate);
    }


    @Test
    @DisplayName("getStars should return correct stars rating")
    void getStars() {
        assertEquals(5.0f, reviewGood.getStars());
        assertEquals(1.5f, reviewBad.getStars());
    }


    @Test
    @DisplayName("toString should return formatted string using String.format")
    void testToString() {

        String expectedGood = String.format("%s (%.1f⭐): %s", "Excellent!", 5.0f, todayDate);
        String expectedBad = String.format("%s (%.1f⭐): %s", "Not great.", 1.5f, "01/01/2023");
        assertEquals(expectedGood, reviewGood.toString());
        assertEquals(expectedBad, reviewBad.toString());
    }

}