package Module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa uma temporada específica de uma série de TV.
 * Contém o número da temporada, número de episódios, data de lançamento
 * e uma lista própria de {@link Review} para essa temporada específica.
 * A classe é imutável em seus atributos principais (número, episódios, data),
 * mas a lista de reviews pode ser modificada internamente pela adição de novas reviews.
 */
public class Season implements Serializable { // Added Serializable
    private static final long serialVersionUID = 1L; // Added
    private final int seasonNumber;
    private final int episodeCount;
    private final String releaseDate;
    private final List<Review> listReviews;

    /**
     * Construtor para criar uma instância de Season.
     * Valida se o número da temporada e o número de episódios são positivos,
     * e se a data de lançamento não é nula.
     *
     * @param seasonNumber O número da temporada (deve ser > 0).
     * @param episodeCount O número de episódios nesta temporada (deve ser > 0).
     * @param releaseDate  A data de lançamento da temporada (formato String). Não pode ser nulo.
     * @throws IllegalArgumentException se `seasonNumber` ou `episodeCount` não forem positivos,
     *                                  ou se `releaseDate` for nulo.
     */
    public Season(int seasonNumber, int episodeCount, String releaseDate) {
        if (seasonNumber <= 0) {
            throw new IllegalArgumentException("Season number must be positive.");
        }
        if (episodeCount <= 0) {
            throw new IllegalArgumentException("Episode count must be positive.");
        }
        if (releaseDate == null || releaseDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Release date cannot be null or empty.");
        }
        this.seasonNumber = seasonNumber;
        this.episodeCount = episodeCount;
        this.releaseDate = releaseDate.trim();
        this.listReviews = new ArrayList<>();
    }

    /**
     * Retorna o número desta temporada.
     *
     * @return O número inteiro da temporada (sempre positivo).
     */
    public int getSeasonNumber() {
        return seasonNumber;
    }

    /**
     * Retorna o número de episódios declarados para esta temporada.
     *
     * @return O número inteiro de episódios (sempre positivo).
     */
    public int getEpisodeCount() {
        return episodeCount;
    }

    /**
     * Retorna a data de lançamento desta temporada como foi fornecida.
     *
     * @return A data de lançamento como uma string.
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * Adiciona uma {@link Review} específica para esta temporada.
     * Garante que a review adicionada não seja nula.
     * Modifica o estado interno do objeto Season.
     *
     * @param review O objeto Review a ser adicionado à lista desta temporada. Não pode ser nulo.
     * @throws NullPointerException se `review` for nulo.
     */
    public void addReview(Review review) {
        Objects.requireNonNull(review, "Review cannot be null.");
        this.listReviews.add(review);
    }

    /**
     * Retorna uma cópia não modificável da lista de {@link Review} associadas
     * especificamente a esta temporada. Protege a lista interna contra modificações externas.
     *
     * @return Uma lista não modificável (`UnmodifiableList`) de objetos Review.
     *         Pode estar vazia se nenhuma review foi adicionada.
     */
    public List<Review> getReviews() {
        return Collections.unmodifiableList(listReviews);
    }

}