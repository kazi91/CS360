package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentHomePage {

	private final DatabaseHelper databaseHelper;

	public StudentHomePage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage, User user) {
		VBox layout = new VBox(10);
		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		Label studentLabel = new Label("Hello, Student!");
		studentLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		Button askQuestionButton = new Button("Ask a Question");
		askQuestionButton.setOnAction(a -> {
			QAView qaView = new QAView(databaseHelper, user);
			try {
				qaView.start(primaryStage, user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Button viewReviewsButton = new Button("View Reviews for Answers");
		viewReviewsButton.setOnAction(e -> new StudentSelectAnswer(databaseHelper, user).show(primaryStage));

		Button dmButton = new Button("Direct Messages");
		dmButton.setOnAction(a -> {
			DMPage dmPage = new DMPage(databaseHelper, user);
			dmPage.show(primaryStage);
		});

		Button trustedReviewersButton = new Button("Manage Trusted Reviewers");
		trustedReviewersButton.setOnAction(e -> new TrustedReviewerManagement(databaseHelper).show(primaryStage, user));

		Button requestReviewerRoleButton = new Button("Request Reviewer Role");
		requestReviewerRoleButton.setOnAction(e -> new RequestReviewerRole(databaseHelper).show(primaryStage, user));

		Button backButton = new Button("Back");
		backButton.setOnAction(a -> new WelcomeLoginPage(databaseHelper).show(primaryStage, user));

		layout.getChildren().addAll(studentLabel, askQuestionButton, viewReviewsButton, dmButton, trustedReviewersButton, requestReviewerRoleButton, backButton);
		Scene studentScene = new Scene(layout, 800, 400);
		primaryStage.setScene(studentScene);
		primaryStage.setTitle("Student Home Page");
	}
}
