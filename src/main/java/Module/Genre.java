package Module;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa um gênero cultural (ex: Ficção Científica, Drama, Romance).
 * Implementa Comparable para permitir ordenação alfabética (case-insensitive).
 * Sobrescreve equals e hashCode para comparação e uso em coleções baseadas em hash,
 * tratando nomes de gênero de forma case-insensitive.
 * Esta classe é imutável após a criação.
 */
public class Genre implements Comparable<Genre>, Serializable { // Added Serializable
    private static final long serialVersionUID = 1L; // Added
    private String genre;

    /**
     * Construtor para criar uma instância de Genre.
     * Armazena o nome do gênero após remover espaços extras nas pontas.
     *
     * @param genre O nome do gênero.
     * @throws IllegalArgumentException se o nome do gênero for nulo ou consistir apenas de espaços em branco.
     */
    public Genre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre name cannot be null or empty.");
        }
        this.genre = genre.trim();
    }

    /**
     * Retorna o nome do gênero.
     *
     * @return O nome do gênero (sem espaços extras nas pontas).
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Retorna a representação em string do gênero (o próprio nome).
     *
     * @return O nome do gênero.
     */
    @Override
    public String toString() {
        return genre;
    }

    /**
     * Compara este gênero com outro gênero alfabeticamente, ignorando maiúsculas/minúsculas.
     * Essencial para ordenação (ex: Collections.sort).
     *
     * @param other O outro objeto Genre a ser comparado. Não deve ser nulo.
     * @return um valor negativo se este gênero vier antes de `other`,
     *         zero se forem considerados iguais (ignorando case),
     *         ou um valor positivo se este gênero vier depois de `other`.
     * @throws NullPointerException se `other` for nulo.
     */
    @Override
    public int compareTo(Genre other) {
        Objects.requireNonNull(other, "Cannot compare to a null Genre.");
        return this.genre.compareToIgnoreCase(other.genre);
    }

    /**
     * Verifica se este gênero é igual a outro objeto.
     * A igualdade é baseada no nome do gênero, ignorando maiúsculas/minúsculas.
     * Fundamental para operações como `List.contains()`, `Set.add()`, etc.
     *
     * @param o O objeto a ser comparado com este Genre.
     * @return `true` se `o` for um objeto Genre com o mesmo nome (ignorando case), `false` caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre otherGenre = (Genre) o;
        return this.genre.equalsIgnoreCase(otherGenre.genre);
    }

    /**
     * Retorna um código hash para este gênero, baseado no nome do gênero em minúsculas.
     * Garante que gêneros iguais (pelo método `equals`) tenham o mesmo `hashCode`.
     * Essencial para o bom funcionamento de coleções baseadas em hash como `HashSet` e `HashMap`.
     *
     * @return O código hash do gênero.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.genre.toLowerCase());
    }
}