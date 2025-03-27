package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the reviewer.
 */

public class ReviewersHomePage {
	
	private final DatabaseHelper databaseHelper;
		
	public ReviewersHomePage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label reviewerLabel = new Label("Hello, Reviewer!");
	    reviewerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // button to back out of reviewer home page and return to the welcome login page
	    Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
        });

	    layout.getChildren().addAll(reviewerLabel, backButton);
	    Scene reviewerScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(reviewerScene);
	    primaryStage.setTitle("User Page");
    	
    }
}