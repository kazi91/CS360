package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

/**
 * This page allows reviewers to view, update, and delete the feedback (reviews)
 * they have submitted.
 */
public class ReviewerReviewManagement {
    private final DatabaseHelper databaseHelper;

    public ReviewerReviewManagement(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Label headerLabel = new Label("Manage Your Reviews");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<FeedbackItem> reviewTable = new TableView<>();
        reviewTable.setPlaceholder(new Label("No reviews found"));

        TableColumn<FeedbackItem, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<FeedbackItem, String> questionCol = new TableColumn<>("Question ID");
        questionCol.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        TableColumn<FeedbackItem, String> textCol = new TableColumn<>("Feedback");
        textCol.setCellValueFactory(new PropertyValueFactory<>("feedbackText"));
        TableColumn<FeedbackItem, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        reviewTable.getColumns().addAll(idCol, questionCol, textCol, timestampCol);

        ObservableList<FeedbackItem> items = FXCollections.observableArrayList();
        try {
            int reviewerId = Integer.parseInt(user.getUserId());
            List<String[]> feedbackList = databaseHelper.getFeedbackByReviewer(reviewerId);
            for (String[] r : feedbackList) {
                items.add(new FeedbackItem(r[0], r[1], r[2], r[3]));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        reviewTable.setItems(items);

        Button editButton = new Button("Edit Selected Review");
        editButton.setOnAction(e -> {
            FeedbackItem selected = reviewTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.ERROR, "No review selected", "Please select a review to edit.");
                return;
            }
            TextInputDialog dialog = new TextInputDialog(selected.getFeedbackText());
            dialog.setTitle("Edit Review");
            dialog.setHeaderText("Editing Review ID: " + selected.getId());
            dialog.setContentText("Enter new feedback text:");
            dialog.showAndWait().ifPresent(newText -> {
                if (newText.trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Feedback cannot be empty.");
                    return;
                }
                try {
                    int reviewerId = Integer.parseInt(user.getUserId());
                    databaseHelper.updateFeedback(Integer.parseInt(selected.getId()), reviewerId, newText);
                    selected.setFeedbackText(newText);
                    reviewTable.refresh();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Review updated successfully.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not update review.");
                }
            });
        });

        Button deleteButton = new Button("Delete Selected Review");
        deleteButton.setOnAction(e -> {
            FeedbackItem selected = reviewTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.ERROR, "No review selected", "Please select a review to delete.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this review?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        int reviewerId = Integer.parseInt(user.getUserId());
                        databaseHelper.deleteFeedback(Integer.parseInt(selected.getId()), reviewerId);
                        items.remove(selected);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Review deleted successfully.");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Could not delete review.");
                    }
                }
            });
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new ReviewersHomePage(databaseHelper).show(primaryStage, user));

        VBox buttonBox = new VBox(10, editButton, deleteButton);
        layout.getChildren().addAll(headerLabel, reviewTable, buttonBox, backButton);
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reviewer - Manage Reviews");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message);
        alert.setTitle(title);
        alert.showAndWait();
    }

    // Inner class representing a feedback item for the table view.
    public static class FeedbackItem {
        private String id;
        private String questionId;
        private String feedbackText;
        private String timestamp;

        public FeedbackItem(String id, String questionId, String feedbackText, String timestamp) {
            this.id = id;
            this.questionId = questionId;
            this.feedbackText = feedbackText;
            this.timestamp = timestamp;
        }

        public String getId() { return id; }
        public String getQuestionId() { return questionId; }
        public String getFeedbackText() { return feedbackText; }
        public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }
        public String getTimestamp() { return timestamp; }
    }
}
