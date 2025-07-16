package ViewFX;

import Control.WorkManager;
import Module.Book;
import Module.Films;
import Module.Genre;
import Module.Media;
import Module.Review;
import Module.Show;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Comparator;

public class SearchAndListMediaController {

    private WorkManager workManager;

    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private VBox contentArea;
    @FXML
    private AnchorPane filterSidebar;
    @FXML
    private Button toggleSidebarButton;
    @FXML
    private TableView<Media> mediaTableView;
    @FXML
    private TextField titleIsbnFilterField;
    @FXML
    private TextField yearFilterField;
    @FXML
    private CheckComboBox<String> genreFilterCheckComboBox;
    @FXML
    private TextField personFilterField;
    @FXML
    private CheckBox filterBookCheckBox;
    @FXML
    private CheckBox filterFilmCheckBox;
    @FXML
    private CheckBox filterShowCheckBox;
    @FXML
    private ToggleGroup sortToggleGroup;
    @FXML
    private RadioButton sortRatingDescRadio;
    @FXML
    private RadioButton sortRatingAscRadio;
    @FXML
    private RadioButton sortTitleAscRadio;

    private ObservableList<Media> masterMediaList = FXCollections.observableArrayList();
    private ObservableList<Media> filteredAndSortedMediaList = FXCollections.observableArrayList();

    private static final double SIDEBAR_WIDTH = 200.0;

    // Setter for WorkManager - called by MenuController
    public void setWorkManager(WorkManager workManager) {
        this.workManager = workManager;
        // Call setupData() once workManager is set
        setupData();
    }

    // Constructor (required by FXMLLoader)
    public SearchAndListMediaController() {
    }

