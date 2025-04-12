package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a welcome message and displays buttons to allow staff users 
 * the ability to view questions, answers, and private feedback.  
 */

public class StaffHomePage {
	
	private final DatabaseHelper databaseHelper;
		
	public StaffHomePage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label staffLabel = new Label("Hello, Staff!");
	    staffLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // button to view questions asked by students 
	    Button selectQuestionButton = new Button("View a List of Questions");
		selectQuestionButton.setOnAction(e -> new StaffSelectQuestion(databaseHelper, user).show(primaryStage));
		
		Button selectAnswerButton = new Button("View a list of Answers");
		selectAnswerButton.setOnAction(e -> new StaffSelectAnswer(databaseHelper, user).show(primaryStage));

		Button selectFeedbackButton = new Button("View Private Feedback From Students");
		selectFeedbackButton.setOnAction(e -> new StaffPrivateReview(databaseHelper).show(primaryStage, user));
 
		Button selectFlaggedPostButton = new Button("View list of flagged posts");
		selectFlaggedPostButton.setOnAction(e -> new StaffFlaggedPostReview(databaseHelper, user).show(primaryStage));
 
	    
	    // button to back out of staff home page and return to the welcome login page
	    Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
        });

	    layout.getChildren().addAll(staffLabel, selectQuestionButton, selectAnswerButton, selectFeedbackButton, backButton);
	    Scene staffScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(staffScene);
	    primaryStage.setTitle("User Page");
    	
    }
}