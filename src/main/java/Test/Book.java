package Test;

import Module.Book;
import Module.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {
    private Genre genreFantasy;
    private List<Genre> bookGenres;
    private Book bookOwnedRead;
    private Book bookNotOwnedNotRead;

    @BeforeEach
    void setUp() {
        genreFantasy = new Genre("Fantasia");
        bookGenres = Collections.singletonList(genreFantasy);

        bookOwnedRead = new Book(
                true, "The Hobbit", bookGenres, 1937,
                "J.R.R. Tolkien", "Allen & Unwin", "978-0547928227", true
        );

        bookNotOwnedNotRead = new Book(
                false, "Dune", Collections.singletonList(new Genre("SciFi")), 1965,
                "Frank Herbert", "Chilton Books", "978-0441172719", false
        );
    }

    @Test
    @DisplayName("Constructor should set all fields correctly (final included)")
    void constructor() {

        assertTrue(bookOwnedRead.isSeen());
        assertEquals("The Hobbit", bookOwnedRead.getTitle());
        assertIterableEquals(bookGenres, bookOwnedRead.getGenres());
        assertEquals(1937, bookOwnedRead.getYearRelease());
        assertEquals("J.R.R. Tolkien", bookOwnedRead.getAuthor());
        assertEquals("Allen & Unwin", bookOwnedRead.getPublisher());
        assertEquals("978-0547928227", bookOwnedRead.getIsbn());
        assertTrue(bookOwnedRead.getCopy());
        assertTrue(bookOwnedRead.getReviews().isEmpty());


        assertFalse(bookNotOwnedNotRead.isSeen());
        assertEquals("Dune", bookNotOwnedNotRead.getTitle());
        assertEquals("Frank Herbert", bookNotOwnedNotRead.getAuthor());
        assertFalse(bookNotOwnedNotRead.getCopy());
    }

    @Test
    @DisplayName("Getters for final fields should return correct values")
    void bookGetters() {
        assertEquals("J.R.R. Tolkien", bookOwnedRead.getAuthor());
        assertEquals("Allen & Unwin", bookOwnedRead.getPublisher());
        assertEquals("978-0547928227", bookOwnedRead.getIsbn());
        assertTrue(bookOwnedRead.getCopy());
    }


    @Test
    @DisplayName("toString should include Media and Book specific info")
    void testToString() {
        String str = bookOwnedRead.toString();
        assertTrue(str.contains("Título: The Hobbit"));
        assertTrue(str.contains("Gêneros: [Fantasia]"));
        assertTrue(str.contains("Autor: J.R.R. Tolkien"));
        assertTrue(str.contains("Editora: Allen & Unwin"));
        assertTrue(str.contains("ISBN: 978-0547928227"));
        assertTrue(str.contains("Cópia: Sim"));

        String str2 = bookNotOwnedNotRead.toString();
        assertTrue(str2.contains("Cópia: Não"));
    }
}