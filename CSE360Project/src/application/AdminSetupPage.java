package application;

import PasswordEvaluatorTestbed.src.passwordEvaluationTestbed.PasswordEvaluator; // Importing the password evaluation package
import UserNameRecognizerConsoleTestbed.src.userNameRecognizerTestbed.UserNameRecognizer; // Importing the username recognizer package

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator
 * account. This is intended to be used by the first user to initialize the
 * system with admin credentials.
 */
public class AdminSetupPage {

    private final DatabaseHelper databaseHelper;

    // Labels to display error messages
    private Label label_UserNameErrorMessage = new Label(" "); // Initialized with empty text
    private Label label_PasswordErrorMessage = new Label(" "); // Initialized with empty text
    private String UsernamePermissionFlag = ""; // Flag to disallow setup completion until valid username is entered
    private String PasswordPermissionFlag = ""; // Flag to disallow setup completion until valid password is entered

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        // Input fields for username and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin Username");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        // added email input field
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email Address");
        emailField.setMaxWidth(250);

        // Setup button to handle admin setup
        Button setupButton = new Button("Setup");
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
            String email = emailField.getText();

            try {
                // Create a new User object with admin role and register in the database
                User user = new User(userName, password, email, "admin", databaseHelper);
                databaseHelper.register(user);
                databaseHelper.addUserRole(userName, "admin");
                System.out.println("Administrator setup completed.");

                // Navigate to the Welcome Login Page
                new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // Layout for the admin setup page
        VBox layout = new VBox(10, userNameField, label_UserNameErrorMessage, passwordField, label_PasswordErrorMessage, emailField,
                setupButton); // make sure respective error labels are added to page properly to display error
        // information
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
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
