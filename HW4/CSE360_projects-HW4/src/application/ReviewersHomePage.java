package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReviewersHomePage {

	private final DatabaseHelper databaseHelper;

	public ReviewersHomePage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage, User user) {
		VBox layout = new VBox(10);
		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		Label reviewerLabel = new Label("Hello, Reviewer!");
		reviewerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		Button selectQuestionButton = new Button("Select a Question to Provide Feedback");
		selectQuestionButton.setOnAction(e -> new ReviewerSelectQuestion(databaseHelper, user).show(primaryStage));
		
		Button selectAnswerButton = new Button("Select a Answer to Provide Feedback");
		selectAnswerButton.setOnAction(e -> new ReviewerSelectQuestion(databaseHelper, user).show(primaryStage));


		Button manageReviewsButton = new Button("Manage My Reviews");
		manageReviewsButton.setOnAction(e -> new ReviewerReviewManagement(databaseHelper).show(primaryStage, user));

		Button dmButton = new Button("Direct Messages");
		dmButton.setOnAction(a -> {
			DMPage dmPage = new DMPage(databaseHelper, user);
			dmPage.show(primaryStage);
		});

		Button backButton = new Button("Back");
		backButton.setOnAction(a -> new WelcomeLoginPage(databaseHelper).show(primaryStage, user));

		layout.getChildren().addAll(reviewerLabel, selectQuestionButton, manageReviewsButton, dmButton, backButton);
		Scene reviewerScene = new Scene(layout, 800, 400);
		primaryStage.setScene(reviewerScene);
		primaryStage.setTitle("Reviewer Home Page");
	}
}
