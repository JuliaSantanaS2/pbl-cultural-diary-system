package Test;

import Module.AudioVisualMedia;
import Module.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AudioVisualMediaTest {

    private Genre genreSciFi;
    private List<Genre> avGenres;
    private List<String> castList;
    private List<String> watchList;
    private AudioVisualMedia avMedia;
    private AudioVisualMedia avMediaNullCast;

    @BeforeEach
    void setUp() {
        genreSciFi = new Genre("SciFi");
        avGenres = Collections.singletonList(genreSciFi);
        castList = Arrays.asList("Actor 1", "Actress 2");
        watchList = Arrays.asList("Netflix", "Prime Video");

        avMedia = new AudioVisualMedia(
                castList, true, "The Matrix", avGenres, 1999,
                "Matrix", watchList
        );
        avMediaNullCast = new AudioVisualMedia(
                null, false, "Null Cast AV", Collections.singletonList(new Genre("Drama")), 2021,
                "Null Cast AV", Collections.singletonList("Hulu")
        );
    }

    @Test
    @DisplayName("Constructor should set all fields correctly (final included)")
    void constructor() {
        assertTrue(avMedia.isSeen());
        assertEquals("The Matrix", avMedia.getTitle());
        assertIterableEquals(avGenres, avMedia.getGenres());
        assertEquals(1999, avMedia.getYearRelease());
        assertEquals("Matrix", avMedia.getOriginalTitle());
        assertIterableEquals(watchList, avMedia.getWhereWatch());
        assertNotNull(avMedia.getCast());
        assertIterableEquals(castList, avMedia.getCast());
        assertTrue(avMedia.getReviews().isEmpty());

        assertNotNull(avMediaNullCast.getCast());
        assertTrue(avMediaNullCast.getCast().isEmpty());
    }

    @Test
    @DisplayName("Getters for final fields should return correct AV specific values")
    void avGetters() {
        assertEquals("Matrix", avMedia.getOriginalTitle());
        assertIterableEquals(watchList, avMedia.getWhereWatch());
        assertIterableEquals(castList, avMedia.getCast());
    }

}