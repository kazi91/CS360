package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the instructor.
 */

public class InstructorsHomePage {

    private final DatabaseHelper databaseHelper;

    public InstructorsHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox();
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to display Hello user
        Label instructorLabel = new Label("Hello, Instructor!");
        instructorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // button to back out of instructor home page and return to the welcome login page
        Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
        });

        layout.getChildren().addAll(instructorLabel, backButton);
        Scene instructorScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(instructorScene);
        primaryStage.setTitle("User Page");

    }
}