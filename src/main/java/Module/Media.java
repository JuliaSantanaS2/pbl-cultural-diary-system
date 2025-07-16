package Module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Classe base para todos os tipos de mídia cultural no sistema (Livros, Filmes, Séries).
 * Contém atributos e comportamentos comuns: título, ano de lançamento, status de consumo
 * (visto/lido), lista de gêneros associados e uma lista para reviews diretas
 * (tipicamente usada por Livros e Filmes, enquanto Séries usam reviews por temporada).
 * Instâncias desta classe base são imutáveis em seus atributos principais após a criação,
 * mas a lista de reviews pode ser modificada (adicionando reviews).
 */
public class Media implements Serializable { // Added Serializable
    private static final long serialVersionUID = 1L; // Added

    private final String title;
    private final int yearRelease;
    private final boolean seen;
    private final List<Genre> listGenres;
    private final List<Review> listReviews;

    /**
     * Construtor para criar uma instância base de Media.
     * Valida se o título não é nulo/vazio e se a lista de gêneros não é nula.
     * Cria cópias defensivas das listas para garantir encapsulamento.
     *
     * @param seen        Indica se a mídia já foi consumida (vista/lida).
     * @param title       O título da mídia. Não pode ser nulo ou vazio.
     * @param genres      A lista de gêneros associados à mídia. Não pode ser nula (pode ser vazia).
     * @param yearRelease O ano de lançamento da mídia.
     * @throws IllegalArgumentException se `title` for nulo/vazio ou `genres` for nulo.
     */
    public Media(boolean seen, String title, List<Genre> genres, int yearRelease) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Media title cannot be null or empty.");
        }
        if (genres == null) {
            throw new IllegalArgumentException("Genres list cannot be null (can be empty).");
        }
        this.title = title.trim();
        this.yearRelease = yearRelease;
        this.seen = seen;
        this.listGenres = new ArrayList<>(genres); // Defensive copy
        this.listReviews = new ArrayList<>();
    }

    /**
     * Retorna o título da mídia.
     *
     * @return O título (sem espaços extras nas pontas).
     */
    public String getTitle() {
        return title;
    }


    /**
     * Retorna o ano de lançamento da mídia.
     *
     * @return O ano de lançamento.
     */
    public int getYearRelease() {
        return yearRelease;
    }


    /**
     * Verifica se a mídia já foi consumida (vista/lida).
     *
     * @return `true` se já foi consumida, `false` caso contrário.
     */
    public boolean isSeen() {
        return seen;
    }

    /**
     * Retorna uma cópia não modificável da lista de gêneros associados a esta mídia.
     * Isso impede que o código externo modifique diretamente a lista interna de gêneros.
     *
     * @return Uma lista não modificável (`UnmodifiableList`) de objetos Genre.
     */
    public List<Genre> getGenres() {
        return Collections.unmodifiableList(listGenres);
    }


    /**
     * Retorna uma representação em string básica da mídia.
     * Inclui Título, Ano, Status (Visto/Lido) e Gêneros.
     * Classes filhas (Book, Films, Show) devem sobrescrever este método (`@Override`)
     * para adicionar seus detalhes específicos, idealmente chamando `super.toString()`
     * para incluir estas informações base.
     *
     * @return Uma string formatada com informações básicas da mídia.
     */
    @Override
    public String toString() {
        StringBuilder genresStr = new StringBuilder("[");
        if (listGenres != null) {
            for (int i = 0; i < listGenres.size(); i++) {
                genresStr.append(listGenres.get(i).getGenre());
                if (i < listGenres.size() - 1) {
                    genresStr.append(", ");
                }
            }
        }
        genresStr.append("]");

        StringBuilder reviewsStr = new StringBuilder();
        if (listReviews != null && !listReviews.isEmpty()) {
            for (Review r : listReviews) {
                reviewsStr.append("\n    - ").append(r.toString());
            }
        } else {
            reviewsStr.append(" (No reviews)");
        }

        return "Título: " + title + "\n" +
                "Ano de Lançamento: " + yearRelease + "\n" +
                "Gêneros: " + genresStr.toString() + "\n" +
                "Visto/Lido: " + (seen ? "Sim" : "Não") + "\n" +
                "Reviews:" + (listReviews.isEmpty() ? "[]" : reviewsStr.toString());
    }


    /**
     * Adiciona uma nova review à lista de reviews diretas desta mídia (livro/filme).
     * Garante que a review adicionada não seja nula.
     * Este método modifica o estado interno do objeto Media (sua lista de reviews).
     *
     * @param review O objeto Review a ser adicionado. Não pode ser nulo.
     * @throws NullPointerException se `review` for nulo.
     */
    public void addReview(Review review) {
        Objects.requireNonNull(review, "Review to add cannot be null.");
        listReviews.add(review);
    }

    /**
     * Retorna uma cópia não modificável da lista de reviews diretas associadas a esta mídia.
     * Para séries, as reviews relevantes normalmente estão dentro dos objetos `Season`.
     *
     * @return Uma lista não modificável (`UnmodifiableList`) de objetos Review.
     */
    public List<Review> getReviews() {
        return Collections.unmodifiableList(listReviews);
    }
}