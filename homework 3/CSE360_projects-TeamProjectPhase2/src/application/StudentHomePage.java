package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * This page displays a simple welcome message for the student.
 */
public class StudentHomePage {

	private final DatabaseHelper databaseHelper;

	public StudentHomePage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage, User user) {
		VBox layout = new VBox();
		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		// Label to display Hello user
		Label studentLabel = new Label("Hello, Student!");
		studentLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		// Button to open Q&A system
		Button askQuestionButton = new Button("Ask a Question");
		askQuestionButton.setOnAction(a -> {
			QAView qaView = new QAView(databaseHelper, user);
			try {
				qaView.start(primaryStage, user); // Open the Q&A interface
			} catch (SQLException e) {
				e.printStackTrace(); // Handle any SQL errors
			}
		});

		// Button to back out of student home page and return to the welcome login page
		Button backButton = new Button("Back");
		backButton.setOnAction(a -> {
			new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
		});

		layout.getChildren().addAll(studentLabel, askQuestionButton, backButton);
		Scene studentScene = new Scene(layout, 800, 400);

		// Set the scene to primary stage
		primaryStage.setScene(studentScene);
		primaryStage.setTitle("Student Page");
	}
}
