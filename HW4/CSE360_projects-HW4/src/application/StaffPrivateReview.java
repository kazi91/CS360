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

import application.ReviewerReviewManagement.FeedbackItem;

/**
 * This page allows Staff to view private reviews made by reviewers
 */

public class StaffPrivateReview {
	
	 private final DatabaseHelper databaseHelper;
	 
	 public StaffPrivateReview(DatabaseHelper databaseHelper) {
	        this.databaseHelper = databaseHelper;
	 }
	 
	 public void show(Stage primaryStage, User user) {
	        VBox layout = new VBox(10);
	        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
	        Label headerLabel = new Label("Reviews");
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
	 
	        Button backButton = new Button("Back");
	        backButton.setOnAction(e -> new ReviewersHomePage(databaseHelper).show(primaryStage, user));
	        
	        layout.getChildren().addAll(headerLabel, reviewTable, backButton);
	        Scene scene = new Scene(layout, 800, 600);
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("Reviewer - Manage Reviews");
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
