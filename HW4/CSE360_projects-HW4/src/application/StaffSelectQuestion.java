package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;



public class StaffSelectQuestion {
	
	 private final DatabaseHelper databaseHelper;
	 private final User user;
	 
	 public StaffSelectQuestion(DatabaseHelper databaseHelper, User user) {
	        this.databaseHelper = databaseHelper;
	        this.user = user;
	 }
	 
	 
	 public void show(Stage primaryStage) {
	        VBox layout = new VBox(10);
	        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
	        Label header = new Label("Select a Question to View");
	        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	        TableView<QuestionItem> table = new TableView<>();
	        table.setPlaceholder(new Label("No questions available"));

	        TableColumn<QuestionItem, String> idCol = new TableColumn<>("ID");
	        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
	        TableColumn<QuestionItem, String> userCol = new TableColumn<>("User");
	        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
	        TableColumn<QuestionItem, String> textCol = new TableColumn<>("Question");
	        textCol.setCellValueFactory(new PropertyValueFactory<>("questionText"));
	        TableColumn<QuestionItem, String> timeCol = new TableColumn<>("Timestamp");
	        timeCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
	        table.getColumns().addAll(idCol, userCol, textCol, timeCol);

	        ObservableList<QuestionItem> items = FXCollections.observableArrayList();
	        try {
	            List<String[]> questions = databaseHelper.getAllQuestions();
	            for (String[] q : questions) {
	                items.add(new QuestionItem(q[0], q[1], q[2], q[3]));
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        table.setItems(items);

	        Button selectButton = new Button("Select Question");
	        selectButton.setOnAction(e -> {
	            QuestionItem selected = table.getSelectionModel().getSelectedItem();
	            if (selected == null) {
	                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a question.");
	                alert.showAndWait();
	                return;
	            }
	            StaffReviewNote.show(primaryStage, databaseHelper, user, selected.getId());
	        });

	        Button backButton = new Button("Back");
	        backButton.setOnAction(e -> new StaffHomePage(databaseHelper).show(primaryStage, user));

	        layout.getChildren().addAll(header, table, selectButton, backButton);
	        Scene scene = new Scene(layout, 800, 600);
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("Select Question");
	    }

	    public static class QuestionItem {
	        private String id;
	        private String userName;
	        private String questionText;
	        private String timestamp;

	        public QuestionItem(String id, String userName, String questionText, String timestamp) {
	            this.id = id;
	            this.userName = userName;
	            this.questionText = questionText;
	            this.timestamp = timestamp;
	        }

	        public String getId() { return id; }
	        public String getUserName() { return userName; }
	        public String getQuestionText() { return questionText; }
	        public String getTimestamp() { return timestamp; }
	    }
}

