package Main;

import Control.WorkManager;
import ViewFX.MenuController; // Importe MenuController
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainJava extends Application {

    private WorkManager workManagerInstance; // Declare a única instância aqui

    @Override
    public void start(Stage stage) throws IOException {
        workManagerInstance = new WorkManager(); // Crie o WorkManager UMA VEZ aqui

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Menu.fxml"));
        // Isso injetará a instância do WorkManager no MenuController
        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == MenuController.class) {
                return new MenuController(workManagerInstance); // Passe a instância para o MenuController
            }
            try {
                return controllerClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Cultural Diary System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}