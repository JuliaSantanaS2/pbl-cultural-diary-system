package ViewFX;

import Control.WorkManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.synedra.validatorfx.ValidationMessage; // Importar ValidationMessage
import net.synedra.validatorfx.Validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.scene.control.Label;
import java.util.stream.Collectors; // Importar Collectors

/**
 * Unified Controller for the "New Review" screen.
 * Manages review forms for Books, Films, and Shows/Seasons,
 * data validation, and logic for saving new reviews.
 */
public class NewReviewViewController {

    // --- Control Instances ---
    private WorkManager workManager;
    private Validator bookValidator = new Validator();
    private Validator movieValidator = new Validator();
    private Validator showValidator = new Validator();

    // --- Common and Screen Switching Components ---
    @FXML private ToggleGroup mediaTypeToggleGroup;
    @FXML private VBox bookFormPane;
    @FXML private VBox movieFormPane;
    @FXML private VBox showFormPane;


    // --- Book Form Components ---
    @FXML private ComboBox <String> bookNamesComboBox;
    @FXML private TextArea reviewBookTextArea;
    @FXML private Slider bookRatingSlider;
    @FXML private Label bookRatingValueLabel;

    // --- Film Form Components ---
    @FXML private ComboBox <String> movieNamesComboBox;
    @FXML private TextArea reviewMovieTextArea;
    @FXML private Slider movieRatingSlider;
    @FXML private Label movieRatingValueLabel;

    // --- Show and Season Form Components ---
    @FXML private ComboBox <String> showNamesComboBox;
    @FXML private ComboBox <Integer> seasonNamesComboBox;
    @FXML private TextArea reviewShowTextArea;
    @FXML private Slider showRatingSlider;
    @FXML private Label showRatingValueLabel;


    // Empty constructor required by FXMLLoader
    public NewReviewViewController() {}

    // Setter for WorkManager - called by MenuController
    public void setWorkManager(WorkManager workManager) {
        this.workManager = workManager;
        // Call setupData() once workManager is set
        setupData();
    }

    /**
     * Method executed automatically when the FXML is loaded.
     * It's the starting point for setting up the UI components that don't depend on WorkManager.
     */
    @FXML
    public void initialize() {
        setupFormSwitching();
        setupAllValidations();
        setupAllSliderListeners();

        seasonNamesComboBox.setDisable(true);

        showNamesComboBox.valueProperty().addListener((obs, oldShow, newShow) -> {
            if (newShow != null) {
                // If a new show is selected, populate seasons
                populateSeasonComboBox(newShow);
                seasonNamesComboBox.setDisable(false); // And enable the Season ComboBox
            } else {
                // If no show is selected, clear and disable the Season ComboBox
                seasonNamesComboBox.getItems().clear();
                seasonNamesComboBox.setDisable(true);
            }
        });
    }

    /**
     * New method to set up data-dependent components, called after workManager is injected.
     */
    public void setupData() {
        if (workManager != null) {
            populateAllComboBoxes();
        } else {
            System.err.println("WorkManager is null. Cannot populate combo boxes.");
            showAlert("Error", "System not initialized correctly. Please restart.");
        }
    }

