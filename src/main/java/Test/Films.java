package Test;

import Module.Films;
import Module.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmsTest {

    private Genre genreAction;
    private List<Genre> filmGenres;
    private List<String> castList;
    private List<String> watchList;
    private Films film;

    @BeforeEach
    void setUp() {
        genreAction = new Genre("Action");
        filmGenres = Collections.singletonList(genreAction);
        castList = Arrays.asList("Keanu Reeves");
        watchList = Collections.singletonList("HBO Max");

        film = new Films(
                castList, true, "John Wick", filmGenres, 2014,
                "John Wick", watchList, "Chad Stahelski", 101, "Derek Kolstad"
        );
    }

    @Test
    @DisplayName("Constructor should set all fields correctly (final included)")
    void constructor() {

        assertTrue(film.isSeen());
        assertEquals("John Wick", film.getTitle());
        assertIterableEquals(filmGenres, film.getGenres());
        assertEquals(2014, film.getYearRelease());

        assertEquals("John Wick", film.getOriginalTitle());
        assertIterableEquals(watchList, film.getWhereWatch());
        assertIterableEquals(castList, film.getCast());

        assertEquals("Chad Stahelski", film.getDirection());
        assertEquals(101, film.getRunningtime());
        assertEquals("Derek Kolstad", film.getScreenplay());
        assertTrue(film.getReviews().isEmpty());
    }

    @Test
    @DisplayName("Getters for final Film fields should return correct values")
    void filmGetters() {
        assertEquals("Chad Stahelski", film.getDirection());
        assertEquals(101, film.getRunningtime());
        assertEquals("Derek Kolstad", film.getScreenplay());
    }

}