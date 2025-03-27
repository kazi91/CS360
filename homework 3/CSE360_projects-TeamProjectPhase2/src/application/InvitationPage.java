package application;


import java.util.ArrayList;
import java.util.List;

import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class InvitationPage {

	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
    public void show(DatabaseHelper databaseHelper,Stage primaryStage, User user) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display the title of the page
	    Label userLabel = new Label("Invite ");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // checkbox for each role
    	CheckBox adminCheckBox = new CheckBox("admin");
    	CheckBox userCheckBox = new CheckBox("user");
    	CheckBox studentCheckBox = new CheckBox("student");
    	CheckBox reviewerCheckBox = new CheckBox("reviewer");
    	CheckBox instructorCheckBox = new CheckBox("instructor");
    	CheckBox staffCheckBox = new CheckBox("staff");
	    
	    // Button to generate the invitation code
	    Button showCodeButton = new Button("Generate Invitation Code");
	    
	    // Label to display the generated invitation code
	    Label inviteCodeLabel = new Label(""); ;
        inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        
        showCodeButton.setOnAction(a -> {
        	List<String> newUserRoles = new ArrayList<>();
    		
    		if (adminCheckBox.isSelected()) {
    			newUserRoles.add("admin");
    		}
    		if (userCheckBox.isSelected()) {
    			newUserRoles.add("user");
    		}
    		if (studentCheckBox.isSelected()) {
    			newUserRoles.add("student");
    		}
    		if (reviewerCheckBox.isSelected()) {
    			newUserRoles.add("reviewer");
    		}
    		if (instructorCheckBox.isSelected()) {
    			newUserRoles.add("instructor");
    		}
    		if (staffCheckBox.isSelected()) {
    			newUserRoles.add("staff");
    		}
    		
    		if (newUserRoles.isEmpty()) {
                inviteCodeLabel.setText("Please select at least one role.");
                return;
            }

            // Generate the invitation code using the databaseHelper and set it to the label
            String invitationCode = databaseHelper.generateInvitationCode(newUserRoles);
            inviteCodeLabel.setText(invitationCode);
        });
        
        // button to back out of admin invite page and return to the welcome login page
	    Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
        });

        layout.getChildren().addAll(userLabel, adminCheckBox, userCheckBox, studentCheckBox, reviewerCheckBox, instructorCheckBox, staffCheckBox, showCodeButton, inviteCodeLabel, backButton);
	    Scene inviteScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(inviteScene);
	    primaryStage.setTitle("Invite Page");
    	
    }
}