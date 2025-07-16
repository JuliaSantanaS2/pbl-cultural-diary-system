package Test;

import Control.WorkManager;
import Module.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkManagerTest {

    private WorkManager workManager;
    private Genre genreAction;
    private Genre genreComedy;
    private Genre genreDrama;

    private Book book1;
    private Films film1;
    private Show show1;
    private Season season1_1;
    private Season season1_2;

    @BeforeEach
    void setUp() {
        workManager = new WorkManager();

        genreAction = new Genre("Action");
        genreComedy = new Genre("Comedy");
        genreDrama = new Genre("Drama");
        workManager.addGenre(genreAction.getGenre());
        workManager.addGenre(genreComedy.getGenre());
        workManager.addGenre(genreDrama.getGenre());

        book1 = new Book(true, "Test Book Seen", Collections.singletonList(genreComedy), 2023, "Author A", "Publisher P", "ISBN-BOOK1", true);
        film1 = new Films(Arrays.asList("Actor X", "Actor Y"), true, "Test Film Seen", Collections.singletonList(genreAction), 2022, "Test Film", Collections.singletonList("Net"), "Director D", 120, "Writer W");
        show1 = new Show(Arrays.asList("Actor Z"), true, "Test Show Seen", Collections.singletonList(genreDrama), 2021, "Test Show", Collections.singletonList("HBO"), 2023);
        season1_1 = new Season(1, 10, "01/01/2021");
        season1_2 = new Season(2, 8, "01/01/2022");

        workManager.createBook(book1.isSeen(), book1.getTitle(), book1.getGenres(), book1.getYearRelease(), book1.getAuthor(), book1.getPublisher(), book1.getIsbn(), book1.getCopy());
        workManager.createFilm(film1.getCast(), film1.isSeen(), film1.getTitle(), film1.getGenres(), film1.getYearRelease(), film1.getOriginalTitle(), film1.getWhereWatch(), film1.getDirection(), film1.getRunningtime(), film1.getScreenplay());
        workManager.createShow(show1.getCast(), show1.isSeen(), show1.getTitle(), show1.getGenres(), show1.getYearRelease(), show1.getOriginalTitle(), show1.getWhereWatch(), show1.getYearEnd());
        workManager.createSeason(show1.getTitle(), season1_1.getSeasonNumber(), season1_1.getEpisodeCount(), season1_1.getReleaseDate());
        workManager.createSeason(show1.getTitle(), season1_2.getSeasonNumber(), season1_2.getEpisodeCount(), season1_2.getReleaseDate());
    }

    @Test
    @DisplayName("addGenre should add genre and sort library")
    void testAddGenre() {
        workManager.addGenre("SciFi");
        workManager.addGenre("Adventure");

        List<Genre> genres = workManager.getGenres();
        assertEquals(5, genres.size());
        assertEquals("Action", genres.get(0).getGenre());
        assertEquals("Adventure", genres.get(1).getGenre());
        assertEquals("Comedy", genres.get(2).getGenre());
        assertEquals("Drama", genres.get(3).getGenre());
        assertEquals("SciFi", genres.get(4).getGenre());
    }

    @Test
    @DisplayName("WorkManager stores and retrieves created media")
    void testMediaCreationAndRetrieval() {
        assertEquals(1, workManager.searchBooksByAuthor("Author A").size());
        assertEquals(1, workManager.searchFilmsByDirector("Director D").size());
        assertEquals(1, workManager.searchShowsByCast("Actor Z").size());

        List<Media> all = workManager.listMediaAlphabetically();
        assertEquals(3, all.size());
    }

    @Test
    @DisplayName("WorkManager adds review to Book correctly")
    void testAddReviewToBook() {
        int result = workManager.createReviewBook(book1.getTitle(), "Book Review Comment", 5, "DateR1");
        assertEquals(0, result);
        Book retrievedBook = workManager.searchBooksByISBN(book1.getIsbn()).get(0);
        assertEquals(1, retrievedBook.getReviews().size());
        assertEquals(5.0f, retrievedBook.getReviews().get(0).getStars());
    }

    @Test
    @DisplayName("WorkManager adds review to Film correctly")
    void testAddReviewToFilm() {
        int result = workManager.createReviewFilm(film1.getTitle(), "Film Review Comment", 4, "DateR2");
        assertEquals(0, result);
        Films retrievedFilm = workManager.searchFilmsByDirector(film1.getDirection()).get(0);
        assertEquals(1, retrievedFilm.getReviews().size());
        assertEquals(4.0f, retrievedFilm.getReviews().get(0).getStars());
    }

    @Test
    @DisplayName("WorkManager adds review to Show Season correctly")
    void testAddReviewToShowSeason() {
        int result = workManager.createReviewShow(show1.getTitle(), 2, "Show S2 Review", 3, "DateR3");
        assertEquals(0, result);

        Show retrievedShow = workManager.searchShowsByCast("Actor Z").get(0);
        Season season1 = retrievedShow.getSeasons().stream().filter(s -> s.getSeasonNumber() == 1).findFirst().orElseThrow();
        Season season2 = retrievedShow.getSeasons().stream().filter(s -> s.getSeasonNumber() == 2).findFirst().orElseThrow();

        assertTrue(season1.getReviews().isEmpty());
        assertEquals(1, season2.getReviews().size());
        assertEquals(3.0f, season2.getReviews().get(0).getStars());
    }

    @Test
    @DisplayName("WorkManager handles review errors (not seen, not found)")
    void testAddReviewErrors() {
        // Not Seen Book
        workManager.createBook(false, "Unseen Book WM", Collections.emptyList(), 2024, "A", "P", "ISBN-WM-UNSEEN", false);
        assertEquals(2, workManager.createReviewBook("Unseen Book WM", "C", 1, "D"), "Code 2 for unseen book");

        // Not Found Book
        assertEquals(1, workManager.createReviewBook("Not Found Book WM", "C", 1, "D"), "Code 1 for not found book");

        // Not Found Show
        assertEquals(1, workManager.createReviewShow("Not Found Show WM", 1, "C", 1, "D"), "Code 1 for not found show");

        // Season Not Found
        assertEquals(3, workManager.createReviewShow(show1.getTitle(), 99, "C", 1, "D"), "Code 3 for season not found");
    }

    @Test
    @DisplayName("WorkManager searches by various criteria correctly")
    void testSearchMethodsWM() {
        // Use data from setUp
        assertEquals(1, workManager.searchByTitle("Test Book Seen").size());
        assertEquals(1, workManager.searchByYear(2022).size());
        assertEquals(1, workManager.searchByGenre("Comedy").size());
        assertEquals(1, workManager.searchBooksByAuthor("Author A").size());
        assertEquals(1, workManager.searchBooksByISBN("ISBN-BOOK1").size());
        assertEquals(1, workManager.searchFilmsByDirector("Director D").size());
        assertEquals(1, workManager.searchFilmsByCast("Actor Y").size());
        assertEquals(1, workManager.searchShowsByCast("Actor Z").size());
    }

    @Test
    @DisplayName("WorkManager filters and sorts media correctly")
    void testGetFilteredAndSortedMediaWM() {
        workManager.createReviewBook(book1.getTitle(), "BRev", 3, "d1");
        workManager.createReviewFilm(film1.getTitle(), "FRev", 5, "d2");
        workManager.createReviewShow(show1.getTitle(), 1, "S1R", 4, "d3");
        workManager.createReviewShow(show1.getTitle(), 2, "S2R", 2, "d4");

        List<Media> r1 = workManager.getFilteredAndSortedMedia(2023, null, 1);
        assertEquals(1, r1.size());
        assertEquals(book1.getTitle(), r1.get(0).getTitle());

        List<Media> r2 = workManager.getFilteredAndSortedMedia(null, genreAction, 3);
        assertEquals(1, r2.size());
        assertEquals(film1.getTitle(), r2.get(0).getTitle());

        List<Media> r3 = workManager.getFilteredAndSortedMedia(null, null, 1);
        assertEquals(3, r3.size());
        assertEquals(film1.getTitle(), r3.get(0).getTitle());
        assertTrue(r3.get(1).getTitle().equals(book1.getTitle()) || r3.get(1).getTitle().equals(show1.getTitle()));
        assertTrue(r3.get(2).getTitle().equals(book1.getTitle()) || r3.get(2).getTitle().equals(show1.getTitle()));

    }

    @Test
    @DisplayName("WorkManager calculates average rating correctly")
    void testCalculateAverageWM() {
        workManager.createReviewBook(book1.getTitle(), "BRev", 4, "d1");
        workManager.createReviewFilm(film1.getTitle(), "FRev", 2, "d2");
        workManager.createReviewShow(show1.getTitle(), 1, "S1R", 5, "d3");
        workManager.createReviewShow(show1.getTitle(), 2, "S2R", 1, "d4");

        assertEquals(4.0f, WorkManager.calculateAverage(workManager.searchByTitle(book1.getTitle()).get(0)));
        assertEquals(2.0f, WorkManager.calculateAverage(workManager.searchByTitle(film1.getTitle()).get(0)));
        assertEquals(3.0f, WorkManager.calculateAverage(workManager.searchByTitle(show1.getTitle()).get(0)), 0.01f);
    }

}