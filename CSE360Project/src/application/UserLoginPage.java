package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {

    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        // Input fields for the user's username and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button loginButton = new Button("Login");

        loginButton.setOnAction(a -> {
            // Retrieve user inputs
            String userName = userNameField.getText();
            String password = passwordField.getText();

            try {
                // Create a user instance without a role for login validation
                User user = new User(userName, password, userName, "", databaseHelper);

                // Retrieve roles assigned to the user
                List<String> roles = databaseHelper.getUserRoles(userName);

                if (!roles.isEmpty()) {
                    // Check if the user's credentials are valid
                    boolean isValidLogin = databaseHelper.login(user);

                    if (isValidLogin) {

                        // OTP check
                        if (databaseHelper.isUsingOTP(userName)) {
                            System.out.println("OTP Login");
                            // redirect user to otp password reset page
                            new OTPPasswordResetPage(databaseHelper).show(primaryStage, userName);
                            return;
                        }

                        // Handle users with multiple roles
                        if (roles.size() > 1) {
                            // Display a dialog for the user to select a role
                            ChoiceDialog<String> roleDialog = new ChoiceDialog<>(roles.get(0), roles);
                            roleDialog.setTitle("Select Role");
                            roleDialog.setHeaderText("Multiple roles detected");
                            roleDialog.setContentText("Please select a role:");

                            roleDialog.showAndWait().ifPresent(selectedRole -> {
                                user.setRole(selectedRole);
                                WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
                                welcomeLoginPage.show(primaryStage, user);
                            });
                        } else {
                            // Automatically assign the single role
                            user.setRole(roles.get(0));
                            WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
                            welcomeLoginPage.show(primaryStage, user);
                        }
                    } else {
                        // Display an error if the login fails
                        errorLabel.setText("Invalid username or password.");
                    }
                } else {
                    // Display an error if the account does not exist or has no roles
                    errorLabel.setText("No account found or roles are not assigned.");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}
