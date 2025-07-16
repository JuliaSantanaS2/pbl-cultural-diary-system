package Main;

import Control.WorkManager;
import Module.Genre;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GenreManagerApp extends Application {

    private final WorkManager workManager = new WorkManager();
    private final ObservableList<String> genreNames = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Gerenciador de Gêneros");

        // 1. Criar os Controles (Nodes)
        Label instructionLabel = new Label("Digite o nome do novo gênero:");
        TextField genreTextField = new TextField();
        genreTextField.setPromptText("Ex: Ficção Científica"); // Texto de dica
        Button addButton = new Button("Adicionar Gênero");

        Label listLabel = new Label("Gêneros Cadastrados:");
        ListView<String> genreListView = new ListView<>(genreNames);

        // 2. Lógica do Botão
        addButton.setOnAction(event -> {
            String newGenreName = genreTextField.getText().trim();
            if (!newGenreName.isEmpty()) {
                workManager.addGenre(newGenreName);
                genreTextField.clear();
                updateGenreList();
            }
        });

        // 3. Montar o Layout
        VBox root = new VBox(10); // 10 é o espaçamento
        root.setPadding(new Insets(15));
        root.getChildren().addAll(instructionLabel, genreTextField, addButton, listLabel, genreListView);

        // 4. Criar a Cena e colocar no Palco (Stage)
        Scene scene = new Scene(root, 400, 500);
        primaryStage.setScene(scene);

        // 5. Carregar dados iniciais e mostrar a janela
        updateGenreList();
        primaryStage.show();
    }

    /**
     * Atualiza a lista de gêneros na tela.
     */
    private void updateGenreList() {
        genreNames.clear();
        for (Genre genre : workManager.getGenres()) {
            genreNames.add(genre.getGenre());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}