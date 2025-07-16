package View;

import Control.WorkManager;
import Module.*;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Classe da camada View responsável por gerenciar a interface com o usuário
 * relacionada à busca e listagem de mídias culturais na biblioteca.
 * Interage com o {@link WorkManager} para obter os dados filtrados e/ou ordenados
 * e os formata para exibição tabular no console.
 * Utiliza um {@link Scanner} compartilhado, passado pelo construtor.
 */
public class Search {

    final WorkManager workManager;
    private Scanner scanner;

    /**
     * Construtor da classe Search.
     * Recebe instâncias do WorkManager e do Scanner compartilhado.
     *
     * @param workManager A instância do {@link WorkManager} para acesso aos dados da aplicação.
     *                    Não pode ser nulo.
     * @param scanner     A instância do {@link Scanner} compartilhada, usada para
     *                    ler a entrada do usuário nos menus e prompts de busca/listagem.
     *                    Não pode ser nulo.
     */
    public Search(WorkManager workManager) {
        this.workManager = workManager;
        this.scanner = new Scanner(System.in);
    }


    /**
     * Exibe e gerencia o menu principal de busca específica.
     * Permite ao usuário escolher um critério (Título, Gênero, Ano, Pessoa, ISBN)
     * para buscar mídias na biblioteca. Chama os métodos auxiliares correspondentes
     * para realizar a busca e exibir os resultados.
     * O loop continua até o usuário escolher retornar ao menu anterior (Search/List Menu).
     */
    public void mediaSearchMenu() {
        int option;

        do {
            System.out.println("<------------------------------->");
            System.out.println("Select a search option:");

            System.out.println("\n<--- Specific Search Menu --->");
            System.out.println("Search by which criteria?");
            System.out.println("0 - Return to Previous Menu");
            System.out.println("1 - Title");
            System.out.println("2 - Genre");
            System.out.println("3 - Release Year");
            System.out.println("4 - Person (Author, Director, Cast)");
            System.out.println("5 - ISBN (Books Only)");
            System.out.println("<--------------------------------->");
            System.out.print("Option: ");
            System.out.println("<------------------------------->");
            option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1: // Title
                    searchByTitleOption();
                    break;
                case 2: // Genres
                    searchByGenreOption();
                    break;
                case 3: // Year
                    searchByYearOption();
                    break;
                case 4: // People
                    searchByPersonOption();
                    break;
                case 5: // ISBN
                    searchByIsbnOption();
                    break;
                case 0:
                    System.out.println("🔙 Returning to previous menu...");
                    ClearScreen.clear();
                    break;

                default:
                    System.out.println("❌ Invalid option.");
            }

        } while (option != 0);

    }

    /**
     * Método auxiliar privado que lida com a busca por título.
     * Solicita o termo de busca ao usuário, chama o {@link WorkManager#searchByTitle},
     * e exibe os resultados encontrados em formato de tabela usando {@link #printMediaTable}.
     */
    private void searchByTitleOption() {
        System.out.print("Enter the title: ");
        String title = scanner.nextLine();
        System.out.println("\nSearching by title: '" + title + "'...");
        List<Media> results = workManager.searchByTitle(title);
        printMediaTable(results);
        pauseForUser();
    }

    /**
     * Método auxiliar privado que lida com a busca por gênero.
     * Permite ao usuário selecionar um gênero da lista disponível (usando {@link #selectSingleGenreFilter}),
     * chama o {@link WorkManager#searchByGenre} com o nome do gênero selecionado,
     * e exibe os resultados encontrados em formato de tabela.
     */
    private void searchByGenreOption() {
        System.out.print("Enter the genre name: ");
        String genreName = scanner.nextLine();
        System.out.println("\nSearching by genre: '" + genreName + "'...");
        List<Media> results = workManager.searchByGenre(genreName);
        printMediaTable(results);
        pauseForUser();
    }

    /**
     * Método auxiliar privado que lida com a busca por ano de lançamento.
     * Solicita o ano ao usuário, chama o {@link WorkManager#searchByYear},
     * e exibe os resultados em formato de tabela.
     */
    private void searchByYearOption() {
        System.out.print("Enter the release year: ");
        int year;
        try {
            year = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid year. Search cancelled.");
            return;
        }
        System.out.println("\nSearching by year: " + year + "...");
        List<Media> results = workManager.searchByYear(year);
        printMediaTable(results);
        pauseForUser();
    }

    /**
     * Método auxiliar privado que lida com a busca por pessoa (Autor, Diretor, Elenco).
     * Solicita o nome da pessoa e o escopo da busca (tipo de mídia/papel).
     * Chama os métodos de busca apropriados no {@link WorkManager}, combina os resultados,
     * remove duplicatas e exibe a lista final em formato de tabela.
     */
    private void searchByPersonOption() {
        System.out.print("Enter the person's name (Author, Director, Actor/Actress): ");
        String personName = scanner.nextLine();

        System.out.println("Search for '" + personName + "' in which category(s)?");
        System.out.println("1 - Books only (Author)");
        System.out.println("2 - Movies only (Director/Cast)");
        System.out.println("3 - Series only (Cast)");
        System.out.println("4 - All");
        System.out.print("Option: ");

        int scopeOption = readIntInput();

        System.out.println("\nSearching by person: '" + personName + "'...");
        List<Media> combinedResults = new java.util.ArrayList<>();

        if (scopeOption == 1 || scopeOption == 4) {
            combinedResults.addAll(workManager.searchBooksByAuthor(personName));
        }
        if (scopeOption == 2 || scopeOption == 4) {
            combinedResults.addAll(workManager.searchFilmsByDirector(personName));
            combinedResults.addAll(workManager.searchFilmsByCast(personName));
        }
        if (scopeOption == 3 || scopeOption == 4) {
            combinedResults.addAll(workManager.searchShowsByCast(personName));
        }
        if (scopeOption < 1 || scopeOption > 4) {
            System.out.println("Invalid scope option. No search by person performed.");
        }

        List<Media> uniqueResults = combinedResults.stream()
                .filter(distinctByKey(Media::getTitle))
                .collect(Collectors.toList());

        printMediaTable(uniqueResults);
        pauseForUser();
    }

    // Helper para distinct em Streams (requer import java.util.function.Function e java.util.concurrent.ConcurrentHashMap)
    /**
     * Predicado auxiliar estático que pode ser usado com {@code Stream.filter()}
     * para remover elementos duplicados com base em uma chave extraída por uma função.
     * Utiliza um {@link java.util.concurrent.ConcurrentHashMap} internamente para
     * rastrear as chaves já vistas de forma eficiente e thread-safe (embora a thread-safety
     * não seja estritamente necessária neste contexto single-threaded).
     *
     * @param <T> O tipo dos elementos no Stream.
     * @param keyExtractor Uma {@link java.util.function.Function} que, dado um elemento `T`,
     *                     extrai a chave ({@code Object}) usada para determinar a unicidade.
     * @return Um {@link java.util.function.Predicate} que retorna `true` para o primeiro
     *         elemento encontrado com uma chave específica, e `false` para os subsequentes.
     */
    public static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
        java.util.Set<Object> seen = java.util.concurrent.ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Método auxiliar privado que lida com a busca de livros por ISBN.
     * Solicita o ISBN ao usuário, chama o {@link WorkManager#searchBooksByISBN},
     * e exibe o resultado (geralmente um único livro ou nenhum) em formato de tabela.
     */
    private void searchByIsbnOption() {
        System.out.print("Enter the book's ISBN: ");
        String isbn = scanner.nextLine();
        if (isbn.trim().isEmpty()) {
            System.out.println("Search by ISBN cancelled (empty input).");
            return;
        }
        System.out.println("\nSearching by ISBN: '" + isbn + "'...");
        List<? extends Media> results = workManager.searchBooksByISBN(isbn);
        printMediaTable((List<Media>) results);
        pauseForUser();
    }


    /**
     * Exibe e gerencia o menu para listagem de mídias.
     * Permite ao usuário escolher entre uma listagem simples (alfabética) ou
     * uma listagem avançada com opções de filtro e ordenação.
     * O loop continua até o usuário escolher retornar ao menu anterior (Search/List Menu).
     */
    public void mediaListMenu() {
        Scanner scanner = new Scanner(System.in);
        int option;
        do {
            System.out.println("\n<------------------------------->");
            System.out.println("List all media - choose criteria:");
            System.out.println("1 - Alphabetical (A–Z)");
            System.out.println("2 - Forting and Filtering");
            System.out.println("3 - Return to the previous menu");
            System.out.print("Option: ");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> {
                    ClearScreen.clear();
                    List<Media> list = workManager.listMediaAlphabetically();
                    printDetailedMediaList(list, "Lista Completa em Ordem Alfabética");
                    printMediaTable(list);
                }
                case 2 -> {
                    ClearScreen.clear();
                    sortingAndFilter();
                }
                case 3 -> {
                    ClearScreen.clear();
                    System.out.println("🔙 Returning to previous menu...");
                }
            }
        } while (option != 3);
    }

    /**
     * Gerencia a coleta de opções de filtragem (por ano, por gênero) e
     * ordenação (por avaliação, alfabética) do usuário para a listagem avançada.
     * Chama o {@link WorkManager#getFilteredAndSortedMedia} com as opções selecionadas
     * e exibe a lista resultante formatada em tabela usando {@link #printMediaTable}.
     */
    public void sortingAndFilter() {


        Scanner scanner = new Scanner(System.in);
        int optionYear, sortOption;
        String optionGenre;
        Integer filterYear = null;
        Genre filterGenre = null;

        System.out.println("Would you like to filter the list by release year?");
        System.out.println("[1] - YES");
        System.out.println("[2] - NO");
        System.out.print("Option: ");
        optionYear = scanner.nextInt();
        scanner.nextLine();

        if (optionYear == 1) {
            System.out.println("\nWhich year would you like to search for?");
            filterYear = scanner.nextInt();
            scanner.nextLine();
        }

        System.out.println("\nWould you like to filter the list by genre?");
        System.out.println("[1] - YES");
        System.out.println("[2] - NO");
        System.out.print("Option: ");
        int optionGenreInt = scanner.nextInt();
        scanner.nextLine();

        if (optionGenreInt == 1) {
            filterGenre = selectSingleGenreFilter();
            if (filterGenre != null) {
                System.out.println(">> Filtering by genre:" + filterGenre.getGenre());
            } else {
                System.out.println(">> No valid genre selected/found. Genre filter will not be applied.");
            }
        }

        System.out.println("\nWhich sorting would you like?");
        System.out.println("[1] - From highest rated to lowest rated.");
        System.out.println("[2] - From lowest rated to highest rated.");
        System.out.println("[3] - Alphabetical (A-Z)");
        System.out.print("Option: ");
        sortOption = scanner.nextInt();
        scanner.nextLine();

        ClearScreen.clear();
        List<Media> resultList = workManager.getFilteredAndSortedMedia(filterYear, filterGenre, sortOption);
        printMediaTable(resultList);
    }

    /**
     * Apresenta a lista de gêneros disponíveis e permite ao usuário selecionar um
     * para usar como filtro na listagem avançada ou na busca por gênero.
     * Inclui uma opção para não selecionar nenhum gênero (cancelar filtro/busca).
     *
     * @return O objeto {@link Genre} selecionado pelo usuário, ou {@code null} se o usuário
     *         escolher a opção "None / Cancel" ou se não houver gêneros disponíveis.
     */
    private Genre selectSingleGenreFilter() {
        Scanner scanner = new Scanner(System.in);
        ClearScreen.clear();
        System.out.println("Which genre would you like to search for?");
        System.out.print("Please enter the genre: \n");
        List<Genre> allGenres = workManager.getGenres();
        if (allGenres.isEmpty()) return null;
        System.out.println("\n0 - None");
        for (int i = 0; i < allGenres.size(); i++) {
            System.out.println((i + 1) + " - " + allGenres.get(i));
        }
        System.out.print("\nChoose the genre number (or 0):");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice > 0 && choice <= allGenres.size()) {
                return allGenres.get(choice - 1);
            }
        } catch (NumberFormatException e) { /* Ignora erro, retorna null */ }
        return null;
    }

    /**
     * Imprime uma lista de objetos {@link Media} em um formato de tabela formatado no console.
     * A tabela inclui colunas para Título, Tipo (Classe), Ano, Avaliação Média e Gêneros.
     * Trunca títulos e listas de gêneros que excedam a largura definida para as colunas.
     * Exibe uma mensagem se a lista estiver vazia ou nula.
     *
     * @param mediaList A {@code List<Media>} a ser impressa na tabela. Pode ser nula ou vazia.
     */
    private void printMediaTable(List<Media> mediaList) {
        if (mediaList.isEmpty()) {
            System.out.println("📭 No media found..");
            return;
        }
        System.out.printf("%-30s | %-10s | %-6s | %-10s| %-10s%n",
                "Title", "Type", "Year", "★ Rating", "Genre");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
        for (Media m : mediaList) {
            String title = truncate(m.getTitle(), 30);
            String type = m.getClass().getSimpleName();
            int year = m.getYearRelease();
            float avg = WorkManager.calculateAverage(m);

            String genresString;
            if (m.getGenres() == null || m.getGenres().isEmpty()) {
                genresString = "(N/A)";
            } else {
                genresString = m.getGenres().stream()
                        .filter(Objects::nonNull)
                        .map(Genre::toString)
                        .collect(Collectors.joining(", "));
            }
            System.out.printf("%-30s | %-10s | %-6d | %-10.1f | %-10s%n",
                    title, type, year, avg, genresString);
        }
    }


    /**
     * Trunca uma {@code String} para um comprimento máximo especificado, adicionando "..." no final
     * se a string original for mais longa que o limite. Lida com strings nulas retornando uma string vazia.
     *
     * @param s   A string a ser truncada. Pode ser nula.
     * @param len O comprimento máximo desejado para a string resultante (incluindo os "..." se aplicável).
     *            Deve ser pelo menos 3 para que a adição de "..." faça sentido.
     * @return A string original se for nula, vazia ou não exceder `len`. Caso contrário, retorna
     *         a string truncada para `len-3` caracteres seguida por "...".
     */
    private String truncate(String s, int len) {
        return s.length() > len ? s.substring(0, len - 3) + "..." : s;
    }

    /**
     * Imprime uma lista de mídias com detalhes completos e formatados no console.
     * Para cada mídia, exibe informações comuns (título, ano, gêneros, status, avaliação)
     * e detalhes específicos do tipo (autor/ISBN para Livro; diretor/duração para Filme;
     * período/temporadas/reviews de temporadas para Série).
     * Utiliza métodos auxiliares {@link #printReviewsForMedia} e {@link #printStringList}
     * para formatar partes da saída.
     * Exibe um título fornecido acima da lista e mensagens apropriadas se a lista for nula ou vazia.
     *
     * @param mediaList A {@code List<Media>} a ser impressa detalhadamente. Pode ser nula ou vazia.
     * @param titleForList Um {@code String} contendo o título a ser exibido antes da lista (ex: "Search Results").
     */
    private void printDetailedMediaList(List<Media> mediaList, String titleForList) {
        System.out.println("\n<=======================================================>");
        System.out.println("          " + titleForList);
        System.out.println("<=======================================================>");

        if (mediaList == null || mediaList.isEmpty()) {
            System.out.println("\n          ### No media found. ###");
            System.out.println("<=======================================================>");
            return;
        }

        int count = 1;
        for (Media m : mediaList) {
            System.out.println("\n--- Item #" + count++ + " -----------------------------------------");

            System.out.println("  Title: " + m.getTitle());

            if (m instanceof AudioVisualMedia) {
                AudioVisualMedia avm = (AudioVisualMedia) m;

                if (avm.getOriginalTitle() != null && !avm.getOriginalTitle().equalsIgnoreCase(m.getTitle())) {
                    System.out.println("  Original Title: " + avm.getOriginalTitle());
                }
            }
            System.out.println("  Release Year: " + m.getYearRelease());

            String genresString = "(None)";
            if (m.getGenres() != null && !m.getGenres().isEmpty()) {
                genresString = m.getGenres().stream()
                        .filter(Objects::nonNull)
                        .map(Genre::toString)
                        .collect(Collectors.joining(", "));
            }
            System.out.println("  Genre: " + genresString);
            System.out.println("  Seen/Read? " + (m.isSeen() ? "Yes" : "No"));
            System.out.printf("  Review: %.1f ★%n", WorkManager.calculateAverage(m));

            if (m instanceof Book) {
                Book book = (Book) m;
                System.out.println("  Type: Book");
                System.out.println("  Author: " + book.getAuthor());
                System.out.println("  Publisher: " + book.getPublisher());
                System.out.println("  ISBN: " + book.getIsbn());
                System.out.println("  Do you own a copy? " + (book.getCopy() ? "Sim" : "Não"));
                printReviewsForMedia(m.getReviews(), "  ");

            } else if (m instanceof Films) {
                Films film = (Films) m;
                System.out.println("  Type: Film");
                System.out.println("  Direction: " + film.getDirection());
                System.out.println("  Screenplay: " + film.getScreenplay());
                System.out.println("  Duration: " + film.getRunningtime() + " min");

                printStringList(film.getCast(), "  Cast: ");
                printStringList(film.getWhereWatch(), "  Where to Watch: ");
                printReviewsForMedia(m.getReviews(), "  ");

            } else if (m instanceof Show) {
                Show show = (Show) m;
                System.out.println("  Type: TV Show");

                if (show.getYearEnd() > 0 && show.getYearEnd() != show.getYearRelease()) {
                    System.out.println("  Period: " + show.getYearRelease() + " - " + show.getYearEnd());
                }

                printStringList(show.getCast(), "  Cast: ");
                printStringList(show.getWhereWatch(), "  Where to Watch: ");


                if (show.getSeasons() != null && !show.getSeasons().isEmpty()) {
                    System.out.println("  Seasons (" + show.getSeasons().size() + "):");

                    for (Season season : show.getSeasons()) {
                        if (season != null) {
                            System.out.println("    - Season " + season.getSeasonNumber() + ":");
                            System.out.println("      Episodes: " + season.getEpisodeCount());
                            System.out.println("      Release: " + season.getReleaseDate());
                            printReviewsForMedia(season.getReviews(), "      ");
                        } else {
                            System.out.println("    - (Error: null season found)");
                        }
                    }
                } else {
                    System.out.println("  Seasons: (No seasons registered)");
                }

            } else {

                System.out.println("  Type: Unknown Media");
            }

        }

        System.out.println("---------------------------------------------------");
    }

    /**
     * Método auxiliar privado para imprimir uma lista de {@link Review}s com um prefixo
     * de indentação especificado. Formata cada review usando seu método `toString()`.
     * Lida com listas nulas ou vazias e reviews nulas dentro da lista.
     *
     * @param reviews A {@code List<Review>} a ser impressa. Pode ser nula ou vazia.
     * @param prefix Uma {@code String} contendo os espaços ou caracteres a serem impressos
     *               antes de cada linha de review para indentação (ex: "  ", "      ").
     */
    private void printReviewsForMedia(List<Review> reviews, String prefix) {
        if (reviews != null && !reviews.isEmpty()) {
            System.out.println(prefix + "Reviews:");

            for (Review review : reviews) {
                if (review != null) {
                    System.out.println(prefix + "  * " + review);
                } else {
                    System.out.println(prefix + "  * (Error: null review found)");
                }
            }
        } else {
            System.out.println(prefix + "Reviews: (null or empty)");
        }
    }

    /**
     * Método auxiliar privado para formatar e imprimir uma lista de {@code String}s
     * (como elenco ou locais para assistir) como uma única linha, separada por vírgulas.
     * Exibe um rótulo antes da lista. Lida com listas nulas/vazias e strings vazias/nulas
     * dentro da lista, exibindo "(Not specified)" nesses casos.
     *
     * @param list A {@code List<String>} a ser formatada e impressa. Pode ser nula ou vazia.
     * @param label A {@code String} de rótulo a ser impressa antes da lista (ex: "Cast: ").
     */
    private void printStringList(List<String> list, String label) {
        String content = "(Not specified)";

        if (list != null && !list.isEmpty()) {

            content = list.stream()
                    .filter(s -> s != null && !s.trim().isEmpty())
                    .collect(Collectors.joining(", "));

            if (content.isEmpty()) {
                content = "(Not specified)";
            }
        }

        System.out.println(label + content);
    }

    /**
     * Lê um input inteiro do usuário de forma segura, tratando {@link InputMismatchException}.
     * Usa o {@link Scanner} compartilhado passado no construtor.
     *
     * @return O inteiro lido do usuário.
     */
    private int readIntInput() {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
                System.out.print("Option: ");
            }
        }
    }

    /**
     * Pausa a execução do programa e aguarda que o usuário pressione a tecla Enter
     * para continuar. Útil para permitir a leitura de resultados ou mensagens.
     * Usa o {@link Scanner} compartilhado.
     */
    private void pauseForUser() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }



}





