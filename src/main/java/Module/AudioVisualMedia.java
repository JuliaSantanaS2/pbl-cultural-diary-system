package Module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representa uma mídia audiovisual, estendendo a classe base Media.
 * Adiciona atributos comuns a filmes e séries, como título original,
 * locais para assistir e elenco.
 * Esta classe é imutável após a criação.
 */

public class AudioVisualMedia extends Media implements Serializable { // Added Serializable
    private static final long serialVersionUID = 1L; // Added

    private final String originalTitle;
    private final List<String> whereWatch;
    private final List<String> cast;

    /**
     * Construtor para criar uma instância de AudioVisualMedia.
     * Realiza cópias defensivas das listas fornecidas.
     *
     * @param cast          Lista de strings representando o elenco. Pode ser null ou vazia.
     * @param seen          Indica se a mídia já foi vista pelo usuário.
     * @param title         O título principal da mídia. Não pode ser nulo ou vazio.
     * @param genres        Lista de gêneros associados à mídia. Não pode ser nula.
     * @param yearRelease   O ano de lançamento da mídia.
     * @param originalTitle O título original da mídia (pode ser igual ao título principal). Pode ser nulo.
     * @param whereWatch    Lista de strings indicando onde a mídia pode ser assistida. Pode ser null ou vazia.
     * @throws IllegalArgumentException se title for nulo/vazio ou genres for nulo (validação da superclasse).
     */

    public AudioVisualMedia(List<String> cast, boolean seen, String title, List<Genre> genres, int yearRelease, String originalTitle, List<String> whereWatch) {
        super( seen, title, genres, yearRelease);
        this.originalTitle = (originalTitle != null && !originalTitle.trim().isEmpty()) ? originalTitle.trim() : title; // Ensure originalTitle is not empty if provided

        if (whereWatch != null) {
            this.whereWatch = new ArrayList<>(whereWatch.stream().map(String::trim).collect(Collectors.toList())); // Defensive copy
        } else {
            this.whereWatch = new ArrayList<>();
        }

        if (cast != null) {
            this.cast = new ArrayList<>(cast.stream().map(String::trim).collect(Collectors.toList())); // Defensive copy
        } else {
            this.cast = new ArrayList<>();
        }
    }

    /**
     * Retorna o título original da mídia audiovisual.
     *
     * @return O título original, ou null se não foi fornecido.
     */

    public String getOriginalTitle() {
        return originalTitle;
    }

    /**
     * Retorna uma cópia não modificável da lista de locais onde a mídia pode ser assistida.
     *
     * @return Uma lista não modificável de strings representando os locais para assistir.
     */

    public List<String> getWhereWatch() {
        return Collections.unmodifiableList(whereWatch);
    }

    /**
     * Retorna uma cópia não modificável da lista de membros do elenco.
     *
     * @return Uma lista não modificável de strings representando o elenco.
     */

    public List<String> getCast() {
        return Collections.unmodifiableList(cast);
    }

    /**
     * Retorna uma representação em string da mídia audiovisual.
     * Inclui informações da classe Media e adiciona título original, elenco e onde assistir.
     *
     * @return Uma string formatada com os detalhes da mídia audiovisual.
     */
    @Override
    public String toString() {
        return super.toString() + "\n" +
                "Título Original: " + (originalTitle != null ? originalTitle : "N/A") + "\n" +
                "Elenco: " + (cast != null && !cast.isEmpty() ? String.join(", ", cast) : "N/A") + "\n" +
                "Onde Assistir: " + (whereWatch != null && !whereWatch.isEmpty() ? String.join(", ", whereWatch) : "N/A");
    }
}