    /**
     * Adds a listener to the button group (Book, Movie) to
     * show/hide the correct form.
     */
    private void setupFormSwitching() {
        mediaTypeToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            // Ensures that the switch only happens if a new button is selected
            if (newToggle == null && oldToggle != null) {
                oldToggle.setSelected(true); // Prevents no button from being selected
                return;
            }

            bookFormPane.setVisible(false);
            movieFormPane.setVisible(false);
            showFormPane.setVisible(false);

            if (newToggle != null) {
                String selectedType = ((ToggleButton) newToggle).getText();
                if ("Book".equals(selectedType)) {
                    bookFormPane.setVisible(true);
                } else if ("Movie".equals(selectedType)) {
                    movieFormPane.setVisible(true);
                } else if ("Show/Season".equals(selectedType)) {
                    showFormPane.setVisible(true);
                }
            }
        });
    }

    private String dateNow() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private void setupAllValidations() {
        // Validation for Book - removed .decorates() calls
        bookValidator.createCheck().dependsOn("value", bookNamesComboBox.valueProperty()).withMethod(c -> { if (c.get("value") == null) c.error("Please select a book."); });
        bookValidator.createCheck().dependsOn("text", reviewBookTextArea.textProperty()).withMethod(c -> { if (((String) c.get("text")).trim().isEmpty()) c.error("The review cannot be empty."); });

        // Validation for Film - removed .decorates() calls
        movieValidator.createCheck().dependsOn("value", movieNamesComboBox.valueProperty()).withMethod(c -> { if (c.get("value") == null) c.error("Please select a film."); });
        movieValidator.createCheck().dependsOn("text", reviewMovieTextArea.textProperty()).withMethod(c -> { if (((String) c.get("text")).trim().isEmpty()) c.error("The review cannot be empty."); });

        // Validation for Show/Season - removed .decorates() calls
        showValidator.createCheck().dependsOn("value", showNamesComboBox.valueProperty()).withMethod(c -> { if (c.get("value") == null) c.error("Please select a show."); });
        showValidator.createCheck().dependsOn("value", seasonNamesComboBox.valueProperty()).withMethod(c -> { if (c.get("value") == null) c.error("Please select a season."); });
        showValidator.createCheck().dependsOn("text", reviewShowTextArea.textProperty()).withMethod(c -> { if (((String) c.get("text")).trim().isEmpty()) c.error("The review cannot be empty."); });
    }

    private void setupAllSliderListeners() {
        // Listener for Book slider
        bookRatingSlider.valueProperty().addListener((obs, oldVal, newVal) -> bookRatingValueLabel.setText(String.format("%.0f", newVal)));
        // Listener for Film slider
        movieRatingSlider.valueProperty().addListener((obs, oldVal, newVal) -> movieRatingValueLabel.setText(String.format("%.0f", newVal)));
        // Listener for Show slider
        showRatingSlider.valueProperty().addListener((obs, oldVal, newVal) -> showRatingValueLabel.setText(String.format("%.0f", newVal)));
    }

    private void populateAllComboBoxes() {
        if (workManager != null) {
            bookNamesComboBox.getItems().setAll(workManager.getBooksName());
            movieNamesComboBox.getItems().setAll(workManager.getFilmName());
            showNamesComboBox.getItems().setAll(workManager.getShowName());
        } else {
            System.err.println("WorkManager is null in populateAllComboBoxes. Cannot populate combo boxes.");
            showAlert("Error", "System not initialized correctly. Please restart.");
        }
    }

    private void populateSeasonComboBox(String showName) {
        if (workManager != null) {
            List<Integer> seasonNumbers = workManager.getSeasonsByShowName(showName);
            seasonNamesComboBox.getItems().setAll(seasonNumbers);
        } else {
            System.err.println("WorkManager is null in populateSeasonComboBox. Cannot populate seasons.");
            showAlert("Error", "System not initialized correctly. Please restart.");
        }
    }

    /**
     * Single method called by the "Save Media" button.
     * It decides which validator to use and which saving method to call.
     */
    @FXML
    void saveReview() {
        ToggleButton selectedButton = (ToggleButton) mediaTypeToggleGroup.getSelectedToggle();
        String selectedType = selectedButton.getText();
        boolean isFormValid = false;
        Validator currentValidator = null;

        switch (selectedType) {
            case "Book":
                currentValidator = bookValidator;
                break;
            case "Movie":
                currentValidator = movieValidator;
                break;
            case "Show/Season":
                currentValidator = showValidator;
                break;
        }

        if (currentValidator != null) {
            isFormValid = currentValidator.validate();
        }

        if (isFormValid) {
            switch (selectedType) {
                case "Book":
                    saveBookReview();
                    break;
                case "Movie":
                    saveMovieReview();
                    break;
                case "Show/Season":
                    saveShowReview();
                    break;
            }
        } else {
            // Coleta todas as mensagens de erro e exibe em um único alerta
            String errorMessages = currentValidator.getValidationResult().getMessages().stream()
                    .map(ValidationMessage::getText)
                    .collect(Collectors.joining("\n- ", "- ", ""));

            showAlert("Validation Error", "Please correct the following issues:\n" + errorMessages);
        }
    }

    // --- Specific Saving Logic Methods ---

    private void saveBookReview() {
        try {
            int result = workManager.createReviewBook(
                    bookNamesComboBox.getValue(),
                    reviewBookTextArea.getText(),
                    (int) bookRatingSlider.getValue(),
                    dateNow()
            );
            if (result == 0) {
                showAlert("Success", "Review for book '" + bookNamesComboBox.getValue() + "' saved successfully!");
                clearAllForms();
            } else if (result == 1) {
                showAlert("Error", "Book not found.");
            } else if (result == 2) {
                showAlert("Error", "You haven't read this book yet.");
            } else {
                showAlert("Error", "An unexpected error occurred while saving the book review.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred while saving the book review: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveMovieReview() {
        try {
            int result = workManager.createReviewFilm(
                    movieNamesComboBox.getValue(),
                    reviewMovieTextArea.getText(),
                    (int) movieRatingSlider.getValue(),
                    dateNow()
            );
            if (result == 0) {
                showAlert("Success", "Review for film '" + movieNamesComboBox.getValue() + "' saved successfully!");
                clearAllForms();
            } else if (result == 1) {
                showAlert("Error", "Film not found.");
            } else if (result == 2) {
                showAlert("Error", "You haven't watched this film yet.");
            } else {
                showAlert("Error", "An unexpected error occurred while saving the film review.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred while saving the film review: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveShowReview() {
        try {
            int result = workManager.createReviewShow(
                    showNamesComboBox.getValue(),
                    seasonNamesComboBox.getValue(),
                    reviewShowTextArea.getText(),
                    (int) showRatingSlider.getValue(),
                    dateNow()
            );
            if (result == 0) {
                showAlert("Success", "Review for season " + seasonNamesComboBox.getValue() + " of '" + showNamesComboBox.getValue() + "' saved successfully!");
                clearAllForms();
            } else if (result == 1) {
                showAlert("Error", "Show not found.");
            } else if (result == 3) {
                showAlert("Error", "Season not found for this show.");
            } else {
                showAlert("Error", "An unexpected error occurred while saving the show review.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred while saving the show review: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Auxiliary and Action Methods for Lists ---

    private void clearAllForms() {
        reviewBookTextArea.clear();
        bookNamesComboBox.getSelectionModel().clearSelection();
        bookRatingSlider.setValue(1);

        reviewMovieTextArea.clear();
        movieNamesComboBox.getSelectionModel().clearSelection();
        movieRatingSlider.setValue(1);

        reviewShowTextArea.clear();
        showNamesComboBox.getSelectionModel().clearSelection();
        seasonNamesComboBox.getSelectionModel().clearSelection();
        seasonNamesComboBox.setDisable(true);
        showRatingSlider.setValue(1);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}