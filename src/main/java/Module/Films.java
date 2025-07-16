package Module;

import java.io.Serializable;
import java.util.List;

/**
 * Representa um filme, estendendo a classe AudioVisualMedia.
 * Adiciona atributos específicos de filmes, como direção, duração e roteiro.
 * Esta classe é imutável após a criação.
 */
public class Films extends AudioVisualMedia implements Serializable { // Added Serializable
    private static final long serialVersionUID = 1L; // Added
    private final String direction;
    private final int runningtime;
    private final String screenplay;

    /**
     * Construtor para criar uma instância de Films.
     * Valida a duração do filme.
     *
     * @param cast          Lista de strings representando o elenco.
     * @param seen          Indica se o filme já foi visto pelo usuário.
     * @param title         O título principal do filme. Não pode ser nulo ou vazio.
     * @param genres        Lista de gêneros associados ao filme. Não pode ser nula.
     * @param yearRelease   O ano de lançamento do filme.
     * @param originalTitle O título original do filme. Pode ser nulo.
     * @param whereWatch    Lista de strings indicando onde o filme pode ser assistido.
     * @param direction     O(s) diretor(es) do filme. Pode ser nulo ou vazio.
     * @param runningtime   A duração do filme em minutos. Deve ser maior que 0.
     * @param screenplay    O(s) roteirista(s) do filme. Pode ser nulo ou vazio.
     * @throws IllegalArgumentException se runningtime não for positivo, ou devido a validações da superclasse.
     */
    public Films(List<String> cast, boolean seen, String title, List<Genre> genres, int yearRelease, String originalTitle, List<String> whereWatch, String direction, int runningtime, String screenplay) {
        super(cast,seen, title, genres, yearRelease, originalTitle, whereWatch);
        if (runningtime <= 0) {
            throw new IllegalArgumentException("Running time must be positive.");
        }
        this.direction = (direction != null) ? direction.trim() : "";
        this.runningtime = runningtime;
        this.screenplay = (screenplay != null) ? screenplay.trim() : "";
    }

    /**
     * Retorna o(s) diretor(es) do filme.
     *
     * @return O nome do(s) diretor(es), ou null se não fornecido.
     */
    public String  getDirection(){
        return direction;
    }

    /**
     * Retorna a duração do filme em minutos.
     *
     * @return A duração em minutos (sempre positiva).
     */
    public int getRunningtime(){
        return runningtime;
    }

    /**
     * Retorna o(s) roteirista(s) do filme.
     *
     * @return O nome do(s) roteirista(s), ou null se não fornecido.
     */
    public String  getScreenplay(){
        return screenplay;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "Direção: " + (!direction.isEmpty() ? direction : "N/A") + "\n" +
                "Duração: " + runningtime + " min\n" +
                "Roteiro: " + (!screenplay.isEmpty() ? screenplay : "N/A");
    }
}