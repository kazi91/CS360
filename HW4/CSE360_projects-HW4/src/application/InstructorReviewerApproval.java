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

public class InstructorReviewerApproval {
    private final DatabaseHelper databaseHelper;

    public InstructorReviewerApproval(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User instructor) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Label headerLabel = new Label("Approve Reviewer Requests");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<ReviewerRequest> requestTable = new TableView<>();
        requestTable.setPlaceholder(new Label("No pending requests"));

        TableColumn<ReviewerRequest, Integer> idCol = new TableColumn<>("Request ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<ReviewerRequest, String> studentNameCol = new TableColumn<>("Student Name");
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        TableColumn<ReviewerRequest, Integer> studentIdCol = new TableColumn<>("Student ID");
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        TableColumn<ReviewerRequest, String> textCol = new TableColumn<>("Request");
        textCol.setCellValueFactory(new PropertyValueFactory<>("requestText"));
        TableColumn<ReviewerRequest, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        requestTable.getColumns().addAll(idCol, studentNameCol, studentIdCol, textCol, statusCol);

        ObservableList<ReviewerRequest> items = FXCollections.observableArrayList();
        try {
            List<ReviewerRequest> requests = databaseHelper.getReviewerRequests();
            items.addAll(requests);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        requestTable.setItems(items);

        Button approveButton = new Button("Approve Selected Request");
        approveButton.setOnAction(e -> {
            ReviewerRequest selected = requestTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.ERROR, "No Request Selected", "Select a request to approve.");
                return;
            }
            try {
                databaseHelper.updateReviewerRequestStatus(selected.getId(), "approved");
                selected.setStatus("approved");
                requestTable.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Request approved.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not approve request.");
            }
        });

        Button rejectButton = new Button("Reject Selected Request");
        rejectButton.setOnAction(e -> {
            ReviewerRequest selected = requestTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.ERROR, "No Request Selected", "Select a request to reject.");
                return;
            }
            try {
                databaseHelper.updateReviewerRequestStatus(selected.getId(), "rejected");
                selected.setStatus("rejected");
                requestTable.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Request rejected.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not reject request.");
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new InstructorsHomePage(databaseHelper).show(primaryStage, instructor));

        layout.getChildren().addAll(headerLabel, requestTable, approveButton, rejectButton, backButton);
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Instructor - Reviewer Requests");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
