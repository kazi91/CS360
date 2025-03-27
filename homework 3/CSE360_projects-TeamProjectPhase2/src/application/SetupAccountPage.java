package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

import PasswordEvaluatorTestbed.src.passwordEvaluationTestbed.PasswordEvaluator;
import UserNameRecognizerConsoleTestbed.src.userNameRecognizerTestbed.UserNameRecognizer;
import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    // Labels to display error messages
    private Label label_UserNameErrorMessage = new Label(" "); // Initialized with empty text
    private Label label_PasswordErrorMessage = new Label(" "); // Initialized with empty text
    private String UsernamePermissionFlag = ""; // Flag to disallow setup completion until valid username is entered
    private String PasswordPermissionFlag = ""; // Flag to disallow setup completion until valid password is entered


    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
        // added email input field
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email Address");
        emailField.setMaxWidth(250);
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Button setupButton = new Button("Register");
        setupButton.setDisable(true); // Initially disabled
        
        // Add a listener to update the username error message dynamically
        userNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            String errorMessage = UserNameRecognizer.checkForValidUserName(newValue); // Call username recognizer
            // and pass each value to
            // evaluate semi-dynamically
            UsernamePermissionFlag = errorMessage;
            if (errorMessage.isEmpty()) {
                label_UserNameErrorMessage.setText("Valid Username!");
                label_UserNameErrorMessage.setStyle("-fx-text-fill: green;");
            } else {
                label_UserNameErrorMessage.setText(errorMessage);
                label_UserNameErrorMessage.setStyle("-fx-text-fill: red;");
            }
            // Update setup button state
            updateSetupButtonState(setupButton);
        });

        // Add a listener to update the password error message dynamically
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
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
            // Update setup button state
            updateSetupButtonState(setupButton);
        });
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String code = inviteCodeField.getText();
            String email = emailField.getText();
            
            try {
            	// Check if the user already exists
            	if(!databaseHelper.doesUserExist(userName)) {
            		
            		// Validate the invitation code
            		List<String> roles = databaseHelper.validateInvitationCode(code);
            		if(roles != null) {
            			
            			// Create a new user and register them in the database
		            	User user=new User(userName, password, email, "user", databaseHelper);
		                databaseHelper.register(user);
		                for (String eachRole : roles) {
                            databaseHelper.addUserRole(userName, eachRole);
                        }
		                
		             // Navigate to the Login Page
		                new UserLoginPage(databaseHelper).show(primaryStage);
            		}
            		else {
            			errorLabel.setText("Please enter a valid invitation code");
            		}
            	}
            	else {
            		errorLabel.setText("This useruserName is taken!!.. Please use another to setup an account");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        // Button to return to the initial SetupLoginSelectionPage
        Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, label_UserNameErrorMessage, passwordField, label_PasswordErrorMessage, emailField, inviteCodeField, setupButton, backButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
    
    /**
     * Updates the enabled state of the setup button based on the permission flags.
     *
     * @param setupButton The setup button to be updated.
     */
    private void updateSetupButtonState(Button setupButton) {
        // Button is enabled only when both flags are empty (indicating valid inputs)
        setupButton.setDisable(!UsernamePermissionFlag.isEmpty() || !PasswordPermissionFlag.isEmpty());
    }
}
