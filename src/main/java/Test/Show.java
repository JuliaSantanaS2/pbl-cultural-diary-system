package Test;

import Module.Genre;
import Module.Season;
import Module.Show;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ShowTest {

    private Genre genreDrama;
    private List<Genre> showGenres;
    private List<String> castList;
    private List<String> watchList;
    private Show show;
    private Season season1;
    private Season season2;

    @BeforeEach
    void setUp() {
        genreDrama = new Genre("Drama");
        showGenres = Collections.singletonList(genreDrama);
        castList = Arrays.asList("Bryan Cranston");
        watchList = Collections.singletonList("Netflix");

        show = new Show(
                castList, true, "Breaking Bad", showGenres, 2008,
                "Breaking Bad", watchList, 2013 // yearEnd
        );

        season1 = new Season(1, 7, "20/01/2008");
        season2 = new Season(2, 13, "08/03/2009");
    }

    @Test
    @DisplayName("Constructor should set all fields correctly (final included)")
    void constructor() {

        assertTrue(show.isSeen());
        assertEquals("Breaking Bad", show.getTitle());
        assertIterableEquals(showGenres, show.getGenres());
        assertEquals(2008, show.getYearRelease());

        assertEquals("Breaking Bad", show.getOriginalTitle());
        assertIterableEquals(watchList, show.getWhereWatch());
        assertIterableEquals(castList, show.getCast());

        assertEquals(2013, show.getYearEnd());
        assertNotNull(show.getSeasons());
        assertTrue(show.getSeasons().isEmpty());
        assertTrue(show.getReviews().isEmpty());
    }

    @Test
    @DisplayName("Getter for final yearEnd should return correct value")
    void showGetters() {
        assertEquals(2013, show.getYearEnd());

    }


    @Test
    @DisplayName("addSeason should add season to the list")
    void addSeason() {
        assertTrue(show.getSeasons().isEmpty());
        show.addSeason(season1);
        assertEquals(1, show.getSeasons().size());
        assertTrue(show.getSeasons().contains(season1));

        show.addSeason(season2);
        assertEquals(2, show.getSeasons().size());
        assertTrue(show.getSeasons().contains(season2));
    }

    @Test
    @DisplayName("getSeasons should return the correct list of seasons")
    void getSeasons() {
        assertTrue(show.getSeasons().isEmpty());
        show.addSeason(season1);
        show.addSeason(season2);
        List<Season> seasons = show.getSeasons();
        assertNotNull(seasons);
        assertEquals(2, seasons.size());
        assertIterableEquals(Arrays.asList(season1, season2), seasons);
    }


}