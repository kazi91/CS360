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
 * This page allows students to view and update the weight values for their trusted reviewers.
 */
public class TrustedReviewerManagement {
    private final DatabaseHelper databaseHelper;

    public TrustedReviewerManagement(DatabaseHelper databaseHelper) {
        if (databaseHelper == null) {
            throw new IllegalArgumentException("DatabaseHelper cannot be null");
        }
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Label headerLabel = new Label("Manage Trusted Reviewers");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<TrustedReviewer> table = new TableView<>();
        table.setPlaceholder(new Label("No trusted reviewers found"));

        TableColumn<TrustedReviewer, String> reviewerCol = new TableColumn<>("Reviewer");
        reviewerCol.setCellValueFactory(new PropertyValueFactory<>("reviewerName"));
        TableColumn<TrustedReviewer, Integer> weightCol = new TableColumn<>("Weight");
        weightCol.setCellValueFactory(new PropertyValueFactory<>("weight"));
        table.getColumns().addAll(reviewerCol, weightCol);

        ObservableList<TrustedReviewer> items = FXCollections.observableArrayList();
        try {
            int studentId = Integer.parseInt(user.getUserId());
            List<TrustedReviewer> list = databaseHelper.getTrustedReviewers(studentId);
            items.addAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table.setItems(items);

        TextField weightField = new TextField();
        weightField.setPromptText("Enter new weight");
        Button updateButton = new Button("Update Weight");
        updateButton.setOnAction(e -> {
            TrustedReviewer selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.ERROR, "No reviewer selected", "Select a reviewer to update.");
                return;
            }
            try {
                int newWeight = Integer.parseInt(weightField.getText());
                int studentId = Integer.parseInt(user.getUserId());
                databaseHelper.updateTrustedReviewerWeight(studentId, selected.getReviewerId(), newWeight);
                selected.setWeight(newWeight);
                table.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Weight updated successfully.");
            } catch (NumberFormatException | SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not update weight.");
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new StudentHomePage(databaseHelper).show(primaryStage, user));

        layout.getChildren().addAll(headerLabel, table, weightField, updateButton, backButton);
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student - Trusted Reviewers");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