    // Method called automatically by FXMLLoader.
    // Should only contain UI setup that DOES NOT depend on 'workManager'.
    @FXML
    public void initialize() {
        // Initial setup for the right sidebar: EXTENDED by default
        filterSidebar.setTranslateX(0); // Sidebar visible by default
        AnchorPane.setRightAnchor(contentArea, SIDEBAR_WIDTH); // Content pushed by sidebar
        toggleSidebarButton.setText(">"); // Button text to indicate closing (points right)
        AnchorPane.setRightAnchor(toggleSidebarButton, SIDEBAR_WIDTH + 10.0); // Adjust button position

        // Column setup must happen early, before data is loaded
        setupTableColumns();

        // FIX: Make columns fill the table width
        mediaTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // New method to set up data-dependent components, called after workManager is injected.
    public void setupData() {
        if (workManager != null) { // Defensive check
            populateGenreCheckComboBox();
            loadAllMedia();
            applyFiltersAndSort(); // This also triggers initial data display
        } else {
            System.err.println("WorkManager is null. Cannot populate initial data for Search and List.");
            showAlert("Error", "System not initialized correctly. Please restart.");
        }
    }


    private void setupTableColumns() {
        // Clear existing columns to re-add them with new structure if this method is called multiple times
        mediaTableView.getColumns().clear();

        // 1. Title Column
        TableColumn<Media, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        titleColumn.setPrefWidth(150);

        // 2. Type Column
        TableColumn<Media, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(cellData -> {
            Media media = cellData.getValue();
            if (media instanceof Book) {
                return new SimpleStringProperty("Book");
            } else if (media instanceof Films) {
                return new SimpleStringProperty("Film");
            } else if (media instanceof Show) {
                return new SimpleStringProperty("Show");
            }
            return new SimpleStringProperty("Unknown");
        });
        typeColumn.setPrefWidth(80);

        // 3. Year Column
        TableColumn<Media, String> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getYearRelease())));
        yearColumn.setPrefWidth(60);

        // 4. Rating Column
        TableColumn<Media, String> ratingColumn = new TableColumn<>("Rating");
        ratingColumn.setCellValueFactory(cellData -> {
            float avg = WorkManager.calculateAverage(cellData.getValue());
            return new SimpleStringProperty(String.format("%.1f ★", avg));
        });
        ratingColumn.setPrefWidth(70);

        // 5. Genres Column
        TableColumn<Media, String> genresColumn = new TableColumn<>("Genres");
        genresColumn.setCellValueFactory(cellData -> {
            List<Genre> genres = cellData.getValue().getGenres();
            String genresString = genres.stream()
                    .filter(Objects::nonNull)
                    .map(Genre::getGenre)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(genresString.isEmpty() ? "N/A" : genresString);
        });
        genresColumn.setPrefWidth(150);


        // NEW: Cast Column
        TableColumn<Media, String> castColumn = new TableColumn<>("Cast");
        castColumn.setCellValueFactory(cellData -> {
            Media media = cellData.getValue();
            if (media instanceof Films) {
                Films film = (Films) media;
                return new SimpleStringProperty(film.getCast().isEmpty() ? "N/A" : String.join(", ", film.getCast()));
            } else if (media instanceof Show) {
                Show show = (Show) media;
                return new SimpleStringProperty(show.getCast().isEmpty() ? "N/A" : String.join(", ", show.getCast()));
            }
            return new SimpleStringProperty("N/A"); // Books don't have a 'cast' attribute
        });
        castColumn.setPrefWidth(150);

        // NEW: Author Column
        TableColumn<Media, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(cellData -> {
            Media media = cellData.getValue();
            if (media instanceof Book) {
                Book book = (Book) media;
                return new SimpleStringProperty(book.getAuthor());
            }
            return new SimpleStringProperty("N/A"); // Films/Shows don't have an 'author'
        });
        authorColumn.setPrefWidth(120);

        // NEW: Director Column
        TableColumn<Media, String> directorColumn = new TableColumn<>("Director");
        directorColumn.setCellValueFactory(cellData -> {
            Media media = cellData.getValue();
            if (media instanceof Films) {
                Films film = (Films) media;
                return new SimpleStringProperty(film.getDirection());
            }
            return new SimpleStringProperty("N/A"); // Books/Shows don't have a 'director'
        });
        directorColumn.setPrefWidth(120);


        // NEW: Details/Reviews Column (last column)
        TableColumn<Media, Void> detailsColumn = new TableColumn<>("Details");
        detailsColumn.setCellFactory(param -> new TableCell<Media, Void>() {
            private final Button detailsButton = new Button("View Info"); // Changed text to be more generic for full info

            {
                detailsButton.setOnAction(event -> {
                    Media media = getTableView().getItems().get(getIndex());
                    openReviewDetailsModal(media);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        });
        detailsColumn.setPrefWidth(100);

        // Add all columns to the TableView
        mediaTableView.getColumns().addAll(titleColumn, typeColumn, yearColumn, ratingColumn, genresColumn,
                castColumn, authorColumn, directorColumn, detailsColumn);
    }

    private void populateGenreCheckComboBox() {
        if (workManager != null) { // Defensive check
            List<String> genreNames = workManager.getGenres().stream()
                    .map(Genre::getGenre)
                    .collect(Collectors.toList());
            genreFilterCheckComboBox.getItems().setAll(genreNames);
        } else {
            System.err.println("WorkManager is null in populateGenreCheckComboBox. Cannot populate genres.");
        }
    }

    private void loadAllMedia() {
        if (workManager != null) { // Defensive check
            masterMediaList.setAll(workManager.listMediaAlphabetically());
            // mediaTableView.setItems(masterMediaList); // applyFiltersAndSort will set the items
        } else {
            System.err.println("WorkManager is null in loadAllMedia. Cannot load media.");
        }
    }

    @FXML
    private void handleApplyFilters() {
        applyFiltersAndSort();
    }

    @FXML
    private void handleClearFilters() {
        titleIsbnFilterField.clear();
        yearFilterField.clear();
        genreFilterCheckComboBox.getCheckModel().clearChecks();
        personFilterField.clear();
        filterBookCheckBox.setSelected(false);
        filterFilmCheckBox.setSelected(false);
        filterShowCheckBox.setSelected(false);
        sortTitleAscRadio.setSelected(true); // Default sort
        applyFiltersAndSort();
    }

    private void applyFiltersAndSort() {
        if (workManager == null) { // Defensive check
            System.err.println("WorkManager is null in applyFiltersAndSort. Cannot filter/sort.");
            filteredAndSortedMediaList.clear(); // Clear table if no data source
            return;
        }

        List<Media> currentList = workManager.listMediaAlphabetically();

        // 1. Filter by Title/ISBN
        String titleIsbnSearchTerm = titleIsbnFilterField.getText().trim().toLowerCase();
        if (!titleIsbnSearchTerm.isEmpty()) {
            currentList = currentList.stream()
                    .filter(media -> media.getTitle().toLowerCase().contains(titleIsbnSearchTerm) ||
                            (media instanceof Book && ((Book) media).getIsbn().toLowerCase().contains(titleIsbnSearchTerm)))
                    .collect(Collectors.toList());
        }

        // 2. Filter by Year
        String yearText = yearFilterField.getText().trim();
        if (!yearText.isEmpty()) {
            try {
                int year = Integer.parseInt(yearText);
                currentList = currentList.stream()
                        .filter(media -> media.getYearRelease() == year)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Release year must be a valid number.");
                yearFilterField.clear();
                return; // Stop processing if year is invalid
            }
        }

        // 3. Filter by Genre
        List<String> selectedGenreNames = genreFilterCheckComboBox.getCheckModel().getCheckedItems();
        if (!selectedGenreNames.isEmpty()) {
            currentList = currentList.stream()
                    .filter(media -> media.getGenres().stream()
                            .anyMatch(genre -> selectedGenreNames.contains(genre.getGenre())))
                    .collect(Collectors.toList());
        }

        // 4. Filter by Person (Author, Director, Cast)
        String personSearchTerm = personFilterField.getText().trim().toLowerCase();
        if (!personSearchTerm.isEmpty()) {
            currentList = currentList.stream()
                    .filter(media -> {
                        if (media instanceof Book) {
                            return ((Book) media).getAuthor().toLowerCase().contains(personSearchTerm);
                        } else if (media instanceof Films) {
                            Films film = (Films) media;
                            return film.getDirection().toLowerCase().contains(personSearchTerm) ||
                                    film.getCast().stream().anyMatch(castMember -> castMember.toLowerCase().contains(personSearchTerm));
                        } else if (media instanceof Show) {
                            Show show = (Show) media;
                            return show.getCast().stream().anyMatch(castMember -> castMember.toLowerCase().contains(personSearchTerm));
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }

        // 5. Filter by Media Type (checkboxes)
        boolean filterBook = filterBookCheckBox.isSelected();
        boolean filterFilm = filterFilmCheckBox.isSelected();
        boolean filterShow = filterShowCheckBox.isSelected();

        if (filterBook || filterFilm || filterShow) { // Only apply type filter if at least one checkbox is selected
            currentList = currentList.stream()
                    .filter(media -> (filterBook && media instanceof Book) ||
                            (filterFilm && media instanceof Films) ||
                            (filterShow && media instanceof Show))
                    .collect(Collectors.toList());
        }

        // 6. Sort the filtered list
        Comparator<Media> comparator;
        if (sortRatingDescRadio.isSelected()) {
            comparator = Comparator.comparingDouble(WorkManager::calculateAverage).reversed();
        } else if (sortRatingAscRadio.isSelected()) {
            comparator = Comparator.comparingDouble(WorkManager::calculateAverage);
        } else { // Default to sortTitleAscRadio
            comparator = Comparator.comparing(Media::getTitle, String.CASE_INSENSITIVE_ORDER);
        }
        currentList.sort(comparator);

        filteredAndSortedMediaList.setAll(currentList);
        mediaTableView.setItems(filteredAndSortedMediaList);

        if (filteredAndSortedMediaList.isEmpty()) {
            showAlert("No Results", "No media found with the selected filters and search criteria.");
        }
    }

    @FXML
    private void handleToggleSidebar() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), filterSidebar);
        double currentTranslateX = filterSidebar.getTranslateX();

        if (currentTranslateX == 0) { // Sidebar is open (positioned at the right edge, translateX 0)
            // Closes the sidebar (moves it to the right off-screen)
            transition.setToX(SIDEBAR_WIDTH);
            toggleSidebarButton.setText("<"); // Change button text to indicate opening (points left)
            AnchorPane.setRightAnchor(contentArea, 0.0); // Main content returns to full space
            AnchorPane.setRightAnchor(toggleSidebarButton, 10.0); // Button moves to the right (outside the sidebar)
        } else { // Sidebar is closed (positioned off-screen to the right, translateX = SIDEBAR_WIDTH)
            // Opens the sidebar (moves it to the left onto the screen)
            transition.setToX(0);
            toggleSidebarButton.setText(">"); // Change button text to indicate closing (points right)
            AnchorPane.setRightAnchor(contentArea, SIDEBAR_WIDTH); // Main content is pushed left by the sidebar
            AnchorPane.setRightAnchor(toggleSidebarButton, SIDEBAR_WIDTH + 10.0); // Button moves with the left edge of the sidebar
        }
        transition.play();
    }

    private void openReviewDetailsModal(Media media) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MediaDetailsView.fxml"));
            Parent parent = fxmlLoader.load();
            MediaDetailsController controller = fxmlLoader.getController();

            // Pass the selected media and the WorkManager instance to the new controller
            controller.setMediaAndWorkManager(media, this.workManager);

            Stage stage = new Stage();
            stage.setTitle("Details for " + media.getTitle());
            stage.initModality(Modality.APPLICATION_MODAL); // Makes it a modal window
            stage.setScene(new Scene(parent));
            stage.showAndWait(); // Show and wait until the modal is closed
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open details. Check console for details.");
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