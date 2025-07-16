package View;


import Control.WorkManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Classe da camada View responsável por gerenciar a interface com o usuário
 * para a criação e adição de novas avaliações (reviews) para mídias culturais
 * existentes (Livros, Filmes, Temporadas de Séries).
 * Coleta os dados da review (comentário, nota) e o identificador da mídia/temporada,
 * e interage com o {@link WorkManager} para registrar a review.
 * Esta classe gerencia seu próprio {@link Scanner}.
 */
public class CreateReview {
    Scanner scanner = new Scanner(System.in);
    final WorkManager workManager;
    private Screen screen;

    /**
     * Construtor da classe CreateReview.
     * Recebe a instância do WorkManager necessária para interagir com os dados.
     *
     * @param workManager A instância do {@link WorkManager} para acessar
     *                    listas de mídias, temporadas e adicionar reviews. Não pode ser nulo.
     */
    public CreateReview(WorkManager workManager) {
        this.workManager = workManager;
     }

    /**
     * Gera a data atual e a formata como uma {@code String} no padrão "dd/MM/yyyy".
     * Usado para registrar a data em que uma review é criada.
     *
     * @return A data atual formatada como "dd/MM/yyyy".
     */
    public String dateNow() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = date.format(formatter);
        return formattedDate;
    }

    /**
     * Exibe uma mensagem para o usuário indicando o resultado da tentativa de criar uma review,
     * com base no código de status retornado pelo {@link WorkManager}.
     * Pausa a execução após exibir a mensagem.
     *
     * @param result O código de status inteiro:
     *               <ul>
     *                 <li>0: Sucesso</li>
     *                 <li>1: Mídia (Livro/Filme/Série) não encontrada</li>
     *                 <li>2: Mídia não marcada como vista/lida</li>
     *                 <li>3: Temporada não encontrada (para reviews de séries)</li>
     *                 <li>Outro: Erro inesperado ou dados da review inválidos</li>
     *               </ul>
     */
    public void messager(int result) {
        if (result == 0) {
            System.out.println("Review created successfully!");
        } else if (result == 1) {
            System.out.println("Book not found.");
        } else if (result == 2) {
            System.out.println("You haven't read this book yet.");
        } else {
            System.out.println("Error creating the review.");
        }
    }

    /**
     * Exibe e gerencia o menu principal para a criação de reviews.
     * Permite ao usuário escolher o tipo de mídia (Livro, Filme, Série/Temporada)
     * que deseja avaliar, ou retornar ao menu principal da aplicação.
     * O loop continua até o usuário escolher a opção de retornar.
     */
    public void showCreateReview() {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
        System.out.println("<----------------------------->");
        System.out.println("Choose the type of media:");
        System.out.println("1. Book");
        System.out.println("2. Film");
        System.out.println("3. Series/Season");
        System.out.println("4. Return to the previous menu");

        option = scanner.nextInt();

        switch (option) {
            case 1:
                createReviewBookData();
                break;
            case 2:
                createReviewFilmsData();
                break;
            case 3:
                createReviewShowSerieData();
                break;
            case 4:
                ClearScreen.clear();
                System.out.println("🔙 Returning to previous menu...");
                break;
            default:
                System.out.println("Invalid option.");
        }
        }
        while (option != 4);
    }

    /**
     * Gerencia o processo de coleta de dados para criar uma review para um Livro.
     * Solicita ao usuário que selecione o livro, insira o comentário e a nota (estrelas).
     * Chama o {@link WorkManager#createReviewBook} para registrar a review.
     */
    public void createReviewBookData() {

        System.out.println("<----------------------------->");
        System.out.println("Which book would you like to review?");
        String title = selectBookFromLibrary();

        System.out.println("<----------------------------->");
        System.out.println("Write your review about the book:");
        String comment = scanner.nextLine();

        System.out.println("<----------------------------->");
        System.out.println("Rate the book (1 to 5 stars):");
        int stars = Integer.parseInt(scanner.nextLine());

        String reviewDate = dateNow();

        int result = workManager.createReviewBook(title, comment, stars, reviewDate);
        messager(result);
    }

    /**
     * Apresenta ao usuário a lista de livros disponíveis (obtida do {@link WorkManager})
     * e permite que ele selecione um livro pelo número correspondente.
     * Inclui opção para cancelar.
     *
     * @param prompt A mensagem a ser exibida antes da lista de livros.
     * @return O título do livro selecionado como {@code String}, ou {@code null} se não houver
     *         livros ou se o usuário cancelar a seleção.
     */
    public String selectBookFromLibrary() {
        System.out.println("<----------------------------->");
        System.out.println("Enter the title of the book you want to select:");

        List<String> books = workManager.getBooksName();
        for (int i = 0; i < books.size(); i++) {
            System.out.println((i + 1) + " - " + books.get(i));
        }

        int nameBookIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
        return books.get(nameBookIndex);
    }

    /**
     * Gerencia o processo de coleta de dados para criar uma review para um Filme.
     * Solicita ao usuário que selecione o filme, insira o comentário e a nota (estrelas).
     * Chama o {@link WorkManager#createReviewFilm} para registrar a review.
     */
    public void createReviewFilmsData() {


        System.out.println("<----------------------------->");
        System.out.println("Which movie would you like to review?");
        String title = selectFilmFromLibrary();

        System.out.println("<----------------------------->");
        System.out.println("Write your review about the movie:");
        String comment = scanner.nextLine();

        System.out.println("<----------------------------->");
        System.out.println("Enter your rating (stars) between 1 and 5:");
        int stars = Integer.parseInt(scanner.nextLine());

        String reviewDate = dateNow();

        int result = workManager.createReviewFilm(title,comment, stars, reviewDate);
        messager(result);
    }

    /**
     * Apresenta ao usuário a lista de filmes disponíveis (obtida do {@link WorkManager})
     * e permite que ele selecione um filme pelo número correspondente.
     * Inclui opção para cancelar.
     *
     * @param prompt A mensagem a ser exibida antes da lista de filmes.
     * @return O título do filme selecionado como {@code String}, ou {@code null} se não houver
     *         filmes ou se o usuário cancelar a seleção.
     */
    public String selectFilmFromLibrary() {
        System.out.println("<----------------------------->");
        System.out.println("Enter the title of the movie you want to select:");

        List<String> film = workManager.getFilmName();
        for (int i = 0; i < film.size(); i++) {
            System.out.println((i + 1) + " - " + film.get(i));
        }

        int nameBookIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
        return film.get(nameBookIndex);
    }

    /**
     * Gerencia o processo de coleta de dados para criar uma review para uma Temporada específica de uma Série.
     * Solicita ao usuário que selecione a série, depois a temporada, insira o comentário e a nota.
     * Chama o {@link WorkManager#createReviewShow} para registrar a review na temporada correta.
     */
    public void createReviewShowSerieData() {

        System.out.println("<----------------------------->");
        System.out.println("Which series would you like to review?");
        String title = selectShowFromLibrary();

        System.out.println("<----------------------------->");
        System.out.println("Which season would you like to review?");
        int seasonNumber = selectSeasonFromLibrary(title);

        System.out.println("<----------------------------->");
        System.out.println("Write your review about the series:");
        String comment = scanner.nextLine();

        System.out.println("<----------------------------->");
        System.out.println("Enter your rating (stars) from 1 to 5:");
        int stars = Integer.parseInt(scanner.nextLine());

        String reviewDate = dateNow();

        int result = workManager.createReviewShow(title,seasonNumber,comment, stars, reviewDate);
        messager(result);
        workManager.getShow();
    }

    /**
     * Apresenta ao usuário a lista de séries disponíveis (obtida do {@link WorkManager})
     * e permite que ele selecione uma série pelo número correspondente.
     * Inclui opção para cancelar.
     *
     * @param prompt A mensagem a ser exibida antes da lista de séries.
     * @return O título da série selecionada como {@code String}, ou {@code null} se não houver
     *         séries ou se o usuário cancelar a seleção.
     */
    public String selectShowFromLibrary() {
        System.out.println("<----------------------------->");
        System.out.println("Enter the title of the series you want to select:");

        List<String> film = workManager.getShowName();
        for (int i = 0; i < film.size(); i++) {
            System.out.println((i + 1) + " - " + film.get(i));
        }

        int nameBookIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
        return film.get(nameBookIndex);
    }

    /**
     * Apresenta ao usuário a lista de números de temporadas disponíveis para uma série específica
     * (obtida do {@link WorkManager}) e permite que ele selecione uma temporada pelo número de ordem na lista.
     * Inclui opção para cancelar.
     *
     * @param showTitle O título da série cujas temporadas devem ser listadas.
     * @param prompt    A mensagem a ser exibida antes da lista de temporadas.
     * @return O número inteiro da temporada selecionada, ou -1 se não houver temporadas,
     *         se o usuário cancelar, ou se ocorrer um erro na seleção.
     */
    public int selectSeasonFromLibrary(String title) {
        System.out.println("<----------------------------->");
        System.out.println("Enter the season number you want to select:");

        List<Integer> seasons = workManager.getSeasonsByShowName(title);
        for (int i = 0; i < seasons.size(); i++) {
            System.out.println((i + 1) + " - " + seasons.get(i));
        }

        int nameSeasonIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
        return seasons.get(nameSeasonIndex);
    }


}