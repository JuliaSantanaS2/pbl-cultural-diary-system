package ViewFX;

import Control.WorkManager;
import Module.Genre;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import org.controlsfx.control.CheckComboBox;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Unified Controller for the "New Media" screen.
 * Manages Book, Movie, Show, and Season forms, data validation,
 * and logic for saving new media entries.
 */
public class NewMediaViewController {

    // --- Control Instances ---
    private WorkManager workManager;
    private Validator bookValidator = new Validator();
    private Validator movieValidator = new Validator();
    private Validator showValidator = new Validator();
    private Validator seasonValidator = new Validator();

    // --- Common Components and Screen Switching ---
    @FXML private ToggleGroup mediaTypeToggleGroup;
    @FXML private VBox bookFormPane;
    @FXML private VBox movieFormPane;
    @FXML private VBox showFormPane;
    @FXML private VBox seasonFormPane;


    // --- Book Form Components ---
    @FXML private TextField bookTitleField;
    @FXML private TextField bookOriginalTitleField;
    @FXML private TextField bookAuthorField;
    @FXML private TextField bookPublisherField;
    @FXML private TextField bookIsbnField;
    @FXML private TextField bookReleaseYearField;
    @FXML private CheckComboBox<String> bookGenreCheckComboBox;
    @FXML private CheckBox bookSeenCheckBox;
    @FXML private CheckBox bookPhysicalCopyCheckBox;

    // --- Film Form Components ---
    @FXML private TextField movieTitleField;
    @FXML private TextField movieOriginalTitleField;
    @FXML private TextField movieDirectionField;
    @FXML private TextField movieScreenplayField;
    @FXML private TextField movieRunningtimeField;
    @FXML private TextField movieCastInputField;
    @FXML private ListView<String> movieCastListView;
    @FXML private TextField moviePlatformInputField;
    @FXML private ListView<String> moviePlatformListView;
    @FXML private TextField movieReleaseYearField;
    @FXML private CheckComboBox<String> movieGenreCheckComboBox;
    @FXML private CheckBox movieSeenCheckBox;

    // --- Show Form Components ---
    @FXML private TextField showTitleField;
    @FXML private TextField showOriginalTitleField;
    @FXML private TextField showCastInputField;
    @FXML private ListView<String> showCastListView;
    @FXML private TextField showPlatformInputField;
    @FXML private ListView<String> showPlatformListView;
    @FXML private TextField showReleaseYearField;
    @FXML private CheckComboBox<String> showGenreCheckComboBox;
    @FXML private CheckBox showSeenCheckBox;
    @FXML private TextField showYearEndField;

    // --- Season Form Components ---
    @FXML private TextField seasonSeasonNumberTitleField;
    @FXML private TextField seasonEpisodeCountTitleField;
    @FXML private DatePicker seasonReleaseDatePicker;
    @FXML private ComboBox<String> seasonShowComboBox;

    // Setter for WorkManager - called by MenuController
    public void setWorkManager(WorkManager workManager) {
        this.workManager = workManager;
        setupData();
    }

    // Empty constructor required by FXMLLoader
    public NewMediaViewController() {}

    /**
     * Method executed automatically when the FXML is loaded.
     * It's the starting point for setting up the UI components that don't depend on WorkManager.
     */
    @FXML
    public void initialize() {
        setupFormSwitching();
        setupBookValidation();
        setupMovieValidation();
        setupShowValidation();
        setupSeasonValidation();
    }

    /**
     * New method to set up data-dependent components, called after workManager is injected.
     */
    public void setupData() {
        if (workManager != null) {
            populateGenreCheckComboBoxes();
            populateShowCheckComboBoxesForSeason();
        } else {
            System.err.println("WorkManager is null. Cannot populate combo boxes for New Media.");
            showAlert("Error", "System not initialized correctly. Please restart.");
        }
    }

    /**
     * Adds a listener to the button group (Book, Movie) to
     * show/hide the correct form.
     */
    private void setupFormSwitching() {
        mediaTypeToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null && oldToggle != null) {
                oldToggle.setSelected(true);
                return;
            }

