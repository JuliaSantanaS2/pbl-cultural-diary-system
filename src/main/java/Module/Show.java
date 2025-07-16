package Module;

import java.io.Serializable;
import java.util.*;

/**
 * Representa uma série de TV, estendendo a classe {@link AudioVisualMedia}.
 * Adiciona o ano de encerramento (se aplicável) e uma lista de objetos {@link Season}
 * que representam as temporadas individuais da série. As {@link Review}s para séries
 * são tipicamente associadas a cada {@code Season} específica.
 * A classe é imutável em seus atributos principais, mas a lista de temporadas pode ser
 * modificada internamente pela adição de novas temporadas.
 */
public class Show extends AudioVisualMedia implements Serializable { // Added Serializable
    private static final long serialVersionUID = 1L; // Added

    private List<Season> seasons; // No longer final, to allow modification by addSeason
    private final int yearEnd;

    /**
     * Construtor para criar uma instância de Show.
     * Inicializa a lista de temporadas como uma lista vazia e ordenada.
     *
     * @param cast          Lista de strings representando o elenco principal.
     * @param seen          Indica se a série (ou alguma parte dela) já foi vista pelo usuário.
     * @param title         O título principal da série. Não pode ser nulo ou vazio.
     * @param genres        Lista de gêneros associados à série. Não pode ser nula.
     * @param yearRelease   O ano de lançamento da primeira temporada da série.
     * @param originalTitle O título original da série. Pode ser nulo.
     * @param whereWatch    Lista de strings indicando onde a série pode ser assistida.
     * @param yearEnd       O ano em que a série foi encerrada (pode ser 0 ou igual ao ano de lançamento
     *                      se ainda em andamento ou de temporada única). Deve ser >= ano de lançamento se > 0.
     * @throws IllegalArgumentException se `yearEnd` for menor que `yearRelease` (quando `yearEnd` > 0),
     *                                  ou devido a validações das superclasses.
     */
    public Show(List<String> cast, boolean seen, String title, List<Genre> genres, int yearRelease, String originalTitle, List<String> whereWatch, int yearEnd){
        super(cast,seen,title,genres,yearRelease,originalTitle,whereWatch);
        if (yearEnd != 0 && yearEnd < yearRelease) {
            throw new IllegalArgumentException("Year end cannot be before year release.");
        }
        this.yearEnd = yearEnd;
        this.seasons = new ArrayList<>(); // Initialized here
    }

    /**
     * Retorna o ano em que a série foi declarada como encerrada.
     *
     * @return O ano de encerramento, ou um valor como 0 se não aplicável, não definido, ou ainda em andamento.
     */
    public int getYearEnd(){return yearEnd;}

    /**
     * Adiciona um objeto {@link Season} (representando uma temporada) à lista interna de temporadas desta série.
     * Garante que a temporada adicionada não seja nula. A lista de temporadas é mantida ordenada
     * pelo número da temporada após a adição.
     * <p>
     * <b>Nota:</b> Este método não verifica se já existe uma temporada com o mesmo número.
     * Essa verificação deve ser feita externamente (ex: no {@code WorkManager}) antes de chamar este método.
     * </p>
     * Modifica o estado interno do objeto Show.
     *
     * @param season O objeto {@code Season} a ser adicionado. Não pode ser nulo.
     * @throws NullPointerException se {@code season} for nulo.
     */
    public void addSeason(Season season) {
        Objects.requireNonNull(season, "Season to add cannot be null.");
        // Prevent adding duplicate season numbers
        for (Season s : seasons) {
            if (s.getSeasonNumber() == season.getSeasonNumber()) {
                // Optionally throw an exception or just return
                System.err.println("Warning: Season " + season.getSeasonNumber() + " already exists for show " + getTitle());
                return;
            }
        }
        this.seasons.add(season);
        this.seasons.sort(Comparator.comparingInt(Season::getSeasonNumber)); // Keep sorted
    }

    /**
     * Retorna uma cópia não modificável da lista de temporadas associadas a esta série,
     * garantidamente ordenada pelo número da temporada.
     * Protege a lista interna contra modificações externas.
     *
     * @return Uma lista não modificável (`UnmodifiableList`) de objetos {@code Season}.
     *         Pode ser vazia se nenhuma temporada foi adicionada.
     */
    public List<Season> getSeasons() {
        // Return a defensive copy that is also unmodifiable
        return Collections.unmodifiableList(new ArrayList<>(seasons));
    }

    @Override
    public String toString() {
        StringBuilder seasonsStr = new StringBuilder();
        if (seasons != null && !seasons.isEmpty()) {
            for (Season s : seasons) {
                seasonsStr.append("\n  - Temporada ").append(s.getSeasonNumber())
                        .append(" (Episódios: ").append(s.getEpisodeCount())
                        .append(", Lançamento: ").append(s.getReleaseDate()).append(")");
                if (!s.getReviews().isEmpty()) {
                    seasonsStr.append("\n    Reviews da Temporada:");
                    for (Review r : s.getReviews()) {
                        seasonsStr.append("\n      * ").append(r.toString());
                    }
                }
            }
        } else {
            seasonsStr.append(" (Nenhuma temporada registrada)");
        }

        return super.toString() + "\n" +
                "Ano de Encerramento: " + (yearEnd > 0 ? yearEnd : "Em andamento/N/A") + "\n" +
                "Temporadas:" + seasonsStr.toString();
    }
}