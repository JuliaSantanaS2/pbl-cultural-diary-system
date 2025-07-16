package View;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddMediaView extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f7fa;"); // Light background color

        // Left Menu (Simplified for this example as it's not the main focus of the image)
        VBox leftMenu = new VBox(15);
        leftMenu.setPrefWidth(180);
        leftMenu.setPadding(new Insets(20, 10, 20, 10));
        leftMenu.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label userName = new Label("Sophia");
        userName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label userHandle = new Label("@sophia.miller");
        userHandle.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");

        VBox userInfo = new VBox(5, userName, userHandle);
        userInfo.setAlignment(Pos.CENTER_LEFT);
        userInfo.setPadding(new Insets(0, 0, 20, 0)); // Padding below user info

        leftMenu.getChildren().addAll(userInfo,
                createMenuItem("Home", "home-icon.png"), // Placeholder for actual icon
                createMenuItem("Search", "search-icon.png"),
                createSelectedMenuItem("My List", "list-icon.png"), // Highlighted item
                createMenuItem("Settings", "settings-icon.png")
        );
        root.setLeft(leftMenu);

        // Center Content Area
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(0, 0, 0, 30)); // Padding from left menu

        Label titleLabel = new Label("Add New Media");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #343a40;");
        centerContent.getChildren().add(titleLabel);

        // TabPane for Book, Movie, Series
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Prevent tabs from being closed
        tabPane.setStyle("-fx-background-color: transparent;"); // Make TabPane itself transparent

        // Styling for tabs to match the image's segmented control look
        tabPane.getStyleClass().add("floating-tabs");

        // Book Tab Content
        Tab bookTab = new Tab("Book");
        VBox bookContent = new VBox(15);
        bookContent.setPadding(new Insets(20, 0, 0, 0));

        TextField titleField = createStyledTextField("Title");
        TextField originalTitleField = createStyledTextField("Original Title");
        TextField authorField = createStyledTextField("Author");
        TextField publisherField = createStyledTextField("Publisher");
        TextField isbnField = createStyledTextField("ISBN");
        TextField releaseYearField = createStyledTextField("Release Year"); // Changed to TextField for simplicity in UI
        TextField genresField = createStyledTextField("Genres"); // Simplified for now, complex UI for genres would be a separate component

        // Toggle buttons for "Seen" and "Physical Copy"
        HBox seenBox = new HBox(10);
        seenBox.setAlignment(Pos.CENTER_LEFT);
        Label seenLabel = new Label("Seen");
        seenLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");
        ToggleButton seenToggle = createToggleSwitch();
        seenBox.getChildren().addAll(seenLabel, seenToggle);

        HBox physicalCopyBox = new HBox(10);
        physicalCopyBox.setAlignment(Pos.CENTER_LEFT);
        Label physicalCopyLabel = new Label("Physical Copy");
        physicalCopyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");
        ToggleButton physicalCopyToggle = createToggleSwitch();
        physicalCopyBox.getChildren().addAll(physicalCopyLabel, physicalCopyToggle);

        bookContent.getChildren().addAll(
                titleField,
                originalTitleField,
                authorField,
                publisherField,
                isbnField,
                releaseYearField,
                genresField,
                seenBox,
                physicalCopyBox
        );
        bookTab.setContent(bookContent);

        // Movie Tab (Placeholder)
        Tab movieTab = new Tab("Movie");
        VBox movieContent = new VBox();
        movieContent.getChildren().add(new Label("Movie details will go here."));
        movieTab.setContent(movieContent);

        // Series Tab (Placeholder)
        Tab seriesTab = new Tab("Series");
        VBox seriesContent = new VBox();
        seriesContent.getChildren().add(new Label("Series details will go here."));
        seriesTab.setContent(seriesContent);

        tabPane.getTabs().addAll(bookTab, movieTab, seriesTab);
        centerContent.getChildren().add(tabPane);

        // Save Media Button
        Button saveMediaButton = new Button("Save Media");
        saveMediaButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 5;");
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.getChildren().add(saveMediaButton);
        centerContent.getChildren().add(buttonBox);


        root.setCenter(centerContent);

        Scene scene = new Scene(root, 800, 700); // Increased height to better fit elements
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); // Link external CSS

        primaryStage.setTitle("Cultural Diary - Add New Media");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper method to create menu items
    private VBox createMenuItem(String text, String iconPath) {
        VBox item = new VBox(5);
        item.setAlignment(Pos.CENTER_LEFT);
        // ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath))); // For actual icons
        // icon.setFitWidth(20); icon.setFitHeight(20);
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");
        // item.getChildren().addAll(icon, label); // Uncomment if using actual icons
        item.getChildren().add(label);
        item.setPadding(new Insets(10, 15, 10, 15));
        item.setOnMouseClicked(e -> System.out.println(text + " clicked")); // Simple click handler
        return item;
    }

    // Helper method for the selected menu item style
    private VBox createSelectedMenuItem(String text, String iconPath) {
        VBox item = createMenuItem(text, iconPath);
        item.setStyle("-fx-background-color: #e9ecef; -fx-background-radius: 5;");
        item.getChildren().forEach(node -> {
            if (node instanceof Label) {
                ((Label) node).setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #343a40;");
            }
        });
        return item;
    }

    // Helper method to create styled TextField
    private TextField createStyledTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setPrefHeight(40);
        textField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ced4da; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10; -fx-font-size: 14px;");
        return textField;
    }

    // Helper method to create a simple toggle switch (visual only, no functionality)
    private ToggleButton createToggleSwitch() {
        ToggleButton toggle = new ToggleButton();
        toggle.setPrefSize(40, 20); // Width, Height
        toggle.setStyle("-fx-background-color: #adb5bd; -fx-background-radius: 20;");
        toggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                toggle.setStyle("-fx-background-color: #007bff; -fx-background-radius: 20;");
            } else {
                toggle.setStyle("-fx-background-color: #adb5bd; -fx-background-radius: 20;");
            }
        });

        // Create a fake circle for the toggle thumb
        VBox thumb = new VBox();
        thumb.setPrefSize(16, 16);
        thumb.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        thumb.setAlignment(Pos.CENTER);

        toggle.setGraphic(thumb);
        toggle.setAlignment(Pos.CENTER_LEFT); // Align thumb to left initially

        // Move thumb on toggle
        toggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                toggle.setAlignment(Pos.CENTER_RIGHT);
            } else {
                toggle.setAlignment(Pos.CENTER_LEFT);
            }
        });

        return toggle;
    }

    public static void main(String[] args) {
        launch(args);
    }
}