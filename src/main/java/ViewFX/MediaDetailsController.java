package ViewFX;

import Control.WorkManager;
import Module.Book;
import Module.Films;
import Module.Genre;
import Module.Media;
import Module.Review;
import Module.Show;
import Module.Season;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MediaDetailsController {

    @FXML private Label mediaTitleLabel;
    @FXML private Label originalTitleLabel;
    @FXML private Label typeLabel;
    @FXML private Label yearLabel;
    @FXML private Label ratingLabel;
    @FXML private Label seenLabel;
    @FXML private Label genresLabel;
    @FXML private Label castLabel;
    @FXML private Label whereWatchLabel;

    @FXML private VBox bookDetailsPane;
    @FXML private Label authorLabel;
    @FXML private Label publisherLabel;
    @FXML private Label isbnLabel;
    @FXML private Label copyLabel;

    @FXML private VBox filmDetailsPane;
    @FXML private Label directionLabel;
    @FXML private Label screenplayLabel;
    @FXML private Label runningTimeLabel;

    @FXML private VBox showDetailsPane;
    @FXML private Label yearEndLabel;
    @FXML private ListView<String> seasonsListView;


    @FXML private ListView<String> reviewsListView;
    @FXML private Label noReviewsLabel;

    private Media media; // The media object whose details are being displayed
    private WorkManager workManager; // WorkManager instance (can be used for calculations/data if needed)

    // Method to set the media and work manager
    public void setMediaAndWorkManager(Media media, WorkManager workManager) {
        this.media = media;
        this.workManager = workManager;
        updateView();
    }

    private void updateView() {
        if (media == null) {
            mediaTitleLabel.setText("Error: Media not set.");
            noReviewsLabel.setText("No media information available.");
            noReviewsLabel.setVisible(true);
            noReviewsLabel.setManaged(true);
            return;
        }

        // Common Media Details
        mediaTitleLabel.setText(media.getTitle());
        typeLabel.setText(media.getClass().getSimpleName().replace("s", "")); // e.g., Films -> Film
        yearLabel.setText(String.valueOf(media.getYearRelease()));
        ratingLabel.setText(String.format("%.1f ★", WorkManager.calculateAverage(media)));
        seenLabel.setText(media.isSeen() ? "Yes" : "No");
        genresLabel.setText(media.getGenres().stream()
                .map(Genre::getGenre)
                .collect(Collectors.joining(", ")));

        // Reset visibility of specific panes
        bookDetailsPane.setVisible(false);
        bookDetailsPane.setManaged(false);
        filmDetailsPane.setVisible(false);
        filmDetailsPane.setManaged(false);
        showDetailsPane.setVisible(false);
        showDetailsPane.setManaged(false);
        originalTitleLabel.setVisible(false);
        originalTitleLabel.setManaged(false);
        castLabel.setText("N/A"); // Reset default for cast
        whereWatchLabel.setText("N/A"); // Reset default for where to watch


        // Specific Media Type Details
        if (media instanceof Book) {
            Book book = (Book) media;
            bookDetailsPane.setVisible(true);
            bookDetailsPane.setManaged(true);
            authorLabel.setText("Author: " + book.getAuthor());
            publisherLabel.setText("Publisher: " + (book.getPublisher().isEmpty() ? "N/A" : book.getPublisher()));
            isbnLabel.setText("ISBN: " + book.getIsbn());
            copyLabel.setText("Owned Copy: " + (book.getCopy() ? "Yes" : "No"));
            displayReviews(media.getReviews());

        } else if (media instanceof Films) {
            Films film = (Films) media;
            filmDetailsPane.setVisible(true);
            filmDetailsPane.setManaged(true);
            if (!film.getTitle().equalsIgnoreCase(film.getOriginalTitle())) {
                originalTitleLabel.setText("Original Title: " + film.getOriginalTitle());
                originalTitleLabel.setVisible(true);
                originalTitleLabel.setManaged(true);
            }
            directionLabel.setText("Direction: " + (film.getDirection().isEmpty() ? "N/A" : film.getDirection()));
            screenplayLabel.setText("Screenplay: " + (film.getScreenplay().isEmpty() ? "N/A" : film.getScreenplay()));
            runningTimeLabel.setText("Duration: " + film.getRunningtime() + " min");
            castLabel.setText(film.getCast().isEmpty() ? "N/A" : String.join(", ", film.getCast()));
            whereWatchLabel.setText(film.getWhereWatch().isEmpty() ? "N/A" : String.join(", ", film.getWhereWatch()));
            displayReviews(media.getReviews());

        } else if (media instanceof Show) {
            Show show = (Show) media;
            showDetailsPane.setVisible(true);
            showDetailsPane.setManaged(true);
            if (!show.getTitle().equalsIgnoreCase(show.getOriginalTitle())) {
                originalTitleLabel.setText("Original Title: " + show.getOriginalTitle());
                originalTitleLabel.setVisible(true);
                originalTitleLabel.setManaged(true);
            }
            yearEndLabel.setText("End Year: " + (show.getYearEnd() > 0 ? String.valueOf(show.getYearEnd()) : "Ongoing"));
            castLabel.setText(show.getCast().isEmpty() ? "N/A" : String.join(", ", show.getCast()));
            whereWatchLabel.setText(show.getWhereWatch().isEmpty() ? "N/A" : String.join(", ", show.getWhereWatch()));

            // Display Seasons and their nested reviews
            ObservableList<String> seasonInfoList = FXCollections.observableArrayList();
            List<Review> allShowReviews = new ArrayList<>();

            if (show.getSeasons().isEmpty()) {
                seasonInfoList.add("No seasons registered.");
            } else {
                for (Season season : show.getSeasons()) {
                    seasonInfoList.add("Season " + season.getSeasonNumber() + " (Episodes: " + season.getEpisodeCount() + ", Release: " + season.getReleaseDate() + ")");
                    // Collect reviews for display in the main review list
                    if (!season.getReviews().isEmpty()) {
                        allShowReviews.addAll(season.getReviews());
                    }
                }
            }
            seasonsListView.setItems(seasonInfoList);
            displayReviews(allShowReviews); // Display all collected reviews from seasons

        } else {
            // Unknown Media Type
            typeLabel.setText("Unknown");
            displayReviews(media.getReviews());
        }
    }

    private void displayReviews(List<Review> reviews) {
        ObservableList<String> reviewStrings = FXCollections.observableArrayList();
        if (reviews.isEmpty()) {
            noReviewsLabel.setVisible(true);
            noReviewsLabel.setManaged(true);
            reviewsListView.setVisible(false);
            reviewsListView.setManaged(false);
        } else {
            for (Review review : reviews) {
                reviewStrings.add(review.toString());
            }
            reviewsListView.setItems(reviewStrings);
            noReviewsLabel.setVisible(false);
            noReviewsLabel.setManaged(false);
            reviewsListView.setVisible(true);
            reviewsListView.setManaged(true);
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) mediaTitleLabel.getScene().getWindow();
        stage.close();
    }
}