package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {

    private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show( Stage primaryStage, User user) {

        VBox layout = new VBox(5);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label welcomeLabel = new Label("Welcome!!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Button to navigate to the user's respective page based on their role
        Button continueButton = new Button("Continue to your Page");
        continueButton.setOnAction(a -> {
            String role =user.getRole();
            System.out.println(role);

            if(role.equals("admin")) {
                new AdminHomePage(this.databaseHelper).show(primaryStage, user);
            }
            else if(role.equals("user")) {
                new UserHomePage(this.databaseHelper).show(primaryStage, user);
            }
            else if(role.equals("student")) {
                new StudentHomePage(this.databaseHelper).show(primaryStage, user);
            }
            else if(role.equals("reviewer")) {
                new ReviewersHomePage(this.databaseHelper).show(primaryStage, user);
            }
            else if(role.equals("instructor")) {
                new InstructorsHomePage(this.databaseHelper).show(primaryStage, user);
            }
            else if(role.equals("staff")) {
                new StaffHomePage(this.databaseHelper).show(primaryStage, user);
            }
        });

        // Button to logout and return to the log in page
        Button logoutButton = new Button("Log Out");
        logoutButton.setOnAction(a -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

        // Button to quit the application
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(a -> {
            databaseHelper.closeConnection();
            Platform.exit(); // Exit the JavaFX application
        });

        // "Invite" button for admin to generate invitation codes
        if ("admin".equals(user.getRole())) {
            Button inviteButton = new Button("Invite");
            inviteButton.setOnAction(a -> {
                new InvitationPage().show(databaseHelper, primaryStage, user);
            });
            layout.getChildren().add(inviteButton);
        }

        layout.getChildren().addAll(welcomeLabel,continueButton,logoutButton,quitButton);
        Scene welcomeScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Welcome Page");
    }
}