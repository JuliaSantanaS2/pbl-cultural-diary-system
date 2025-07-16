package ViewFX;

import Control.WorkManager;
import Module.Genre;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.stream.Collectors;

public class GenreController {

    private WorkManager workManager;

    @FXML private TextField genreTitleField;
    @FXML private ListView<String> genreListView;

    // Empty constructor (required by FXMLLoader)
    public GenreController() {
    }

    // This method is called automatically by FXMLLoader.
    // It should not contain calls that depend on 'workManager'
    // because 'workManager' has not yet been injected at this point.
    @FXML
    public void initialize(){
        // Removed the call to populateGenre() from here.
        // It will now be called in setupData() after workManager is injected.
    }

    // Setter method to inject the WorkManager instance.
    // This method will be called by the MenuController.
    public void setWorkManager(WorkManager workManager) {
        this.workManager = workManager;
        // After workManager is set, call the method to populate the data.
        setupData();
    }

    // New method to set up data-dependent components.
    // This method will be called by setWorkManager().
    public void setupData(){
        populateGenre();
    }

    private void clearAllForms(){
        genreTitleField.clear();
    }


    @FXML
    void addGenre() {
        String newGenreName = genreTitleField.getText();
        if (newGenreName == null || newGenreName.trim().isEmpty()) {
            showAlert("Error", "Genre name cannot be empty.");
            return;
        }

        // Checks if workManager is not null before using it
        if (workManager != null) {
            workManager.addGenre(newGenreName);
            populateGenre();
            clearAllForms();
            showAlert("Success", "Genre '" + newGenreName + "' added successfully!");
        } else {
            showAlert("Error", "System not initialized correctly. Please try again.");
        }
    }


    private void populateGenre(){
        // Checks if workManager is not null before using it
        if (workManager != null) {
            List<String> genreNames = workManager.getGenres().stream()
                    .map(Genre::getGenre)
                    .collect(Collectors.toList());

            if (genreListView != null) {
                genreListView.getItems().setAll(genreNames);
            }
        } else {
            System.err.println("Error: WorkManager is null in populateGenre().");
            // Optional: showAlert("Error", "Could not load genres.");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}