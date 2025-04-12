package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InstructorsHomePage {

    private final DatabaseHelper databaseHelper;

    public InstructorsHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label instructorLabel = new Label("Hello, Instructor!");
        instructorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button approveReviewerRequestsButton = new Button("Approve Reviewer Requests");
        approveReviewerRequestsButton.setOnAction(e -> new InstructorReviewerApproval(databaseHelper).show(primaryStage, user));

        Button backButton = new Button("Back");
        backButton.setOnAction(a -> new WelcomeLoginPage(databaseHelper).show(primaryStage, user));

        Button askQuestionButton = new Button("View Questions");
        askQuestionButton.setOnAction(a -> {
            QAView qaView = new QAView(databaseHelper, user);
            try {
                qaView.start(primaryStage, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button dmButton = new Button("Direct Messages");
        dmButton.setOnAction(a -> {
            DMPage dmPage = new DMPage(databaseHelper, user);
            dmPage.show(primaryStage);
        });

        layout.getChildren().addAll(instructorLabel, approveReviewerRequestsButton, askQuestionButton, dmButton, backButton);
        Scene instructorScene = new Scene(layout, 800, 400);
        primaryStage.setScene(instructorScene);
        primaryStage.setTitle("Instructor Home Page");
    }
}
