package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DMPage {
    private final DatabaseHelper databaseHelper;
    private final User currentUser;

    public DMPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label titleLabel = new Label("Direct Messages");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Recipient selection
        ComboBox<String> recipientCombo = new ComboBox<>();
        try {
            List<String> allUsers = databaseHelper.getAllUsernames();
            allUsers.remove(currentUser.getUserName());
            recipientCombo.getItems().addAll(allUsers);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Message display
        ListView<String> messageList = new ListView<>();

        // Message input
        TextArea messageInput = new TextArea();
        messageInput.setPromptText("Type your message here...");
        messageInput.setPrefRowCount(3);

        Button sendButton = new Button("Send");
        Button backButton = new Button("Back");

        // Load messages when recipient is selected
        recipientCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    List<String[]> messages = databaseHelper.getMessagesBetween(
                            currentUser.getUserName(), newVal);
                    messageList.getItems().clear();
                    for (String[] msg : messages) {
                        messageList.getItems().add(msg[0] + " (" + msg[3] + "): " + msg[2]);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // Send message with instant update
        sendButton.setOnAction(e -> {
            String recipient = recipientCombo.getValue();
            String content = messageInput.getText().trim();
            if (recipient == null || content.isEmpty()) return;

            try {
                // Save to database
                databaseHelper.saveMessage(currentUser.getUserName(), recipient, content);

                // Create timestamp for instant display
                String timestamp = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                // Add to ListView immediately
                String newMessage = currentUser.getUserName() + " (" + timestamp + "): " + content;
                messageList.getItems().add(newMessage);
                messageInput.clear();

                // Scroll to bottom
                messageList.scrollTo(messageList.getItems().size() - 1);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        backButton.setOnAction(e -> {
            new StudentHomePage(databaseHelper).show(primaryStage, currentUser);
        });

        layout.getChildren().addAll(
                titleLabel,
                new Label("Select Recipient:"),
                recipientCombo,
                new Label("Messages:"),
                messageList,
                new Label("New Message:"),
                messageInput,
                sendButton,
                backButton
        );

        primaryStage.setScene(new Scene(layout, 600, 500));
        primaryStage.setTitle("Direct Messages");
    }
}