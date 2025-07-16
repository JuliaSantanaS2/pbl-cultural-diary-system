package Control;

import Module.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Arrays;

import java.util.stream.Collectors;

/**
 * Classe controladora principal (Controller no padrão MVC) para a aplicação Diário Cultural.
 * Responsável por gerenciar as coleções de dados em memória (Livros, Filmes, Séries,
 * Gêneros, Reviews) e por implementar a lógica de negócios da aplicação.
 *
 * <p>Atua como intermediário entre a camada de Visualização (View), que interage com o usuário,
 * e a camada de Modelo (Model), que representa os dados. Fornece métodos públicos para
 * criar, buscar, listar, avaliar e gerenciar as mídias culturais e gêneros.</p>
 *
 * <p><b>Importante:</b> Na versão atual, todos os dados são armazenados em listas na memória
 * RAM e são perdidos quando a aplicação é encerrada (dados voláteis). Não há persistência
 * implementada.</p>
 *
 *
 * @see View.Screen
 * @see View.Create
 * @see View.CreateReview
 * @see View.Search
 *
 * @author Julia Santana de Oliveira e Davi Figuerêdo
 */
public class WorkManager {

    private static final String DATA_FILE = "cultural_diary.dat"; // Added for persistence

    /** Lista para armazenar todos os objetos {@link Genre} cadastrados. Mantida ordenada alfabeticamente. */
    private List<Genre> genreLibrary; // Removed final
    /** Lista geral que armazena todos os objetos {@link Review} criados (de livros, filmes e temporadas de séries). */
    private final List<Review> reviewLibrary; // Kept for now, but its direct persistence is less critical if reviews are well-nested.
    /** Lista para armazenar especificamente os objetos {@link Book} cadastrados. */
    private List<Book> bookLibrary; // Removed final
    /** Lista para armazenar especificamente os objetos {@link Films} cadastrados. */
    private List<Films> filmLibrary; // Removed final
    /** Lista para armazenar especificamente os objetos {@link Show} cadastrados. */
    private List<Show> showLibrary; // Removed final
    /** Lista agregada contendo referências a todos os objetos {@link Media} (Book, Films, Show)
     * para facilitar buscas e listagens gerais que abrangem todos os tipos de mídia. */
    private final List<Media> media; // This will be repopulated after loading


    /**
     * Construtor padrão do WorkManager.
     * Inicializa todas as listas de dados (`genreLibrary`, `reviewLibrary`, `bookLibrary`,
     * `filmLibrary`, `showLibrary`, `media`) como {@link ArrayList}s vazias.
     * Tries to load data from file, otherwise initializes with example data.
     */
    public WorkManager() {
        // Initialize lists first
        this.genreLibrary = new ArrayList<>();
        this.reviewLibrary = new ArrayList<>(); // Consider if this is truly needed for persistence or can be reconstructed
        this.bookLibrary = new ArrayList<>();
        this.filmLibrary = new ArrayList<>();
        this.showLibrary = new ArrayList<>();
        this.media = new ArrayList<>();
        initializeExampleData();

    }




