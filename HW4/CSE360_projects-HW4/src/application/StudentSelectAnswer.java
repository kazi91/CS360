package application;

import databasePart1.DatabaseHelper;
import application.StudentSelectAnswer.AnswerItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

public class StudentSelectAnswer {

    private final DatabaseHelper databaseHelper;
    private final User user;

    public StudentSelectAnswer(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Label header = new Label("Select an Answer to View Reviews");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<AnswerItem> table = new TableView<>();
        table.setPlaceholder(new Label("No answers available"));

        TableColumn<AnswerItem, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<AnswerItem, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        TableColumn<AnswerItem, String> answerCol = new TableColumn<>("Answer");
        answerCol.setCellValueFactory(new PropertyValueFactory<>("answerText"));
        TableColumn<AnswerItem, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        table.getColumns().addAll(idCol, userCol, answerCol, timestampCol);

        ObservableList<AnswerItem> items = FXCollections.observableArrayList();
        try {
            List<Answer> answers = databaseHelper.getAllAnswers();
            for (Answer a : answers) {
                items.add(new AnswerItem(a.getId(), a.getUserName(), a.getAnswerText(), a.getTimestamp()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table.setItems(items);

        Button selectButton = new Button("Select Answer");
        selectButton.setOnAction(e -> {
            AnswerItem selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select an answer.");
                alert.showAndWait();
                return;
            }
            new StudentReviewView(databaseHelper, selected.getId()).show(primaryStage, user);
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new StudentHomePage(databaseHelper).show(primaryStage, user));

        layout.getChildren().addAll(header, table, selectButton, backButton);
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Select Answer");
    }

    public static class AnswerItem {
        private int id;
        private String userName;
        private String answerText;
        private String timestamp;

        public AnswerItem(int id, String userName, String answerText, String timestamp) {
            this.id = id;
            this.userName = userName;
            this.answerText = answerText;
            this.timestamp = timestamp;
        }

        public int getId() { return id; }
        public String getUserName() { return userName; }
        public String getAnswerText() { return answerText; }
        public String getTimestamp() { return timestamp; }
    }
}
