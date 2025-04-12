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

public class StudentReviewView {

    private final DatabaseHelper databaseHelper;
    private int answerId;

    public StudentReviewView(DatabaseHelper databaseHelper, int answerId) {
        this.databaseHelper = databaseHelper;
        this.answerId = answerId;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Label headerLabel = new Label("View Reviews for Answer ID: " + answerId);
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<ReviewItem> reviewTable = new TableView<>();
        reviewTable.setPlaceholder(new Label("No reviews found"));

        TableColumn<ReviewItem, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<ReviewItem, String> reviewerCol = new TableColumn<>("Reviewer");
        reviewerCol.setCellValueFactory(new PropertyValueFactory<>("reviewerName"));
        TableColumn<ReviewItem, String> textCol = new TableColumn<>("Review");
        textCol.setCellValueFactory(new PropertyValueFactory<>("feedbackText"));
        TableColumn<ReviewItem, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        reviewTable.getColumns().addAll(idCol, reviewerCol, textCol, timestampCol);

        ObservableList<ReviewItem> items = FXCollections.observableArrayList();
        try {
            List<String[]> reviews = databaseHelper.getFeedbackForQuestion(answerId, Integer.parseInt(user.getUserId()));
            for (String[] r : reviews) {
                items.add(new ReviewItem(r[0], r[1], r[2], r[3]));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        reviewTable.setItems(items);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new StudentSelectAnswer(databaseHelper, user).show(primaryStage));

        layout.getChildren().addAll(headerLabel, reviewTable, backButton);
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student - View Reviews");
    }

    public static class ReviewItem {
        private String id;
        private String reviewerName;
        private String feedbackText;
        private String timestamp;

        public ReviewItem(String id, String reviewerName, String feedbackText, String timestamp) {
            this.id = id;
            this.reviewerName = reviewerName;
            this.feedbackText = feedbackText;
            this.timestamp = timestamp;
        }

        public String getId() { return id; }
        public String getReviewerName() { return reviewerName; }
        public String getFeedbackText() { return feedbackText; }
        public String getTimestamp() { return timestamp; }
    }
}