    // --- Persistence Methods ---
    @SuppressWarnings("unchecked")
    private boolean loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return false;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            this.genreLibrary = (List<Genre>) ois.readObject();
            this.bookLibrary = (List<Book>) ois.readObject();
            this.filmLibrary = (List<Films>) ois.readObject();
            this.showLibrary = (List<Show>) ois.readObject();
            // Note: reviewLibrary and media list will be repopulated
            System.out.println("Data loaded from " + DATA_FILE);
            return true;
        } catch (FileNotFoundException e) {
            System.err.println("Data file not found (should have been caught by exists()): " + e.getMessage());
            return false; // File does not exist
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace(); // For debugging
            // Consider deleting the corrupt file here or backing it up
            // file.delete(); // Or rename to .bak
            return false; // Error during loading
        }
    }

    /**
     * Salva os dados atuais das bibliotecas principais (gêneros, livros, filmes e séries)
     * em um arquivo binário especificado pela constante {@code DATA_FILE}.
     * Utiliza a serialização de objetos Java para persistir as listas:
     * <ul>
     * <li>{@code genreLibrary}</li>
     * <li>{@code bookLibrary}</li>
     * <li>{@code filmLibrary}</li>
     * <li>{@code showLibrary}</li>
     * </ul>
     * A lista {@code reviewLibrary} não é salva diretamente, pois as reviews são
     * consideradas parte dos objetos de mídia (livros, filmes, temporadas de séries)
     * e são salvas aninhadas dentro deles. Similarmente, a lista agregada {@code media}
     * também não é salva, pois ela é reconstruída a partir das listas específicas
     * de cada tipo de mídia após o carregamento.
     * <p>
     * Em caso de erro durante o processo de salvamento (por exemplo, problemas de I/O),
     * uma mensagem de erro é impressa no console de erro padrão, juntamente com o
     * rastreamento da pilha da exceção para fins de depuração.
     * </p>
     * Este método é chamado internamente após operações que modificam
     * os dados principais (como adição de novas mídias, gêneros, temporadas ou reviews)
     * para garantir a persistência das alterações.
     *
     * @see #loadData()
     * @see #DATA_FILE
     */

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(genreLibrary);
            oos.writeObject(bookLibrary);
            oos.writeObject(filmLibrary);
            oos.writeObject(showLibrary);
            // Note: reviewLibrary is not directly saved; it's derived or part of other objects.
            // The `media` list is also derived.
            System.out.println("Data saved to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
            e.printStackTrace(); // For debugging
        }
    }


    private void initializeExampleData() {
        System.out.println("--- Inicializando Dados de Exemplo ---");

        // --- 1. Criação de Gêneros ---
        addGenre("Ficção Científica");
        addGenre("Fantasia");
        addGenre("Drama");
        addGenre("Aventura");
        addGenre("Suspense");
        addGenre("Romance");
        addGenre("Ação");
        addGenre("Comédia");
        addGenre("Mistério");
        addGenre("Histórico");
        addGenre("Biografia");
        addGenre("Crime");

        // --- 2. LIVROS (10 EXEMPLOS) ---
        createBook(true, "Duna", Arrays.asList(getGenre("Ficção Científica"), getGenre("Aventura")), 1965, "Frank Herbert", "Editora Aleph", "978-8576570076", true);
        createBook(false, "O Nome do Vento", Arrays.asList(getGenre("Fantasia"), getGenre("Aventura")), 2007, "Patrick Rothfuss", "Arqueiro", "978-8580410657", false);
        createBook(true, "Cem Anos de Solidão", Arrays.asList(getGenre("Drama"), getGenre("Romance")), 1967, "Gabriel García Márquez", "Record", "978-8501012903", true);
        createBook(true, "A Revolução dos Bichos", Arrays.asList(getGenre("Drama"), getGenre("Ficção Científica")), 1945, "George Orwell", "Companhia das Letras", "978-8535909062", false);
        createBook(true, "O Pequeno Príncipe", Arrays.asList(getGenre("Aventura"), getGenre("Fantasia")), 1943, "Antoine de Saint-Exupéry", "Agir", "978-8522005470", true);
        createBook(false, "Morte no Nilo", Collections.singletonList(getGenre("Mistério")), 1937, "Agatha Christie", "L&PM Editores", "978-8525413158", false);
        createBook(true, "Orgulho e Preconceito", Collections.singletonList(getGenre("Romance")), 1813, "Jane Austen", "Martin Claret", "978-8572322301", true);
        createBook(false, "O Código Da Vinci", Collections.singletonList(getGenre("Suspense")), 2003, "Dan Brown", "Arqueiro", "978-8580410053", false);
        createBook(true, "Sapiens: Uma Breve História da Humanidade", Collections.singletonList(getGenre("Histórico")), 2011, "Yuval Noah Harari", "Companhia das Letras", "978-8535925017", true);
        createBook(false, "Dom Quixote", Collections.singletonList(getGenre("Aventura")), 1605, "Miguel de Cervantes", "Penguin Companhia", "978-8563560761", true);


// --- 3. FILMES (10 EXEMPLOS) ---
        createFilm(Arrays.asList("Matthew McConaughey", "Anne Hathaway"), true, "Interestelar", Arrays.asList(getGenre("Ficção Científica"), getGenre("Aventura")), 2014, "Interstellar", Arrays.asList("HBO Max", "Prime Video"), "Christopher Nolan", 169, "Jonathan Nolan");
        createFilm(Arrays.asList("Elijah Wood", "Ian McKellen"), true, "O Senhor dos Anéis: A Sociedade do Anel", Arrays.asList(getGenre("Fantasia"), getGenre("Aventura")), 2001, "The Lord of the Rings: The Fellowship of the Ring", Arrays.asList("HBO Max"), "Peter Jackson", 178, "Fran Walsh");
        createFilm(Arrays.asList("Liam Neeson", "Ben Kingsley"), true, "A Lista de Schindler", Collections.singletonList(getGenre("Drama")), 1993, "Schindler's List", Arrays.asList("Netflix"), "Steven Spielberg", 195, "Steven Zaillian");
        createFilm(Arrays.asList("Johnny Depp", "Orlando Bloom"), false, "Piratas do Caribe: A Maldição do Pérola Negra", Collections.singletonList(getGenre("Aventura")), 2003, "Pirates of the Caribbean: The Curse of the Black Pearl", Arrays.asList("Disney+"), "Gore Verbinski", 143, "Ted Elliott");
        createFilm(Arrays.asList("Jodie Foster", "Anthony Hopkins"), true, "O Silêncio dos Inocentes", Collections.singletonList(getGenre("Suspense")), 1991, "The Silence of the Lambs", Arrays.asList("Amazon Prime Video"), "Jonathan Demme", 118, "Ted Tally");
        createFilm(Arrays.asList("Julia Roberts", "Hugh Grant"), false, "Um Lugar Chamado Notting Hill", Collections.singletonList(getGenre("Romance")), 1999, "Notting Hill", Arrays.asList("Globoplay"), "Roger Michell", 124, "Richard Curtis");
        createFilm(Arrays.asList("Bruce Willis", "Alan Rickman"), true, "Duro de Matar", Collections.singletonList(getGenre("Ação")), 1988, "Die Hard", Arrays.asList("Star+"), "John McTiernan", 132, "Jeb Stuart");
        createFilm(Arrays.asList("Bradley Cooper", "Ed Helms"), false, "Se Beber, Não Case!", Collections.singletonList(getGenre("Comédia")), 2009, "The Hangover", Arrays.asList("HBO Max"), "Todd Phillips", 100, "Jon Lucas");
        createFilm(Arrays.asList("Brad Pitt", "Morgan Freeman"), true, "Seven: Os Sete Crimes Capitais", Arrays.asList(getGenre("Crime"), getGenre("Suspense")), 1995, "Se7en", Arrays.asList("HBO Max"), "David Fincher", 127, "Andrew Kevin Walker");
        createFilm(Arrays.asList("Amy Adams", "Jeremy Renner"), false, "A Chegada", Collections.singletonList(getGenre("Ficção Científica")), 2016, "Arrival", Arrays.asList("Netflix"), "Denis Villeneuve", 116, "Eric Heisserer");


// --- 4. SÉRIES (10 EXEMPLOS) ---
        createShow(Arrays.asList("Millie Bobby Brown", "David Harbour"), true, "Stranger Things", Arrays.asList(getGenre("Ficção Científica"), getGenre("Suspense")), 2016, "Stranger Things", Arrays.asList("Netflix"), 0);
        createShow(Arrays.asList("Henry Cavill", "Freya Allan"), true, "The Witcher", Arrays.asList(getGenre("Fantasia"), getGenre("Aventura")), 2019, "The Witcher", Arrays.asList("Netflix"), 0);
        createShow(Arrays.asList("Claire Foy", "Olivia Colman"), false, "The Crown", Collections.singletonList(getGenre("Histórico")), 2016, "The Crown", Arrays.asList("Netflix"), 0);
        createShow(Arrays.asList("Bryan Cranston", "Aaron Paul"), true, "Breaking Bad", Arrays.asList(getGenre("Drama"), getGenre("Crime")), 2008, "Breaking Bad", Arrays.asList("Netflix"), 2013);
        createShow(Arrays.asList("Tom Hiddleston", "Owen Wilson"), true, "Loki", Collections.singletonList(getGenre("Aventura")), 2021, "Loki", Arrays.asList("Disney+"), 0);
        createShow(Arrays.asList("Daniel Kaluuya", "Jesse Plemons"), false, "Black Mirror", Arrays.asList(getGenre("Ficção Científica"), getGenre("Drama")), 2011, "Black Mirror", Arrays.asList("Netflix"), 0);
        createShow(Arrays.asList("Kit Harington", "Emilia Clarke"), true, "Game of Thrones", Arrays.asList(getGenre("Fantasia"), getGenre("Aventura")), 2011, "Game of Thrones", Arrays.asList("HBO Max"), 2019);
        createShow(Arrays.asList("Jennifer Aniston", "Courteney Cox"), true, "Friends", Collections.singletonList(getGenre("Comédia")), 1994, "Friends", Arrays.asList("HBO Max"), 2004);
        createShow(Arrays.asList("Matthew McConaughey", "Woody Harrelson"), true, "True Detective", Collections.singletonList(getGenre("Crime")), 2014, "True Detective", Arrays.asList("HBO Max"), 0);
        createShow(Arrays.asList("Benedict Cumberbatch", "Martin Freeman"), false, "Sherlock", Collections.singletonList(getGenre("Mistério")), 2010, "Sherlock", Arrays.asList("Globoplay"), 0);


// --- 5. TEMPORADAS (10 EXEMPLOS) ---
// Crie as temporadas para as séries existentes.
        createSeason("Stranger Things", 1, 8, "15/07/2016");
        createSeason("Stranger Things", 2, 9, "27/10/2017");
        createSeason("Stranger Things", 3, 8, "04/07/2019");
        createSeason("Stranger Things", 4, 9, "27/05/2022");
        createSeason("The Witcher", 1, 8, "20/12/2019");
        createSeason("The Witcher", 2, 8, "17/12/2021");
        createSeason("The Witcher", 3, 8, "29/06/2023");
        createSeason("Breaking Bad", 1, 7, "20/01/2008");
        createSeason("Breaking Bad", 2, 13, "08/03/2009");
        createSeason("Game of Thrones", 1, 10, "17/04/2011");


// --- 6. REVIEWS (10 EXEMPLOS) ---
// Certifique-se de que as mídias/temporadas revisadas estejam marcadas como 'vista/lida' (seen: true).
        createReviewBook("Duna", "Uma jornada épica e complexa, leitura obrigatória!", 5, dateNow());
        createReviewBook("Cem Anos de Solidão", "Fascinante realismo mágico, mas um pouco denso às vezes.", 4, dateNow());
        createReviewFilm("Interestelar", "Visualmente deslumbrante e emocionalmente impactante. Final confuso.", 5, dateNow());
        createReviewFilm("O Silêncio dos Inocentes", "Um clássico do suspense que te prende do início ao fim.", 4, dateNow());
        createReviewFilm("Duro de Matar", "Ação pura e um protagonista icônico. Yippee-ki-yay!", 3, dateNow());
        createReviewShow("Stranger Things", 1, "Início nostálgico e cheio de mistério. Viciante!", 5, dateNow());
        createReviewShow("The Witcher", 1, "Boas atuações e efeitos visuais, mas a cronologia é um desafio.", 4, dateNow());
        createReviewShow("Breaking Bad", 1, "Walter White se transformando... que série!", 5, dateNow());
        createReviewShow("Game of Thrones", 1, "Personagens bem construídos e um mundo imersivo. Começo promissor!", 5, dateNow());
        createReviewShow("Friends", 1, "Engraçada e leve, perfeita para descontrair.", 4, dateNow());



        class GenreFinder { // Helper class, consider moving or making static if used elsewhere
            static Genre find(String name, List<Genre> library) {
                for (Genre g : library) {
                    if (g.getGenre().equalsIgnoreCase(name)) {
                        return g;
                    }
                }
                System.out.println("AVISO: Gênero '" + name + "' não encontrado durante inicialização! Criando...");
                // If not found, create it, add it, and return it for robustness in example data
                Genre newGenre = new Genre(name);
                // This direct add bypasses the WorkManager's addGenre logic (sorting, saving)
                // which might be an issue. For examples, it's okay, but better to use the public method.
                // However, addGenre itself calls saveData, which is problematic during initialization.
                // For now, this direct add is simpler for the example.
                // A better way would be to collect all example genres and add them once.
                // library.add(newGenre); Collections.sort(library);
                // Or ensure `addGenre` doesn't save during `initializeExampleData`
                return newGenre; // Temporary: return a new one, won't be added to the main library here
            }
        }
    }

    private Genre getGenre(String genreName) {
        return getGenres().stream()
                .filter(g -> g.getGenre().equalsIgnoreCase(genreName))
                .findFirst()
                .orElseGet(() -> {
                    addGenre(genreName); // Adiciona se não existe
                    return getGenres().stream()
                            .filter(g -> g.getGenre().equalsIgnoreCase(genreName))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Gênero '" + genreName + "' não pôde ser criado ou encontrado."));
                });
    }

    /**
     * Adiciona um novo gênero à biblioteca (`genreLibrary`).
     * O nome do gênero é tratado para remover espaços extras e a adição é feita
     * apenas se o gênero (ignorando maiúsculas/minúsculas) ainda não existir.
     * A lista de gêneros é mantida ordenada alfabeticamente.
     * Imprime mensagens de feedback no console sobre o resultado da operação.
     *
     * //@param genreName O nome do gênero a ser adicionado. Se for nulo, vazio ou consistir
     *                  apenas de espaços, a operação é ignorada com uma mensagem de aviso.
     */

    private String dateNow() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void addGenre(String genreName){
        if (genreName == null || genreName.trim().isEmpty()) {
            System.out.println("Nome do gênero inválido.");
            return;
        }
        Genre newGenre = new Genre(genreName.trim());
        // Check for duplicates ignoring case
        boolean exists = genreLibrary.stream().anyMatch(g -> g.getGenre().equalsIgnoreCase(newGenre.getGenre()));
        if (!exists) {
            genreLibrary.add(newGenre);
            Collections.sort(genreLibrary);
            System.out.println("Gênero '" + newGenre.getGenre() + "' adicionado.");
            saveData(); // Save after adding
        } else {
            System.out.println("Gênero '" + newGenre.getGenre() + "' já existe.");
        }
    }

    /**
     * Retorna uma visão não modificável da lista completa de gêneros cadastrados
     * na `genreLibrary`. Isso impede que o código externo altere a lista diretamente.
     *
     * @return Uma {@code List<Genre>} não modificável contendo todos os gêneros cadastrados,
     *         ordenada alfabeticamente.
     */
    public List<Genre> getGenres() {
        return Collections.unmodifiableList(genreLibrary);
    }


    /**
     * Imprime no console a lista de nomes dos gêneros atualmente cadastrados.
     * Este método é destinado principalmente para fins de teste ou confirmação visual rápida
     * pela camada de Visualização (View), pois imprime diretamente no `System.out`.
     */
    public void getGenresTest() {
        if (genreLibrary.isEmpty()) {
            System.out.println("Nenhum gênero cadastrado.");
            return;
        }
        for (Genre genre : genreLibrary) {
            System.out.println("- " + genre.getGenre());
        }
    }


    // METHODS RELATED TO BOOKS =====================================================================================

    /**
     * Cria uma nova instância de {@link Book} com os dados fornecidos e a adiciona
     * à biblioteca específica de livros (`bookLibrary`) e também à lista agregada
     * de todas as mídias (`media`). Trata exceções durante a criação do objeto Book.
     *
     * @param seen        Indica se o livro já foi lido pelo usuário.
     * @param title       O título do livro (não nulo/vazio).
     * @param genres      A lista de {@link Genre}s do livro (não nula).
     * @param yearRelease O ano de publicação do livro.
     * @param author      O autor do livro (não nulo/vazio).
     * @param publisher   A editora do livro (pode ser nula/vazia).
     * @param isbn        O ISBN do livro (não nulo/vazio).
     * @param copy        Indica se o usuário possui uma cópia física.
     */
    public void createBook(boolean seen, String title,List<Genre> genres, int yearRelease, String author, String publisher, String isbn, boolean copy){
        try {
            Book book = new Book (seen, title, genres, yearRelease, author, publisher, isbn, copy);
            bookLibrary.add(book);
            media.add(book); // Also add to aggregated list
            saveData(); // Save after creation
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao criar livro: " + e.getMessage());
        }
    }

    /**
     * Cria uma nova {@link Review} para um livro específico, identificado pelo título.
     * A busca pelo livro ignora maiúsculas/minúsculas no título.
     * A review só é adicionada se o livro for encontrado e estiver marcado como lido (`isSeen() == true`).
     * A review criada é adicionada tanto à lista de reviews do próprio objeto {@link Book}
     * quanto à lista geral `reviewLibrary`.
     *
     * @param title      O título do livro a ser avaliado (case-insensitive).
     * @param comment    O texto da avaliação (não nulo/vazio).
     * @param stars      A nota atribuída (deve estar entre 1 e 5).
     * @param reviewDate A data da avaliação (String formatada, não nula).
     * @return Código de status inteiro:
     *         <ul>
     *           <li>0: Sucesso - Review criada e adicionada.</li>
     *           <li>1: Erro - Livro com o título especificado não encontrado.</li>
     *           <li>2: Erro - Livro encontrado, mas não está marcado como lido.</li>
     *           <li>99: Erro - Dados fornecidos para a review (comentário, estrelas, data) são inválidos.</li>
     *         </ul>
     */
    public int createReviewBook(String title, String comment, int stars, String reviewDate) {
        for (Book book : bookLibrary) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                if (book.isSeen()) {
                    try {
                        Review newReview = new Review(comment, stars, reviewDate);
                        book.addReview(newReview); // addReview is now public in Media
                        reviewLibrary.add(newReview); // Keep this for now
                        saveData(); // Save after adding review
                        return 0;
                    } catch (IllegalArgumentException e) {
                        System.err.println("Erro ao criar review: " + e.getMessage());
                        return 99; // Invalid review data
                    }
                } else {
                    return 2; // Not seen
                }
            }
        }
        return 1; // Book not found
    }

    /**
     * Retorna uma lista contendo apenas os títulos de todos os livros presentes
     * na `bookLibrary`. A lista retornada é ordenada alfabeticamente (ignorando case).
     * Útil para a camada View apresentar opções de seleção de livros ao usuário.
     *
     * @return Uma {@code List<String>} com os títulos dos livros, ordenada alfabeticamente.
     *         Retorna uma lista vazia se não houver livros cadastrados.
     */
    public List<String> getBooksName() {
        return bookLibrary.stream()
                .map(Book::getTitle)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }


    // METHODS RELATED TO FILMS =============================================================================================================================

    /**
     * Cria uma nova instância de {@link Films} com os dados fornecidos e a adiciona
     * à biblioteca específica de filmes (`filmLibrary`) e à lista agregada `media`.
     * Trata exceções durante a criação do objeto Films.
     *
     * @param cast          Lista de strings do elenco principal.
     * @param seen          Indica se o filme já foi visto.
     * @param title         O título do filme (não nulo/vazio).
     * @param genres        A lista de {@link Genre}s do filme (não nula).
     * @param yearRelease   O ano de lançamento.
     * @param originalTitle O título original (pode ser nulo).
     * @param whereWatch    Lista de strings de onde assistir.
     * @param direction     O(s) diretor(es) (pode ser nulo/vazio).
     * @param runningtime   A duração em minutos (deve ser > 0).
     * @param screenplay    O(s) roteirista(s) (pode ser nulo/vazio).
     */
    public void createFilm(List<String> cast, boolean seen, String title, List<Genre> genres, int yearRelease, String originalTitle, List<String> whereWatch, String direction, int runningtime, String screenplay){
        try {
            Films film = new Films (cast, seen, title, genres, yearRelease, originalTitle, whereWatch, direction, runningtime, screenplay);
            filmLibrary.add(film);
            media.add(film);
            saveData(); // Save after creation
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao criar filme: " + e.getMessage());
        }
    }

    /**
     * Cria uma nova {@link Review} para um filme específico, identificado pelo título.
     * A busca pelo filme ignora maiúsculas/minúsculas no título.
     * A review só é adicionada se o filme for encontrado e estiver marcado como visto (`isSeen() == true`).
     * A review criada é adicionada à lista de reviews do objeto {@link Films} e à lista geral `reviewLibrary`.
     *
     * @param title      O título do filme a ser avaliado (case-insensitive).
     * @param comment    O texto da avaliação (não nulo/vazio).
     * @param stars      A nota atribuída (deve estar entre 1 e 5).
     * @param reviewDate A data da avaliação (String formatada, não nula).
     * @return Código de status inteiro:
     *         <ul>
     *           <li>0: Sucesso - Review criada e adicionada.</li>
     *           <li>1: Erro - Filme com o título especificado não encontrado.</li>
     *           <li>2: Erro - Filme encontrado, mas não está marcado como visto.</li>
     *           <li>99: Erro - Dados fornecidos para a review são inválidos.</li>
     *         </ul>
     */
    public int createReviewFilm(String title, String comment, int stars, String reviewDate) {
        for (Films film : filmLibrary) {
            if (film.getTitle() != null && film.getTitle().equalsIgnoreCase(title)) {
                if (film.isSeen()) {
                    try {
                        Review newReview = new Review(comment, stars, reviewDate);
                        film.addReview(newReview);
                        reviewLibrary.add(newReview);
                        saveData(); // Save after adding review
                        return 0;
                    } catch (IllegalArgumentException e) {
                        System.err.println("Erro ao criar review: " + e.getMessage());
                        return 99; // Invalid review data
                    }
                } else {
                    return 2; // Not seen
                }
            }
        }
        return 1; // Film not found
    }

    // AUXILIARY METHOD OF "createReviewFilm": SELECT THE CORRESPONDING FILM IN CLASS CREATE REVIEW
    /**
     * Retorna uma lista contendo apenas os títulos de todos os filmes presentes
     * na `filmLibrary`. A lista é ordenada alfabeticamente (ignorando case).
     * Útil para a View apresentar opções de seleção de filmes.
     *
     * @return Uma {@code List<String>} com os títulos dos filmes, ordenada.
     *         Retorna lista vazia se não houver filmes.
     */
    public List<String> getFilmName() {
        return filmLibrary.stream()
                .map(Films::getTitle)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    // METHODS RELATED TO SHOW/SEASON =============================================================================================================================

    /**
     * Cria uma nova instância de {@link Show} com os dados fornecidos e a adiciona
     * à biblioteca específica de séries (`showLibrary`) e à lista agregada `media`.
     * Trata exceções durante a criação do objeto Show.
     *
     * @param cast          Lista de strings do elenco principal.
     * @param seen          Indica se a série (ou parte dela) já foi vista.
     * @param title         O título da série (não nulo/vazio).
     * @param genres        A lista de {@link Genre}s da série (não nula).
     * @param yearRelease   O ano de lançamento da primeira temporada.
     * @param originalTitle O título original (pode ser nulo).
     * @param whereWatch    Lista de strings de onde assistir.
     * @param yearEnd       O ano de encerramento (0 se não aplicável, deve ser >= yearRelease se > 0).
     */
    public void createShow(List<String> cast, boolean seen, String title, List<Genre> genres, int yearRelease, String originalTitle, List<String> whereWatch, int yearEnd){
        try {
            Show show = new Show (cast, seen, title, genres, yearRelease, originalTitle, whereWatch, yearEnd);
            showLibrary.add(show);
            media.add(show);
            saveData(); // Save after creation
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao criar série: " + e.getMessage());
        }
    }


    /**
     * Cria uma nova {@link Season} para uma série existente, identificada pelo título.
     * Verifica primeiro se a série existe na `showLibrary`. Em seguida, verifica se uma
     * temporada com o `seasonNumber` fornecido já existe para essa série. Se não existir,
     * cria a nova temporada e a adiciona à lista de temporadas do objeto {@link Show} correspondente.
     *
     * @param title        O título da série à qual adicionar a temporada (case-insensitive).
     * @param seasonNumber O número da nova temporada (deve ser > 0).
     * @param episodeCount O número de episódios da nova temporada (deve ser > 0).
     * @param releaseDate  A data de lançamento da nova temporada (String formatada, não nula).
     * @return Código de status inteiro:
     *         <ul>
     *           <li>0: Sucesso - Temporada criada e adicionada à série.</li>
     *           <li>1: Erro - Série com o título especificado não encontrada.</li>
     *           <li>2: Erro - Série não marcada como vista (se relevante para adicionar temporada).</li>
     *           <li>4: Erro - Uma temporada com este número já existe para esta série.</li>
     *           <li>98: Erro - Dados fornecidos para a temporada (número, episódios, data) são inválidos.</li>
     *         </ul>
     */
    public int createSeason(String title, int seasonNumber, int episodeCount, String releaseDate) {
        for (Show show : showLibrary) {
            if (show.getTitle() != null && show.getTitle().equalsIgnoreCase(title)) {
                // if (show.isSeen()) { // This check might not be relevant for adding a season
                // Check if season already exists
                boolean seasonExists = show.getSeasons().stream()
                        .anyMatch(s -> s.getSeasonNumber() == seasonNumber);
                if (seasonExists) {
                    return 4; // Season already exists
                }
                try {
                    Season newSeason = new Season(seasonNumber, episodeCount, releaseDate);
                    show.addSeason(newSeason);
                    saveData(); // Save after adding season
                    return 0; // Success
                } catch (IllegalArgumentException e) {
                    System.err.println("Erro ao criar temporada: " + e.getMessage());
                    return 98; // Invalid season data
                }
                // } else {
                //     return 2; // Show not marked as seen (if this is a requirement)
                // }
            }
        }
        return 1; // Show not found
    }

    // AUXILIARY METHOD OF "createSeason": SELECT THE CORRESPONDING SHOW IN CLASS CREATE
    /**
     * Retorna uma lista contendo apenas os títulos de todas mobilization séries presentes
     * na `showLibrary`. A lista é ordenada alfabeticamente (ignorando case).
     * Útil para a View apresentar opções de seleção de séries.
     *
     * @return Uma {@code List<String>} com os títulos das séries, ordenada.
     *         Retorna lista vazia se não houver séries.
     */
    public List<String> getShowName() {
        return showLibrary.stream()
                .map(Show::getTitle)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    /**
     * Cria uma nova {@link Review} para uma temporada específica de uma série.
     * Primeiro, localiza a série pelo `showTitle` (case-insensitive). Em seguida, localiza
     * a temporada específica dentro dessa série pelo `seasonNumber`. Se ambos forem encontrados,
     * cria a review e a adiciona à lista de reviews da temporada encontrada e à lista geral `reviewLibrary`.
     *
     * @param showTitle    O título da série (case-insensitive).
     * @param seasonNumber O número da temporada a ser avaliada.
     * @param comment      O texto da avaliação (não nulo/vazio).
     * @param stars        A nota atribuída (deve estar entre 1 e 5).
     * @param reviewDate   A data da avaliação (String formatada, não nula).
     * @return Código de status inteiro:
     *         <ul>
     *           <li>0: Sucesso - Review criada e adicionada à temporada.</li>
     *           <li>1: Erro - Série com o título especificado não encontrada.</li>
     *           <li>3: Erro - Temporada com o número especificado não encontrada para esta série.</li>
     *           <li>99: Erro - Dados fornecidos para a review são inválidos.</li>
     *         </ul>
     */
    public int createReviewShow(String showTitle, int seasonNumber, String comment, int stars, String reviewDate) {
        for (Show show : showLibrary) {
            if (show.getTitle().equalsIgnoreCase(showTitle)) {
                // Find the specific season object to add the review to
                Season targetSeason = null;
                for (Season season : show.getSeasons()) { // Iterate over the modifiable list from show
                    if (season.getSeasonNumber() == seasonNumber) {
                        targetSeason = season;
                        break;
                    }
                }

                if (targetSeason != null) {
                    try {
                        Review newReview = new Review(comment, stars, reviewDate);
                        targetSeason.addReview(newReview); // Add review to the actual season object
                        reviewLibrary.add(newReview); // Keep this for now
                        saveData(); // Save after adding review
                        return 0; // Review created successfully
                    } catch (IllegalArgumentException e) {
                        System.err.println("Erro ao criar review: " + e.getMessage());
                        return 99; // Invalid review data
                    }
                } else {
                    return 3; // Season not found
                }
            }
        }
        return 1; // Show not found
    }

    // AUXILIARY METHOD OF "getSeasonsByShowName": SELECT THE CORRESPONDING SEASON IN CLASS CREATE REVIEW
    /**
     * Retorna uma lista ordenada contendo os números de todas as temporadas existentes
     * para uma série específica, identificada pelo título.
     * Útil para a View permitir ao usuário selecionar uma temporada existente.
     *
     * @param showTitle O título da série (case-insensitive) cujos números de temporada são desejados.
     * @return Uma {@code List<Integer>} ordenada com os números das temporadas.
     *         Retorna uma lista vazia se a série não for encontrada ou não tiver temporadas.
     */
    public List<Integer> getSeasonsByShowName(String showTitle) {
        return showLibrary.stream()
                .filter(show -> show.getTitle().equalsIgnoreCase(showTitle))
                .findFirst()
                .map(show -> show.getSeasons().stream()
                        .map(Season::getSeasonNumber)
                        .sorted()
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    /**
     * Imprime no console informações detalhadas sobre todas as séries cadastradas
     * na `showLibrary`, incluindo detalhes de cada uma de suas temporadas e as
     * {@link Review}s associadas a cada temporada.
     */
    public void getShow() { // For debugging/testing primarily
        if (showLibrary.isEmpty()) {
            System.out.println("Nenhuma série cadastrada.");
            return;
        }
        for (Show show : showLibrary) {
            System.out.println(show.toString()); // Show's toString should handle season and review details
        }
    }


    // METHODS RELATED TO SEARCH =============================================================================================================================

    /**
     * Busca mídias na lista agregada `media` cujo título contenha a {@code String} fornecida,
     * ignorando diferenças de maiúsculas/minúsculas.
     *
     * @param title O termo de busca para o título. A busca é cancelada se for nulo ou vazio.
     * @return Uma {@code List<Media>} contendo todas as mídias que correspondem ao critério.
     *         Retorna uma lista vazia se nenhum item for encontrado ou se `title` for inválido.
     */
    public List<Media> searchByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String searchTerm = title.trim().toLowerCase();
        return media.stream()
                .filter(m -> m.getTitle().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Busca mídias na lista agregada `media` cujo ano de lançamento (`yearRelease`)
     * seja exatamente igual ao ano fornecido.
     *
     * @param year O ano de lançamento inteiro a ser buscado.
     * @return Uma {@code List<Media>} contendo todas as mídias lançadas no ano especificado.
     *         Retorna uma lista vazia se nenhum item for encontrado.
     */
    public List<Media> searchByYear(int year) {
        return media.stream()
                .filter(m -> m.getYearRelease() == year)
                .collect(Collectors.toList());
    }


    /**
     * Busca mídias na lista agregada `media` que possuam pelo menos um {@link Genre}
     * cujo nome contenha a {@code String} fornecida, ignorando maiúsculas/minúsculas.
     *
     * @param genreName O nome (ou parte do nome) do gênero a ser buscado. A busca é
     *                  cancelada se for nulo ou vazio.
     * @return Uma {@code List<Media>} contendo todas as mídias que possuem um gênero
     *         correspondente. Retorna lista vazia se nada for encontrado ou se
     *         `genreName` for inválido.
     */
    public List<Media> searchByGenre(String genreName) {
        if (genreName == null || genreName.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String searchNameLower = genreName.trim().toLowerCase();

        return this.media.stream()
                .filter(m -> {
                    List<Genre> genresDaMedia = m.getGenres(); // This is already an unmodifiable list
                    if (genresDaMedia.isEmpty()) { // No need to check for null if constructor guarantees non-null
                        return false;
                    }
                    return genresDaMedia.stream()
                            //.filter(Objects::nonNull) // Genre objects in list should not be null
                            .anyMatch(g -> g.getGenre().toLowerCase().contains(searchNameLower));
                })
                .collect(Collectors.toList());
    }

    /**
     * Busca livros na `bookLibrary` cujo nome do autor contenha a {@code String} fornecida,
     * ignorando maiúsculas/minúsculas.
     *
     * @param author O nome (ou parte do nome) do autor a ser buscado. Busca cancelada se nulo/vazio.
     * @return Uma {@code List<Book>} contendo os livros correspondentes. Lista vazia se nada encontrado.
     */
    public List<Book> searchBooksByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String searchTerm = author.trim().toLowerCase();
        return bookLibrary.stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Busca livros na `bookLibrary` cujo ISBN seja exatamente igual à {@code String} fornecida,
     * ignorando maiúsculas/minúsculas.
     *
     * @param isbn O ISBN exato a ser buscado. Busca cancelada se nulo/vazio.
     * @return Uma {@code List<Book>} contendo o livro correspondente (geralmente 0 ou 1 item).
     */
    public List<Book> searchBooksByISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String searchTerm = isbn.trim();
        return bookLibrary.stream()
                .filter(b -> b.getIsbn().equalsIgnoreCase(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Busca filmes na `filmLibrary` cujo nome do(s) diretor(es) contenha a {@code String} fornecida,
     * ignorando maiúsculas/minúsculas.
     *
     * @param director O nome (ou parte do nome) do diretor. Busca cancelada se nulo/vazio.
     * @return Uma {@code List<Films>} contendo os filmes correspondentes.
     */
    public List<Films> searchFilmsByDirector(String director) {
        if (director == null || director.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String searchTerm = director.trim().toLowerCase();
        return filmLibrary.stream()
                .filter(f -> f.getDirection().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Busca filmes na `filmLibrary` cuja lista de elenco (`cast`) contenha pelo menos um nome
     * que contenha a {@code String} fornecida, ignorando maiúsculas/minúsculas.
     *
     * @param actor O nome (ou parte do nome) do ator/atriz a ser buscado no elenco.
     *              Busca cancelada se nulo/vazio.
     * @return Uma {@code List<Films>} contendo os filmes que têm a pessoa no elenco.
     */
    public List<Films> searchFilmsByCast(String actor) {
        if (actor == null || actor.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String searchTerm = actor.trim().toLowerCase();
        return filmLibrary.stream()
                .filter(f -> f.getCast().stream().anyMatch(a -> a.toLowerCase().contains(searchTerm)))
                .collect(Collectors.toList());
    }


    /**
     * Busca séries na `showLibrary` cuja lista de elenco (`cast`) contenha pelo menos um nome
     * que contenha a {@code String} fornecida, ignorando maiúsculas/minúsculas.
     *
     * @param actor O nome (ou parte do nome) do ator/atriz a ser buscado no elenco.
     *              Busca cancelada se nulo/vazio.
     * @return Uma {@code List<Show>} contendo as séries que têm a pessoa no elenco.
     */
    public List<Show> searchShowsByCast(String actor) {
        if (actor == null || actor.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String searchTerm = actor.trim().toLowerCase();
        return showLibrary.stream()
                .filter(s -> s.getCast().stream().anyMatch(a -> a.toLowerCase().contains(searchTerm)))
                .collect(Collectors.toList());
    }

    /**
     * Retorna a lista agregada `media` completa, ordenada alfabeticamente pelo título
     * (A-Z, ignorando maiúsculas/minúsculas). Filtra itens nulos por segurança.
     *
     * @return Uma nova {@code List<Media>} contendo todos os objetos Media cadastrados,
     *         ordenados por título.
     */
    public List<Media> listMediaAlphabetically() {
        return media.stream()
                // .filter(Objects::nonNull) // Should not be necessary if media list is well-managed
                .sorted(Comparator.comparing(Media::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /**
     * Calcula a avaliação "representativa" de uma mídia para fins de ordenação e exibição.
     * <p>
     * - Para {@link Book} e {@link Films}: Retorna a nota da <b>última</b> review adicionada (ou 0.0f se não houver reviews).
     * - Para {@link Show}: Retorna a <b>média</b> das notas de todas as reviews de todas as suas temporadas (ou 0.0f se não houver reviews/temporadas).
     * - Para outros tipos ou {@code null}: Retorna 0.0f.
     * </p>
     * Este método é estático pois seu cálculo depende apenas do objeto {@link Media} passado como argumento.
     *
     * @param m O objeto {@link Media} cuja avaliação deve ser calculada.
     * @return A nota representativa (última ou média) como float, ou 0.0f.
     */
    public static float calculateAverage(Media m) {
        if (m == null) {
            return 0f;
        }

        if (m instanceof Book || m instanceof Films) {
            List<Review> reviews = m.getReviews(); // This is an unmodifiable list
            if (reviews.isEmpty()) { // No need to check for null
                return 0f;
            }
            // To get the truly last added, we'd need the original modifiable list or a timestamp.
            // Assuming the unmodifiable list maintains insertion order.
            Review lastReview = reviews.get(reviews.size() - 1);
            return lastReview.getStars(); // Review constructor ensures stars is valid
        }

        else if (m instanceof Show) {
            Show show = (Show) m;
            List<Season> seasons = show.getSeasons(); // This is an unmodifiable list
            if (seasons.isEmpty()) {
                return 0f;
            }

            float totalStarsSum = 0;
            int totalReviewCount = 0;

            for (Season season : seasons) {
                // season objects themselves should be non-null if list management is correct
                List<Review> seasonReviews = season.getReviews(); // Unmodifiable
                if (!seasonReviews.isEmpty()) {
                    for (Review review : seasonReviews) {
                        // review objects should be non-null
                        totalStarsSum += review.getStars();
                        totalReviewCount++;
                    }
                }
            }

            if (totalReviewCount == 0) {
                return 0f;
            }
            return totalStarsSum / totalReviewCount;
        }
        else { // Should not happen with current class hierarchy
            return 0f;
        }
    }

    /**
     * Filtra a lista agregada de mídias ({@code media}) com base em um ano e/ou gênero opcional,
     * e então ordena a lista resultante de acordo com a opção de ordenação fornecida.
     *
     * @param filterYear  O ano para filtrar (ou {@code null} para não filtrar por ano).
     * @param filterGenre O {@link Genre} para filtrar (ou {@code null} para não filtrar por gênero).
     * @param sortOption  O critério de ordenação:
     *                    <ul>
     *                      <li>1: Avaliação Decrescente (maior para menor)</li>
     *                      <li>2: Avaliação Crescente (menor para maior)</li>
     *                      <li>3 ou outro: Alfabético por Título (A-Z, case-insensitive)</li>
     *                    </ul>
     * @return Uma nova {@code List<Media>} contendo as mídias filtradas e ordenadas.
     */
    public List<Media> getFilteredAndSortedMedia(Integer filterYear, Genre filterGenre, int sortOption) {
        List<Media> filteredList = this.media.stream()
                .filter(m -> filterYear == null || m.getYearRelease() == filterYear)
                .filter(m -> filterGenre == null || m.getGenres().stream() // m.getGenres() is unmodifiable
                        .anyMatch(g -> g.equals(filterGenre))) // Use .equals for Genre comparison
                .collect(Collectors.toList());

        Comparator<Media> comparator;
        switch (sortOption) {
            case 1: // Highest to lowest rating
                comparator = Comparator.comparingDouble((Media m) -> calculateAverage(m)).reversed();
                break;
            case 2: // Lowest to highest rating
                comparator = Comparator.comparingDouble((Media m) -> calculateAverage(m));
                break;
            case 3: // Alphabetical (A-Z)
            default:
                comparator = Comparator.comparing(Media::getTitle, String.CASE_INSENSITIVE_ORDER);
                break;
        }
        filteredList.sort(comparator);
        return filteredList;
    }

}