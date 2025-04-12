package application;

import databasePart1.DatabaseHelper;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.Optional;

public class ReviewerFeedbackDialog {
    public static void show(Stage owner, DatabaseHelper databaseHelper, User user, String questionId) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Provide Feedback");
        dialog.setHeaderText("Provide feedback for Question ID: " + questionId);
        dialog.setContentText("Enter your feedback:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String feedbackText = result.get().trim();
            if (feedbackText.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Feedback cannot be empty.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            try {
                int qid = Integer.parseInt(questionId);
                int reviewerId = Integer.parseInt(user.getUserId());
                databaseHelper.provideFeedback(qid, reviewerId, feedbackText);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Feedback submitted successfully!", ButtonType.OK);
                alert.showAndWait();
            } catch (NumberFormatException | SQLException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error submitting feedback: " + ex.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        }
    }
}
