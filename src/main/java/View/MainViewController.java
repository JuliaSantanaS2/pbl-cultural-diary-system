package View;

import Control.WorkManager;
import Module.Genre;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class MainViewController {

    // Instância do seu cérebro da aplicação
    private WorkManager workManager;

    // --- Componentes do FXML ---
    // A anotação @FXML conecta esta variável ao componente no FXML com o mesmo fx:id

    // Container dos formulários
    @FXML
    private VBox formBook; // VBox do formulário de Livro

    // Campos do formulário de Livro
    @FXML
    private TextField titleField;
    @FXML
    private TextField originalTitleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField publisherField;
    @FXML
    private TextField isbnField;
    @FXML
    private TextField releaseYearField;
    @FXML
    private ComboBox<String> genreComboBox;
    @FXML
    private CheckBox seenCheckBox;
    @FXML
    private CheckBox physicalCopyCheckBox;

    /**
     * Método especial que o JavaFX chama automaticamente DEPOIS
     * que o FXML é carregado. Perfeito para inicializar a tela.
     */
    @FXML
    public void initialize() {
        // 1. Cria a instância do WorkManager
        this.workManager = new WorkManager();

        // 2. Popula o ComboBox com os gêneros existentes
        populateGenreComboBox();

        // (Opcional) Mostra o formulário de livro como padrão
        formBook.setVisible(true);
    }

    /**
     * Pega os gêneros do WorkManager e os adiciona ao ComboBox.
     */
    private void populateGenreComboBox() {
        // Pega a lista de objetos Genre do WorkManager
        List<Genre> genres = workManager.getGenres();
        // Extrai apenas os nomes (String) para mostrar na ComboBox
        List<String> genreNames = genres.stream()
                .map(Genre::getGenre)
                .collect(Collectors.toList());
        // Limpa itens antigos e adiciona os novos
        genreComboBox.getItems().clear();
        genreComboBox.getItems().addAll(genreNames);
    }

    /**
     * Método que será chamado quando o botão "Salva Media" for clicado.
     */
    @FXML
    private void saveMedia() {
        System.out.println("Botão Salvar Clicado!");

        // Lógica para salvar um LIVRO (exemplo)
        // 1. Ler os dados dos campos da tela
        String title = titleField.getText();
        String author = authorField.getText();
        // ... leia todos os outros campos

        // 2. Validar os dados (verificar se não estão vazios, etc.)
        if (title.isEmpty() || author.isEmpty()) {
            // Mostra um alerta para o usuário
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Validação");
            alert.setHeaderText("Campos Obrigatórios Vazios");
            alert.setContentText("Por favor, preencha Título e Autor.");
            alert.showAndWait();
            return;
        }

        // 3. Pegar o Gênero selecionado no ComboBox
        String selectedGenreName = genreComboBox.getValue();
        List<Genre> selectedGenres = workManager.getGenres().stream()
                .filter(g -> g.getGenre().equals(selectedGenreName))
                .collect(Collectors.toList());


        // 4. Chamar o método do WorkManager para criar o livro
        workManager.createBook(
                seenCheckBox.isSelected(),
                title,
                selectedGenres,
                Integer.parseInt(releaseYearField.getText()),
                author,
                publisherField.getText(),
                isbnField.getText(),
                physicalCopyCheckBox.isSelected()
        );

        // 5. Mostrar um alerta de sucesso
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setContentText("Livro '" + title + "' salvo com sucesso!");
        alert.showAndWait();

        // 6. Limpar os campos do formulário
        clearBookForm();
    }

    private void clearBookForm() {
        titleField.clear();
        originalTitleField.clear();
        authorField.clear();
        publisherField.clear();
        isbnField.clear();
        releaseYearField.clear();
        genreComboBox.getSelectionModel().clearSelection();
        seenCheckBox.setSelected(false);
        physicalCopyCheckBox.setSelected(false);
    }
}