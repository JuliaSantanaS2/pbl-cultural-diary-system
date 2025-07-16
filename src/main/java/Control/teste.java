package Control;

import Module.Genre;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import Control.WorkManager;


public class teste {

    // Instância do WorkManager (ajuste conforme necessário para seu projeto)
    private WorkManager workManager = new WorkManager();

    // --- MÉTODOS AUXILIARES (COLOQUE-OS ONDE FOREM NECESSÁRIOS, EX: NO WorkManager OU EM UMA CLASSE UTILITÁRIA) ---
    // Certifique-se de que 'workManager' é uma instância acessível de Control.WorkManager.

    // Método auxiliar para obter um objeto Genre pelo nome.
    // Se o gênero não existir, ele tentará adicioná-lo.
    private Genre getGenre(String genreName) {
        return workManager.getGenres().stream()
                .filter(g -> g.getGenre().equalsIgnoreCase(genreName))
                .findFirst()
                .orElseGet(() -> {
                    workManager.addGenre(genreName); // Adiciona se não existe
                    return workManager.getGenres().stream()
                            .filter(g -> g.getGenre().equalsIgnoreCase(genreName))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Gênero '" + genreName + "' não pôde ser criado ou encontrado."));
                });
    }

    // Método auxiliar para obter a data atual formatada.
    private String dateNow() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void popularDados() {
        // --- 1. GÊNEROS (10 EXEMPLOS) ---
        // Adicione esses gêneros primeiro, pois as mídias dependem deles.
        workManager.addGenre("Ficção Científica");
        workManager.addGenre("Fantasia");
        workManager.addGenre("Drama");
        workManager.addGenre("Aventura");
        workManager.addGenre("Suspense");
        workManager.addGenre("Romance");
        workManager.addGenre("Ação");
        workManager.addGenre("Comédia");
        workManager.addGenre("Mistério");
        workManager.addGenre("Biografia");
        workManager.addGenre("Histórico");
        workManager.addGenre("Crime");

        // --- 2. LIVROS (10 EXEMPLOS) ---
        workManager.createBook(true, "Duna", Arrays.asList(getGenre("Ficção Científica"), getGenre("Aventura")), 1965, "Frank Herbert", "Editora Aleph", "978-8576570076", true);
        workManager.createBook(false, "O Nome do Vento", Arrays.asList(getGenre("Fantasia"), getGenre("Aventura")), 2007, "Patrick Rothfuss", "Arqueiro", "978-8580410657", false);
        workManager.createBook(true, "Cem Anos de Solidão", Arrays.asList(getGenre("Drama"), getGenre("Romance")), 1967, "Gabriel García Márquez", "Record", "978-8501012903", true);
        workManager.createBook(true, "A Revolução dos Bichos", Arrays.asList(getGenre("Drama"), getGenre("Ficção Científica")), 1945, "George Orwell", "Companhia das Letras", "978-8535909062", false);
        workManager.createBook(true, "O Pequeno Príncipe", Arrays.asList(getGenre("Aventura"), getGenre("Fantasia")), 1943, "Antoine de Saint-Exupéry", "Agir", "978-8522005470", true);
        workManager.createBook(false, "Morte no Nilo", Collections.singletonList(getGenre("Mistério")), 1937, "Agatha Christie", "L&PM Editores", "978-8525413158", false);
        workManager.createBook(true, "Orgulho e Preconceito", Collections.singletonList(getGenre("Romance")), 1813, "Jane Austen", "Martin Claret", "978-8572322301", true);
        workManager.createBook(false, "O Código Da Vinci", Collections.singletonList(getGenre("Suspense")), 2003, "Dan Brown", "Arqueiro", "978-8580410053", false);
        workManager.createBook(true, "Sapiens: Uma Breve História da Humanidade", Collections.singletonList(getGenre("Histórico")), 2011, "Yuval Noah Harari", "Companhia das Letras", "978-8535925017", true);
        workManager.createBook(false, "Dom Quixote", Collections.singletonList(getGenre("Aventura")), 1605, "Miguel de Cervantes", "Penguin Companhia", "978-8563560761", true);

        // --- 3. FILMES (10 EXEMPLOS) ---
        workManager.createFilm(Arrays.asList("Matthew McConaughey", "Anne Hathaway"), true, "Interestelar", Arrays.asList(getGenre("Ficção Científica"), getGenre("Aventura")), 2014, "Interstellar", Arrays.asList("HBO Max", "Prime Video"), "Christopher Nolan", 169, "Jonathan Nolan");
        workManager.createFilm(Arrays.asList("Elijah Wood", "Ian McKellen"), true, "O Senhor dos Anéis: A Sociedade do Anel", Arrays.asList(getGenre("Fantasia"), getGenre("Aventura")), 2001, "The Lord of the Rings: The Fellowship of the Ring", Arrays.asList("HBO Max"), "Peter Jackson", 178, "Fran Walsh");
        workManager.createFilm(Arrays.asList("Liam Neeson", "Ben Kingsley"), true, "A Lista de Schindler", Collections.singletonList(getGenre("Drama")), 1993, "Schindler's List", Arrays.asList("Netflix"), "Steven Spielberg", 195, "Steven Zaillian");
        workManager.createFilm(Arrays.asList("Johnny Depp", "Orlando Bloom"), false, "Piratas do Caribe: A Maldição do Pérola Negra", Collections.singletonList(getGenre("Aventura")), 2003, "Pirates of the Caribbean: The Curse of the Black Pearl", Arrays.asList("Disney+"), "Gore Verbinski", 143, "Ted Elliott");
        workManager.createFilm(Arrays.asList("Jodie Foster", "Anthony Hopkins"), true, "O Silêncio dos Inocentes", Collections.singletonList(getGenre("Suspense")), 1991, "The Silence of the Lambs", Arrays.asList("Amazon Prime Video"), "Jonathan Demme", 118, "Ted Tally");
        workManager.createFilm(Arrays.asList("Julia Roberts", "Hugh Grant"), false, "Um Lugar Chamado Notting Hill", Collections.singletonList(getGenre("Romance")), 1999, "Notting Hill", Arrays.asList("Globoplay"), "Roger Michell", 124, "Richard Curtis");
        workManager.createFilm(Arrays.asList("Bruce Willis", "Alan Rickman"), true, "Duro de Matar", Collections.singletonList(getGenre("Ação")), 1988, "Die Hard", Arrays.asList("Star+"), "John McTiernan", 132, "Jeb Stuart");
        workManager.createFilm(Arrays.asList("Bradley Cooper", "Ed Helms"), false, "Se Beber, Não Case!", Collections.singletonList(getGenre("Comédia")), 2009, "The Hangover", Arrays.asList("HBO Max"), "Todd Phillips", 100, "Jon Lucas");
        workManager.createFilm(Arrays.asList("Brad Pitt", "Morgan Freeman"), true, "Seven: Os Sete Crimes Capitais", Arrays.asList(getGenre("Crime"), getGenre("Suspense")), 1995, "Se7en", Arrays.asList("HBO Max"), "David Fincher", 127, "Andrew Kevin Walker");
        workManager.createFilm(Arrays.asList("Amy Adams", "Jeremy Renner"), false, "A Chegada", Collections.singletonList(getGenre("Ficção Científica")), 2016, "Arrival", Arrays.asList("Netflix"), "Denis Villeneuve", 116, "Eric Heisserer");

        // --- 4. SÉRIES (10 EXEMPLOS) ---
        workManager.createShow(Arrays.asList("Millie Bobby Brown", "David Harbour"), true, "Stranger Things", Arrays.asList(getGenre("Ficção Científica"), getGenre("Suspense")), 2016, "Stranger Things", Arrays.asList("Netflix"), 0);
        workManager.createShow(Arrays.asList("Henry Cavill", "Freya Allan"), true, "The Witcher", Arrays.asList(getGenre("Fantasia"), getGenre("Aventura")), 2019, "The Witcher", Arrays.asList("Netflix"), 0);
        workManager.createShow(Arrays.asList("Claire Foy", "Olivia Colman"), false, "The Crown", Collections.singletonList(getGenre("Histórico")), 2016, "The Crown", Arrays.asList("Netflix"), 0);
        workManager.createShow(Arrays.asList("Bryan Cranston", "Aaron Paul"), true, "Breaking Bad", Arrays.asList(getGenre("Drama"), getGenre("Crime")), 2008, "Breaking Bad", Arrays.asList("Netflix"), 2013);
        workManager.createShow(Arrays.asList("Tom Hiddleston", "Owen Wilson"), true, "Loki", Collections.singletonList(getGenre("Aventura")), 2021, "Loki", Arrays.asList("Disney+"), 0);
        workManager.createShow(Arrays.asList("Daniel Kaluuya", "Jesse Plemons"), false, "Black Mirror", Arrays.asList(getGenre("Ficção Científica"), getGenre("Drama")), 2011, "Black Mirror", Arrays.asList("Netflix"), 0);
        workManager.createShow(Arrays.asList("Kit Harington", "Emilia Clarke"), true, "Game of Thrones", Arrays.asList(getGenre("Fantasia"), getGenre("Aventura")), 2011, "Game of Thrones", Arrays.asList("HBO Max"), 2019);
        workManager.createShow(Arrays.asList("Jennifer Aniston", "Courteney Cox"), true, "Friends", Collections.singletonList(getGenre("Comédia")), 1994, "Friends", Arrays.asList("HBO Max"), 2004);
        workManager.createShow(Arrays.asList("Matthew McConaughey", "Woody Harrelson"), true, "True Detective", Collections.singletonList(getGenre("Crime")), 2014, "True Detective", Arrays.asList("HBO Max"), 0);
        workManager.createShow(Arrays.asList("Benedict Cumberbatch", "Martin Freeman"), false, "Sherlock", Collections.singletonList(getGenre("Mistério")), 2010, "Sherlock", Arrays.asList("Globoplay"), 0);

        // --- 5. TEMPORADAS (10 EXEMPLOS) ---
        // Crie as temporadas para as séries existentes.
        workManager.createSeason("Stranger Things", 1, 8, "15/07/2016");
        workManager.createSeason("Stranger Things", 2, 9, "27/10/2017");
        workManager.createSeason("Stranger Things", 3, 8, "04/07/2019");
        workManager.createSeason("Stranger Things", 4, 9, "27/05/2022");
        workManager.createSeason("The Witcher", 1, 8, "20/12/2019");
        workManager.createSeason("The Witcher", 2, 8, "17/12/2021");
        workManager.createSeason("The Witcher", 3, 8, "29/06/2023");
        workManager.createSeason("Breaking Bad", 1, 7, "20/01/2008");
        workManager.createSeason("Breaking Bad", 2, 13, "08/03/2009");
        workManager.createSeason("Game of Thrones", 1, 10, "17/04/2011");

        // --- 6. REVIEWS (10 EXEMPLOS) ---
        // Certifique-se de que as mídias/temporadas revisadas estejam marcadas como 'vista/lida' (seen: true).
        workManager.createReviewBook("Duna", "Uma jornada épica e complexa, leitura obrigatória!", 5, dateNow());
        workManager.createReviewBook("Cem Anos de Solidão", "Fascinante realismo mágico, mas um pouco denso às vezes.", 4, dateNow());
        workManager.createReviewFilm("Interestelar", "Visualmente deslumbrante e emocionalmente impactante. Final confuso.", 5, dateNow());
        workManager.createReviewFilm("O Silêncio dos Inocentes", "Um clássico do suspense que te prende do início ao fim.", 4, dateNow());
        workManager.createReviewFilm("Duro de Matar", "Ação pura e um protagonista icônico. Yippee-ki-yay!", 3, dateNow());
        workManager.createReviewShow("Stranger Things", 1, "Início nostálgico e cheio de mistério. Viciante!", 5, dateNow());
        workManager.createReviewShow("The Witcher", 1, "Boas atuações e efeitos visuais, mas a cronologia é um desafio.", 4, dateNow());
        workManager.createReviewShow("Breaking Bad", 1, "Walter White se transformando... que série!", 5, dateNow());
        workManager.createReviewShow("Game of Thrones", 1, "Personagens bem construídos e um mundo imersivo. Começo promissor!", 5, dateNow());
        workManager.createReviewShow("Friends", 1, "Engraçada e leve, perfeita para descontrair.", 4, dateNow());
    }

}
