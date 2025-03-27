package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
    /**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */

    private final DatabaseHelper databaseHelper;
    private String adminUserName;

    public AdminHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        adminUserName = user.getUserName();
        VBox layout = new VBox();

        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // label to display the welcome message for the admin
        Label adminLabel = new Label("Hello, Admin!");

        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        List<String[]> usersList = this.databaseHelper.listUsers();

        // Table to display list of all user accounts for admin to perform administrative functions
        TableView<User> databaseTable = new TableView<User>();

        // Table column for userId
        TableColumn<User, String> idColumn = new TableColumn<User, String>("id");
        idColumn.setCellValueFactory(new PropertyValueFactory<User, String>("userId"));

        // Table column for userName
        TableColumn<User, String> userNameColumn = new TableColumn<User, String>("Username");
        userNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("userName"));

        // Table column for email
        TableColumn<User, String> emailColumn = new TableColumn<User, String>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));

        // Table column for roles
        TableColumn<User, String> rolesColumn = new TableColumn<User, String>("Roles");
        rolesColumn.setCellValueFactory(new PropertyValueFactory<User, String>("role"));

        databaseTable.getColumns().add(idColumn);
        databaseTable.getColumns().add(userNameColumn);
        databaseTable.getColumns().add(emailColumn);
        databaseTable.getColumns().add(rolesColumn);

        for (String[] eachUser : usersList) {
            databaseTable.getItems().add(new User(eachUser[0], eachUser[1], eachUser[2], eachUser[3], eachUser[4]));
        }

        // Button to the edit the roles of the user that is selected in the table
        Button editUserRolesButton = new Button("Edit User Roles");
        editUserRolesButton.disableProperty().bind(databaseTable.getSelectionModel().selectedItemProperty().isNull());
        editUserRolesButton.setOnAction(a -> {
            User selectedUser = databaseTable.getSelectionModel().getSelectedItem();
            editRolesWindow(selectedUser);

            // refresh table after editing roles
            List<String[]> newUsersList = this.databaseHelper.listUsers();
            databaseTable.getItems().clear();
            for (String[] eachUser : newUsersList) {
                databaseTable.getItems().add(new User(eachUser[0], eachUser[1], eachUser[2], eachUser[3], eachUser[4]));
            }
        });

        // Button to delete the user that is selected in the table
        Button deleteUserButton = new Button("Delete User");
        deleteUserButton.disableProperty().bind(databaseTable.getSelectionModel().selectedItemProperty().isNull());
        deleteUserButton.setOnAction(a -> {
            User selectedUser = databaseTable.getSelectionModel().getSelectedItem();
            String selectedUserName = selectedUser.getUserName();

            // Prevents deletion of an admin
            List<String> selectedUserRoles = databaseHelper.getUserRoles(selectedUserName);
            if (selectedUserRoles.contains("admin")) {
                Alert adminDeletionAlert = new Alert(Alert.AlertType.ERROR);
                adminDeletionAlert.setTitle("Delete Error");
                adminDeletionAlert.setHeaderText("Delete " + selectedUserName);
                adminDeletionAlert.setContentText("Cannot delete user with an admin role.");
                adminDeletionAlert.showAndWait();
                return;
            }

            // Confirming if the admin actually wants to delete the user
            Alert deleteUserConfirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            deleteUserConfirmationAlert.setTitle("Delete Confirmation");
            deleteUserConfirmationAlert.setHeaderText("Delete " + selectedUserName);
            deleteUserConfirmationAlert.setContentText("Are you sure?");

            Optional<ButtonType> result = deleteUserConfirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                databaseHelper.deleteUser(selectedUserName);

                // refresh table after deleting user
                List<String[]> newUsersList = this.databaseHelper.listUsers();
                databaseTable.getItems().clear();
                for (String[] eachUser : newUsersList) {
                    databaseTable.getItems().add(new User(eachUser[0], eachUser[1], eachUser[2], eachUser[3], eachUser[4]));
                }
            }
        });

        // Button to generate a one time password for the selected user that has forgotten their password
        Button generateOTPButton = new Button("Generate OTP");
        generateOTPButton.disableProperty().bind(databaseTable.getSelectionModel().selectedItemProperty().isNull());
        generateOTPButton.setOnAction(a -> {
            User selectedUser = databaseTable.getSelectionModel().getSelectedItem();
            String selectedUserName = selectedUser.getUserName();

            TextInputDialog otpInputField = new TextInputDialog();
            otpInputField.setTitle("OTP for " + selectedUserName);
            otpInputField.setHeaderText("Type in one time password.");
            otpInputField.setContentText("OTP: ");

            Optional<String> otp = otpInputField.showAndWait();

            otp.ifPresent(string -> {
                try {
                    databaseHelper.setOneTimePassword(selectedUserName, string);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Alert generatedOTPAlert = new Alert(Alert.AlertType.INFORMATION);
                generatedOTPAlert.setTitle("One Time Password Set");
                generatedOTPAlert.setHeaderText("OTP set for " + selectedUserName);
                generatedOTPAlert.setContentText("The OTP is: " + string);
                generatedOTPAlert.showAndWait();
            });
        });

        // button to backout out of admin database page and return to the welcome login page
        Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
        });

        layout.getChildren().addAll(adminLabel, databaseTable, editUserRolesButton, deleteUserButton, generateOTPButton, backButton);
        Scene adminScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
    }

    // window that displays a checklist of all the roles for the admin to check what roles they want to assign to the selected user when editing roles
    private void editRolesWindow(User user) {
        VBox layout = new VBox();

        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Stage editRolesDialog = new Stage();
        editRolesDialog.initModality(Modality.APPLICATION_MODAL);
        String userName = user.getUserName();
        editRolesDialog.setTitle("Edit " + userName + "'s roles");

        // checkbox for each role
        CheckBox adminCheckBox = new CheckBox("admin");
        CheckBox userCheckBox = new CheckBox("user");
        CheckBox studentCheckBox = new CheckBox("student");
        CheckBox reviewerCheckBox = new CheckBox("reviewer");
        CheckBox instructorCheckBox = new CheckBox("instructor");
        CheckBox staffCheckBox = new CheckBox("staff");

        // automatically checks each role that the user currently has been assigned to
        List<String> currentUserRoles = databaseHelper.getUserRoles(userName);
        adminCheckBox.setSelected(currentUserRoles.contains("admin"));
        userCheckBox.setSelected(currentUserRoles.contains("user"));
        studentCheckBox.setSelected(currentUserRoles.contains("student"));
        reviewerCheckBox.setSelected(currentUserRoles.contains("reviewer"));
        instructorCheckBox.setSelected(currentUserRoles.contains("instructor"));
        staffCheckBox.setSelected(currentUserRoles.contains("staff"));

        // disables the admin from removing their own admin role
        if (userName.equals(adminUserName)) {
            adminCheckBox.setDisable(true);
            adminCheckBox.setSelected(true);
        }

        // submit button to confirm the changes made to roles
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(a -> {
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

            for (String role : newUserRoles) {
                if (!currentUserRoles.contains(role)) {
                    databaseHelper.addUserRole(userName, role);
                }
            }

            for (String role : currentUserRoles) {
                if (!newUserRoles.contains(role)) {
                    databaseHelper.removeUserRole(userName, role);
                }
            }

            editRolesDialog.close();
        });

        layout.getChildren().addAll(adminCheckBox, userCheckBox, studentCheckBox, reviewerCheckBox, instructorCheckBox, staffCheckBox, submitButton);
        Scene editRolesScene = new Scene(layout, 400, 200);

        editRolesDialog.setScene(editRolesScene);
        editRolesDialog.showAndWait();
    }
}