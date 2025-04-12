package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import PasswordEvaluatorTestbed.src.passwordEvaluationTestbed.PasswordEvaluator;
import databasePart1.*;

/**
 * OTPPasswordResetPage class handles the otp password reset process for users.
 */
public class OTPPasswordResetPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public OTPPasswordResetPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    // Labels to display error messages
    private Label label_PasswordErrorMessage = new Label(" "); // Initialized with empty text
    private String PasswordPermissionFlag = ""; // Flag to disallow setup completion until valid password is entered


    /**
     * Displays the OTP password reset page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage, String userName) {
    	Label newPasswordLabel = new Label("Enter New Password:");
    	
    	// Input field for new password
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter New Password");
        newPasswordField.setMaxWidth(250);
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Button resetButton = new Button("Reset");
        resetButton.setDisable(true); // Initially disabled

        // Add a listener to update the new password error message dynamically
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            String errorMessage = PasswordEvaluator.evaluatePassword(newValue); // Call password evaluator
            // and pass each value to evaluate
            // semi-dynamically
            PasswordPermissionFlag = errorMessage;
            if (errorMessage.isEmpty()) {
                label_PasswordErrorMessage.setText("Valid Password!");
                label_PasswordErrorMessage.setStyle("-fx-text-fill: green;");
            } else {
                label_PasswordErrorMessage.setText(errorMessage);
                label_PasswordErrorMessage.setStyle("-fx-text-fill: red;");
            }
            // Update change button state
            updateResetButtonState(resetButton);
        });
        
        resetButton.setOnAction(a -> {
        	// Retrieve user input
            String newPassword = newPasswordField.getText();
            
            // update password
            try {
				databaseHelper.updateUserPassword(userName, newPassword);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            // return to login page
            new UserLoginPage(databaseHelper).show(primaryStage);
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(newPasswordLabel, newPasswordField, label_PasswordErrorMessage, resetButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
    
    /**
     * Updates the enabled state of the setup button based on the permission flags.
     *
     * @param setupButton The setup button to be updated.
     */
    private void updateResetButtonState(Button resetButton) {
        // Button is enabled only when both flags are empty (indicating valid inputs)
        resetButton.setDisable(!PasswordPermissionFlag.isEmpty());
    }
}
