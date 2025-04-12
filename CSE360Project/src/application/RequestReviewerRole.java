package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page allows students to request the reviewer role.
 */
public class RequestReviewerRole {
    private final DatabaseHelper databaseHelper;

    public RequestReviewerRole(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Label headerLabel = new Label("Request Reviewer Role");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextArea requestArea = new TextArea();
        requestArea.setPromptText("Enter your reasons for becoming a reviewer");
        requestArea.setPrefRowCount(3);

        Button submitButton = new Button("Submit Request");
        submitButton.setOnAction(e -> {
            String requestText = requestArea.getText();
            if (requestText.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Request cannot be empty.");
                return;
            }
            try {
                int studentId = Integer.parseInt(user.getUserId());
                databaseHelper.createReviewerRequest(studentId, requestText);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reviewer request submitted.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not submit request.");
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new StudentHomePage(databaseHelper).show(primaryStage, user));

        layout.getChildren().addAll(headerLabel, requestArea, submitButton, backButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student - Request Reviewer Role");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
