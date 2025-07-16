package Module;

import java.io.Serializable;
import java.util.List;

/**
 * Representa um livro, estendendo a classe base Media.
 * Adiciona atributos específicos de livros, como autor, editora, ISBN e posse de cópia.
 * Esta classe é imutável após a criação.
 */

public class Book extends Media implements Serializable { // Added Serializable
    private static final long serialVersionUID = 1L; // Added
    private final String author;
    private final String publisher;
    private final String isbn;
    private final boolean copy;

    /**
     * Construtor para criar uma instância de Book.
     * Valida os parâmetros específicos do livro.
     *
     * @param seen        Indica se o livro já foi lido pelo usuário.
     * @param title       O título do livro. Não pode ser nulo ou vazio.
     * @param genres      Lista de gêneros associados ao livro. Não pode ser nula.
     * @param yearRelease O ano de publicação do livro.
     * @param author      O autor do livro. Não pode ser nulo ou vazio.
     * @param publisher   A editora do livro. Pode ser nulo ou vazio.
     * @param isbn        O ISBN (International Standard Book Number) do livro. Não pode ser nulo ou vazio.
     * @param copy        Indica se o usuário possui uma cópia física do livro.
     * @throws IllegalArgumentException se title, genres, author ou isbn forem nulos/vazios.
     */


    public Book(boolean seen, String title, List<Genre> genres, int yearRelease, String author, String publisher, String isbn, boolean copy) {
        super(seen, title, genres, yearRelease);
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty.");
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty.");
        }
        this.author = author.trim();
        this.publisher = (publisher != null) ? publisher.trim() : ""; // Allow empty publisher
        this.isbn = isbn.trim();
        this.copy = copy;
    }

    /**
     * Retorna o nome do autor do livro.
     *
     * @return O nome do autor.
     */

    public String getAuthor (){
        return author;
    }

    /**
     * Retorna o nome da editora do livro.
     *
     * @return O nome da editora, ou null se não foi fornecido.
     */

    public String getPublisher(){
        return publisher;
    }

    /**
     * Retorna o ISBN do livro.
     *
     * @return O ISBN como uma string.
     */

    public String getIsbn(){
        return isbn;
    }

    /**
     * Retorna se o usuário possui uma cópia física do livro.
     *
     * @return true se o usuário possui uma cópia, false caso contrário.
     */

    public boolean getCopy(){
        return copy;
    }

    /**
     * Retorna uma representação em string do livro, incluindo informações da classe base Media
     * e os detalhes específicos do livro (autor, editora, ISBN, cópia).
     *
     * @return Uma string formatada com os detalhes do livro.
     */

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "Autor: " + author + "\n" +
                "Editora: " + (!publisher.isEmpty() ? publisher : "N/A") + "\n" +
                "ISBN: " + isbn + "\n" +
                "Cópia: " + (copy ? "Sim" : "Não");
    }
}