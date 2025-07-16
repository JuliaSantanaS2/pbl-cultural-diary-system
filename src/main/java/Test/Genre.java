package Test;

import Module.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenreTest {

    private Genre genreAction;
    private Genre genreActionLower;
    private Genre genreRomance;

    @BeforeEach
    void setUp() {
        genreAction = new Genre("Ação");
        genreActionLower = new Genre("ação");
        genreRomance = new Genre("Romance");
    }

    @Test
    @DisplayName("Should return correct genre string")
    void getGenre() {
        assertEquals("Ação", genreAction.getGenre());
        assertEquals("ação", genreActionLower.getGenre());
        assertEquals("Romance", genreRomance.getGenre());
    }

    @Test
    @DisplayName("toString should return the genre string")
    void testToString() {
        assertEquals("Ação", genreAction.toString());
        assertEquals("ação", genreActionLower.toString());
    }

    @Test
    @DisplayName("compareTo should compare ignoring case")
    void compareTo() {
        assertTrue(genreAction.compareTo(genreActionLower) == 0, "Ação should be equal to ação");
        assertTrue(genreAction.compareTo(genreRomance) < 0, "Ação should come before Romance");
        assertTrue(genreRomance.compareTo(genreAction) > 0, "Romance should come after Ação");
    }

    @Test
    @DisplayName("equals should compare ignoring case and handle null/different types")
    void testEquals() {
        assertEquals(genreAction, genreAction, "Should be equal to itself");
        assertEquals(genreAction, genreActionLower, "Ação should equal ação");
        assertEquals(genreActionLower, genreAction, "ação should equal Ação");
        assertNotEquals(genreAction, genreRomance, "Ação should not equal Romance");
        assertNotEquals(null, genreAction, "Should not be equal to null");
        assertNotEquals(genreAction, "Ação", "Should not be equal to a String");
    }

    @Test
    @DisplayName("hashCode should be consistent with equals (ignore case)")
    void testHashCode() {
        assertEquals(genreAction.hashCode(), genreActionLower.hashCode(), "Hashcodes should be equal for Ação and ação");
        assertNotEquals(genreAction.hashCode(), genreRomance.hashCode(), "Hashcodes should be different for Ação and Romance");
    }
}