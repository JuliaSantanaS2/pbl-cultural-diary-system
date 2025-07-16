package ViewFX;

import Control.WorkManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.net.URL;

/**
 * Esta classe é a "Torre de Controle" da sua aplicação.
 * Ela gerencia o menu lateral e é responsável por carregar
 * as diferentes telas de conteúdo no centro da janela.
 */
public class MenuController {

    // A conexão com o BorderPane principal do seu Menu.fxml
    @FXML
    private BorderPane mainContainer;

    private WorkManager workManager;

    // Construtor principal para injeção de dependência do WorkManager
    public MenuController(WorkManager workManager) {
        this.workManager = workManager;
    }

    // Método initialize para carregar a tela padrão
    @FXML
    public void initialize() {
        // Carrega a tela de busca e listagem como a tela inicial padrão
        loadView("SearchAndListMediaView.fxml");
    }

    /**
     * Método chamado pelo botão "New Media".
     * A única responsabilidade dele é chamar o loadView com o nome do arquivo FXML unificado.
     */
    @FXML
    void showNewMediaView() {
        System.out.println("Botão New Media clicado! Carregando a tela unificada...");
        loadView("NewMediaView.fxml");
    }

    /**
     * Método chamado pelo botão "Genres".
     * (Atualmente um placeholder, carregará a tela de gêneros no futuro)
     */
    @FXML
    void showGenresView() {
        System.out.println("Botão Genres clicado!");
        loadView("Genre.fxml");
    }

    /**
     * Método chamado pelo botão "Review".
     * (Atualmente um placeholder)
     */
    @FXML
    void showReviewView() {
        System.out.println("Botão Review clicado!");
        loadView("NewReviewView.fxml");
    }

    /**
     * Método chamado pelo botão "Search / List".
     * (Atualmente um placeholder)
     */
    @FXML
    void showSearchView() {
        System.out.println("Botão Search / List clicado!");
        loadView("SearchAndListMediaView.fxml");
    }

    /**
     * Método auxiliar genérico para carregar qualquer arquivo FXML no centro do BorderPane.
     * @param fxmlFileName O nome do arquivo FXML a ser carregado (ex: "NewMediaView.fxml")
     */
    private void loadView(String fxmlFileName) {
        try {
            String path = "/" + fxmlFileName;
            URL resourceUrl = getClass().getResource(path);

            if (resourceUrl == null) {
                System.err.println("ERRO: Arquivo FXML não encontrado no caminho: " + path);
                showAlert("Erro Crítico", "O arquivo de interface '" + fxmlFileName + "' não foi encontrado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent view = loader.load();

            // Obtém o controlador da tela carregada
            Object controller = loader.getController();

            // Verifica o tipo do controlador e injeta o WorkManager
            // e chama o método setupData() em cada um deles
            if (controller instanceof NewMediaViewController) {
                ((NewMediaViewController) controller).setWorkManager(this.workManager);
                ((NewMediaViewController) controller).setupData(); // Chama setupData()
            } else if (controller instanceof NewReviewViewController) {
                ((NewReviewViewController) controller).setWorkManager(this.workManager);
                ((NewReviewViewController) controller).setupData(); // Chama setupData()
            } else if (controller instanceof SearchAndListMediaController) {
                ((SearchAndListMediaController) controller).setWorkManager(this.workManager);
                ((SearchAndListMediaController) controller).setupData(); // Chama setupData()
            } else if (controller instanceof GenreController) {
                ((GenreController) controller).setWorkManager(this.workManager);
                ((GenreController) controller).setupData(); // Chama setupData()
            }
            // Adicione mais blocos 'else if' se você tiver outros controladores de tela

            mainContainer.setCenter(view);

        } catch (IOException e) {
            System.err.println("Falha ao carregar a view: " + fxmlFileName);
            e.printStackTrace();
            showAlert("Erro ao Carregar", "Ocorreu um erro ao processar a tela. Verifique o console para detalhes.");
        }
    }

    // Método auxiliar para mostrar alertas de erro
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}