            bookFormPane.setVisible(false);
            movieFormPane.setVisible(false);
            showFormPane.setVisible(false);
            seasonFormPane.setVisible(false);

            if (newToggle != null) {
                String selectedType = ((ToggleButton) newToggle).getText();
                if ("Book".equals(selectedType)) {
                    bookFormPane.setVisible(true);
                } else if ("Movie".equals(selectedType)) {
                    movieFormPane.setVisible(true);
                } else if ("Show".equals(selectedType)) {
                    showFormPane.setVisible(true);
                } else if ("Season".equals(selectedType)) {
                    seasonFormPane.setVisible(true);
                }
            }
        });
    }

    /**
     * Configures validation rules for the Book form.
     */
    private void setupBookValidation() {
        bookValidator.createCheck().dependsOn("text", bookTitleField.textProperty()).withMethod(c -> { if (((String) c.get("text")).trim().isEmpty()) c.error("Title is required."); });
        bookValidator.createCheck().dependsOn("text", bookOriginalTitleField.textProperty()).withMethod(c -> { if (((String) c.get("text")).trim().isEmpty()) c.error("Original Title is required."); });
        bookValidator.createCheck().dependsOn("text", bookAuthorField.textProperty()).withMethod(c -> { if (((String) c.get("text")).trim().isEmpty()) c.error("Author is required."); });
        bookValidator.createCheck().dependsOn("text", bookPublisherField.textProperty()).withMethod(c -> { if (((String) c.get("text")).trim().isEmpty()) c.error("Publisher is required."); });

        // NOVO: Validação de ISBN para 13 dígitos numéricos
        bookValidator.createCheck().dependsOn("text", bookIsbnField.textProperty())
                .withMethod(c -> {
                    String isbn = (String) c.get("text");
                    if (isbn.trim().isEmpty()) {
                        c.error("ISBN is required.");
                    } else if (!isbn.matches("\\d{13}")) { // Verifica exatamente 13 dígitos numéricos
                        c.error("ISBN must be 13 digits long and contain only numbers.");
                    }
                });

        bookValidator.createCheck().dependsOn("text", bookReleaseYearField.textProperty()).withMethod(c -> { if (!((String) c.get("text")).matches("\\d{4}")) c.error("Year must have 4 digits."); });
        bookValidator.createCheck().dependsOn("size", Bindings.size(bookGenreCheckComboBox.getCheckModel().getCheckedItems())).withMethod(c -> { if (c.get("size").equals(0)) c.error("Please select a genre."); });
    }

    /**
     * Configures validation rules for the Film form.
     */
    private void setupMovieValidation() {
        movieValidator.createCheck().dependsOn("text", movieTitleField.textProperty()).withMethod(c -> { if (((String) c.get("text")).trim().isEmpty()) c.error("Title is required."); });
        movieValidator.createCheck().dependsOn("text", movieOriginalTitleField.textProperty()).withMethod(c -> { if (((String) c.get("text")).trim().isEmpty()) c.error("Original Title is required."); });
        movieValidator.createCheck().dependsOn("text", movieDirectionField.textProperty()).withMethod(c -> { if (((String) c.get("text")).trim().isEmpty()) c.error("Direction is required."); });
        movieValidator.createCheck().dependsOn("text", movieScreenplayField.textProperty()).withMethod(c -> { if (((String) c.get("text")).trim().isEmpty()) c.error("Screenplay is required."); });
        movieValidator.createCheck().dependsOn("size", Bindings.size(movieCastListView.getItems())).withMethod(c -> { if (c.get("size").equals(0)) c.error("Add at least one cast member."); });

        // NOVO: Validação para 'Where to Watch' (Platform)
        movieValidator.createCheck().dependsOn("size", Bindings.size(moviePlatformListView.getItems())).withMethod(c -> { if (c.get("size").equals(0)) c.error("Add at least one platform."); });

        movieValidator.createCheck().dependsOn("text", movieReleaseYearField.textProperty()).withMethod(c -> { if (!((String) c.get("text")).matches("\\d{4}")) c.error("Year must have 4 digits."); });
        movieValidator.createCheck().dependsOn("size", Bindings.size(movieGenreCheckComboBox.getCheckModel().getCheckedItems())).withMethod(c -> { if (c.get("size").equals(0)) c.error("Please select a genre."); });
    }

    private void setupShowValidation() {
        showValidator.createCheck().dependsOn("text", showTitleField.textProperty()).withMethod(c -> { if (((String) c.get("text")).trim().isEmpty()) c.error("Title is required."); });
        showValidator.createCheck().dependsOn("text", showOriginalTitleField.textProperty()).withMethod(c -> { if (((String) c.get("text")).trim().isEmpty()) c.error("Original Title is required."); });
        showValidator.createCheck().dependsOn("text", showReleaseYearField.textProperty()).withMethod(c -> { if (!((String) c.get("text")).matches("\\d{4}")) c.error("Year must have 4 digits."); });
        showValidator.createCheck().dependsOn("text", showYearEndField.textProperty()).withMethod(c -> { if (!((String) c.get("text")).matches("\\d+")) c.error("Year must be a number."); });
        showValidator.createCheck().dependsOn("size", Bindings.size(showCastListView.getItems())).withMethod(c -> { if (c.get("size").equals(0)) c.error("Add at least one cast member."); });

        // NOVO: Validação para 'Where to Watch' (Platform)
        showValidator.createCheck().dependsOn("size", Bindings.size(showPlatformListView.getItems())).withMethod(c -> { if (c.get("size").equals(0)) c.error("Add at least one platform."); });

        showValidator.createCheck().dependsOn("size", Bindings.size(showGenreCheckComboBox.getCheckModel().getCheckedItems())).withMethod(c -> { if (c.get("size").equals(0)) c.error("Please select a genre."); });
    }


    private void setupSeasonValidation() {
        seasonValidator.createCheck().dependsOn("value", seasonShowComboBox.valueProperty()).withMethod(c -> { if (c.get("value") == null) c.error("Please select a show."); });
        seasonValidator.createCheck().dependsOn("text", seasonSeasonNumberTitleField.textProperty()).withMethod(c -> { if (!((String) c.get("text")).matches("\\d+")) c.error("Season must be a number."); });
        seasonValidator.createCheck().dependsOn("text", seasonEpisodeCountTitleField.textProperty()).withMethod(c -> { if (!((String) c.get("text")).matches("\\d+")) c.error("Must be a number."); });

        // Validação de DatePicker: já garante que uma data válida (LocalDate) foi selecionada.
        // O formato DD/MM/YYYY é aplicado na conversão para String em saveSeason().
        seasonValidator.createCheck().dependsOn("value", seasonReleaseDatePicker.valueProperty()).withMethod(c -> { if (c.get("value") == null) c.error("Please select a release date."); });
    }

    /**
     * Populates both genre selection combo boxes with data from WorkManager.
     */
    private void populateGenreCheckComboBoxes() {
        if (workManager != null) {
            List<String> genreNames = workManager.getGenres().stream().map(Genre::getGenre).collect(Collectors.toList());
            bookGenreCheckComboBox.getItems().setAll(genreNames);
            movieGenreCheckComboBox.getItems().setAll(genreNames);
            showGenreCheckComboBox.getItems().setAll(genreNames);
        } else {
            System.err.println("WorkManager is null in populateGenreCheckComboBoxes. Cannot populate genres.");
            showAlert("Error", "System not initialized correctly. Please restart.");
        }
    }

    private void populateShowCheckComboBoxesForSeason() {
        if (workManager != null) {
            List<String> showNames = workManager.getShowName();
            seasonShowComboBox.getItems().setAll(showNames);
        } else {
            System.err.println("WorkManager is null in populateShowCheckComboBoxesForSeason. Cannot populate shows.");
            showAlert("Error", "System not initialized correctly. Please restart.");
        }
    }

    /**
     * Single method called by the "Save Media" button.
     * It decides which validator to use and which saving method to call.
     */
    @FXML
    void saveMedia() {
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
            case "Show":
                currentValidator = showValidator;
                break;
            case "Season":
                currentValidator = seasonValidator;
                break;
        }

        if (currentValidator != null) {
            isFormValid = currentValidator.validate();
        }


        if (isFormValid) {
            switch (selectedType) {
                case "Book":
                    saveBook();
                    break;
                case "Movie":
                    saveMovie();
                    break;
                case "Show":
                    saveShow();
                    break;
                case "Season":
                    saveSeason();
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

    private void saveBook() {
        try {
            List<String> selectedGenreNames = bookGenreCheckComboBox.getCheckModel().getCheckedItems();
            List<Genre> genresToSave = workManager.getGenres().stream().filter(g -> selectedGenreNames.contains(g.getGenre())).collect(Collectors.toList());

            workManager.createBook(
                    bookSeenCheckBox.isSelected(),
                    bookTitleField.getText(),
                    genresToSave,
                    Integer.parseInt(bookReleaseYearField.getText()),
                    bookAuthorField.getText(),
                    bookPublisherField.getText(),
                    bookIsbnField.getText(),
                    bookPhysicalCopyCheckBox.isSelected()
            );
            showAlert("Success", "Book '" + bookTitleField.getText() + "' saved successfully!");
            clearAllForms();
        } catch (Exception e) {
            showAlert("Error", "An error occurred while saving the book: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveMovie() {
        try {
            List<String> castToSave = new ArrayList<>(movieCastListView.getItems());
            List<String> platformsToSave = new ArrayList<>(moviePlatformListView.getItems());
            List<String> selectedGenreNames = movieGenreCheckComboBox.getCheckModel().getCheckedItems();
            List<Genre> genresToSave = workManager.getGenres().stream().filter(g -> selectedGenreNames.contains(g.getGenre())).collect(Collectors.toList());

            workManager.createFilm(
                    castToSave,
                    movieSeenCheckBox.isSelected(),
                    movieTitleField.getText(),
                    genresToSave,
                    Integer.parseInt(movieReleaseYearField.getText()),
                    movieOriginalTitleField.getText(),
                    platformsToSave,
                    movieDirectionField.getText(),
                    Integer.parseInt(movieRunningtimeField.getText()),
                    movieScreenplayField.getText()
            );
            showAlert("Success", "Film '" + movieTitleField.getText() + "' saved successfully!");
            clearAllForms();
        } catch (Exception e) {
            showAlert("Error", "An error occurred while saving the film: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveShow() {
        try {
            List<String> showCastToSave = new ArrayList<>(showCastListView.getItems());
            List<String> showPlatformsToSave = new ArrayList<>(showPlatformListView.getItems());
            List<String> selectedGenreNames = showGenreCheckComboBox.getCheckModel().getCheckedItems();
            List<Genre> genresToSave = workManager.getGenres().stream().filter(g -> selectedGenreNames.contains(g.getGenre())).collect(Collectors.toList());

            workManager.createShow(
                    showCastToSave,
                    showSeenCheckBox.isSelected(),
                    showTitleField.getText(),
                    genresToSave,
                    Integer.parseInt(showReleaseYearField.getText()),
                    showOriginalTitleField.getText(),
                    showPlatformsToSave,
                    Integer.parseInt(showYearEndField.getText())
            );
            showAlert("Success", "Show '" + showTitleField.getText() + "' saved successfully!");
            populateShowCheckComboBoxesForSeason();
            clearAllForms();
        } catch (Exception e) {
            showAlert("Error", "An error occurred while saving the show: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveSeason() {
        try {
            String selectedShowName = seasonShowComboBox.getValue();
            LocalDate dataSelecionada = seasonReleaseDatePicker.getValue();

            if (dataSelecionada == null) {
                showAlert("Error", "Please select a release date.");
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormatada = dataSelecionada.format(formatter);

            int result = workManager.createSeason(
                    selectedShowName,
                    Integer.parseInt(seasonSeasonNumberTitleField.getText()),
                    Integer.parseInt(seasonEpisodeCountTitleField.getText()),
                    dataFormatada
            );

            if (result == 0) {
                showAlert("Success", "Season saved successfully!");
                populateShowCheckComboBoxesForSeason();
                clearAllForms();
            } else if (result == 1) {
                showAlert("Error", "Show '" + selectedShowName + "' not found. Please check the title.");
            } else if (result == 4) {
                showAlert("Error", "Season " + seasonSeasonNumberTitleField.getText() + " already exists for show '" + selectedShowName + "'.");
            } else if (result == 98) {
                showAlert("Error", "Invalid season data. Check season number, episode count, or release date.");
            } else {
                showAlert("Error", "An unexpected error occurred while saving the season. Code: " + result);
            }

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for season number and episode count.");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Internal Error", "An unexpected error occurred while saving the season: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Auxiliary and Action Methods for Lists ---

    private void clearAllForms() {
        bookTitleField.clear(); bookOriginalTitleField.clear(); bookAuthorField.clear();
        bookPublisherField.clear(); bookIsbnField.clear(); bookReleaseYearField.clear();
        bookSeenCheckBox.setSelected(false); bookPhysicalCopyCheckBox.setSelected(false);
        bookGenreCheckComboBox.getCheckModel().clearChecks();

        movieTitleField.clear(); movieOriginalTitleField.clear(); movieDirectionField.clear();
        movieScreenplayField.clear(); movieReleaseYearField.clear(); movieRunningtimeField.clear();
        movieSeenCheckBox.setSelected(false); movieGenreCheckComboBox.getCheckModel().clearChecks();
        movieCastInputField.clear(); movieCastListView.getItems().clear();
        moviePlatformInputField.clear(); moviePlatformListView.getItems().clear();

        showTitleField.clear(); showOriginalTitleField.clear(); showReleaseYearField.clear();
        showYearEndField.clear(); showSeenCheckBox.setSelected(false);
        showGenreCheckComboBox.getCheckModel().clearChecks();
        showCastInputField.clear(); showCastListView.getItems().clear();
        showPlatformInputField.clear(); showPlatformListView.getItems().clear();

        seasonSeasonNumberTitleField.clear(); seasonEpisodeCountTitleField.clear();
        seasonReleaseDatePicker.setValue(null);
        seasonShowComboBox.getSelectionModel().clearSelection();
    }

    @FXML private void movieAddCastMember() { String name = movieCastInputField.getText().trim(); if (!name.isEmpty()) { movieCastListView.getItems().add(name); movieCastInputField.clear(); } }
    @FXML private void movieRemoveCastMember() { movieCastListView.getItems().remove(movieCastListView.getSelectionModel().getSelectedItem()); }
    @FXML private void movieAddPlatform() { String name = moviePlatformInputField.getText().trim(); if (!name.isEmpty()) { moviePlatformListView.getItems().add(name); moviePlatformInputField.clear(); } }
    @FXML private void movieRemovePlatform() { moviePlatformListView.getItems().remove(moviePlatformListView.getSelectionModel().getSelectedItem()); }

    @FXML private void showAddCastMember() { String name = showCastInputField.getText().trim(); if (!name.isEmpty()) { showCastListView.getItems().add(name); showCastInputField.clear(); } }
    @FXML private void showRemoveCastMember() { showCastListView.getItems().remove(showCastListView.getSelectionModel().getSelectedItem()); }
    @FXML private void showAddPlatform() { String name = showPlatformInputField.getText().trim(); if (!name.isEmpty()) { showPlatformListView.getItems().add(name); showPlatformInputField.clear(); } }
    @FXML private void showRemovePlatform() { showPlatformListView.getItems().remove(showPlatformListView.getSelectionModel().getSelectedItem()); }

    private void showAlert(String title, String message) { Alert alert = new Alert(Alert.AlertType.INFORMATION); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait(); }
}