package application;

import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import databasePart1.DatabaseHelper;


/**
 * The QAView class represents the graphical user interface for managing questions and answers.
 * It allows users to view, add, update, delete, and manage questions and answers.
 */
public class QAView {
    private DatabaseHelper databaseHelper;
    private final Questions questions; // Manages question-related operations
    private final Answers answers; // Manages answer-related operations
    private User currentUser; // The currently logged-in user
    private TableView<Question> questionTable; // Table for displaying questions
    private TableView<Answer> answerTable; // Table for displaying answers
    private TextField questionInput; // Input field for adding/updating questions
    private TextField answerInput; // Input field for adding/updating answers
    private Button addQuestionBtn, updateQuestionBtn, deleteQuestionBtn; // Buttons for question management
    private Button addAnswerBtn, updateAnswerBtn, deleteAnswerBtn, acceptAnswerBtn; // Buttons for answer management
    private Button goBackBtn;
    private boolean isEditingQuestion = false; // Flag to track editing state


    /**
     * Constructor to initialize the QAView with database access and user details.
     *
     * @param dbHelper Database helper for accessing questions and answers.
     * @param user The currently logged-in user.
     */
    public QAView(DatabaseHelper dbHelper, User user) {
        this.questions = new Questions(dbHelper);
        this.answers = new Answers(dbHelper);
        this.currentUser = user;

        // Debugging information
        if (user != null && user.getUserId() != null) {
            System.out.println("QAView initialized for User ID: " + user.getUserId());
        } else {
            System.out.println("Error: User is NULL when initializing QAView.");
        }
    }

    /**
     * Initializes the graphical interface and displays the Q&A management system.
     *
     * @param primaryStage The primary window of the application.
     * @param user The currently logged-in user.
     * @throws SQLException If there is a database error.
     */
    public void start(Stage primaryStage, User user) throws SQLException {

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // Question section
        Label questionLabel = new Label("Manage Questions:");
        this.questionInput = new TextField();
        this.questionInput.setPromptText("Enter a new question...");

        // Set up search functionality for related questions
        this.questionInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !this.isEditingQuestion) {
                try {
                    List<String[]> results = this.questions.searchRelatedQuestions(newValue);
                    ObservableList<Question> filteredQuestions = FXCollections.observableArrayList();
                    for (String[] q : results) {
                        filteredQuestions.add(new Question(Integer.parseInt(q[0]), q[1], q[2], q[3]));
                    }
                    this.questionTable.setItems(filteredQuestions);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (!this.isEditingQuestion) {
                try {
                    this.loadQuestions();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // Create buttons for managing questions
        this.addQuestionBtn = new Button("Add Question");

        this.updateQuestionBtn = new Button("Update Question");

        this.deleteQuestionBtn = new Button("Delete Question");

        // New code --------------------------------------------------------------------
        this.goBackBtn = new Button("Back");
        this.goBackBtn.setOnAction(a -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
        });

        // New code --------------------------------------------------------------------


        this.addQuestionBtn.setOnAction(event -> {
            String questionText = this.questionInput.getText().trim();
            if (questionText.isEmpty()) {
                System.out.println("Error: Question text is empty.");
                return;
            }
            try {
                this.questions.createQuestion(Integer.valueOf(this.currentUser.getUserId()), questionText);
                this.loadQuestions(); // Refresh table
                this.questionInput.clear();
                System.out.println("Question added successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Event handler for updating a question
        updateQuestionBtn.setOnAction(event -> {
            Question selectedQuestion = questionTable.getSelectionModel().getSelectedItem();
            String newText = questionInput.getText().trim();

            if (selectedQuestion == null) {
                System.out.println("Error: No question selected.");
                return;
            }
            if (newText.isEmpty()) {
                System.out.println("Error: Question text cannot be empty.");
                return;
            }
            try {
                this.questions.updateQuestion(selectedQuestion.getId(), newText);
                this.loadQuestions(); // Refresh table
                this.questionInput.clear();
                System.out.println("Question updated successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Event handler for deleting a question
        deleteQuestionBtn.setOnAction(event -> {
            Question selectedQuestion = questionTable.getSelectionModel().getSelectedItem();

            if (selectedQuestion == null) {
                System.out.println("Error: No question selected.");
                return;
            }
            try {
                this.questions.deleteQuestion(selectedQuestion.getId());
                this.loadQuestions(); // Refresh table
                this.questionInput.clear();
                System.out.println("Question deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });










        // Table for displaying questions
        this.questionTable = new TableView<>();
        TableColumn<Question, String> questionCol = new TableColumn<>("Question");
        questionCol.setCellValueFactory(q -> q.getValue().questionTextProperty());
        TableColumn<Question, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(q -> q.getValue().timestampProperty());
        this.questionTable.getColumns().addAll(questionCol, timestampCol);

        // Load questions into the table
        this.loadQuestions();

        // Selection listener to enable question editing
        this.questionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldQuestion, newQuestion) -> {
            if (newQuestion != null) {
                this.isEditingQuestion = true;
                this.questionInput.setText(newQuestion.questionTextProperty().get());
            }

            // New code --------------------------------------------------------------------
            try {
                this.loadAnswers(newQuestion.getId()); // Load corresponding answers
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // New code --------------------------------------------------------------------

        });

        // Answer section
        Label answerLabel = new Label("Manage Answers:");
        this.answerInput = new TextField();
        this.answerInput.setPromptText("Enter a new answer...");

        // Create buttons for managing answers
        this.addAnswerBtn = new Button("Add Answer");
        this.updateAnswerBtn = new Button("Update Answer");
        this.deleteAnswerBtn = new Button("Delete Answer");
        this.acceptAnswerBtn = new Button("Mark as Accepted");

        this.addAnswerBtn.setOnAction(event -> {
            Question selectedQuestion = questionTable.getSelectionModel().getSelectedItem();
            String answerText = answerInput.getText().trim();

            if (selectedQuestion == null) {
                System.out.println("Error: No question selected.");
                return;
            }
            if (answerText.isEmpty()) {
                System.out.println("Error: Answer text cannot be empty.");
                return;
            }
            try {
                this.answers.createAnswer(selectedQuestion.getId(), Integer.valueOf(this.currentUser.getUserId()), answerText);
                this.loadAnswers(selectedQuestion.getId()); // Refresh answer table
                this.answerInput.clear();
                System.out.println("Answer added successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        updateAnswerBtn.setOnAction(event -> {
            Answer selectedAnswer = answerTable.getSelectionModel().getSelectedItem();
            String newText = answerInput.getText().trim();

            if (selectedAnswer == null) {
                System.out.println("Error: No answer selected.");
                return;
            }
            if (newText.isEmpty()) {
                System.out.println("Error: Answer text cannot be empty.");
                return;
            }
            try {
                this.answers.updateAnswer(selectedAnswer.getId(), newText);
                loadAnswers(selectedAnswer.getQuestionId()); // Refresh table
                answerInput.clear();
                System.out.println("Answer updated successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        deleteAnswerBtn.setOnAction(event -> {
            Answer selectedAnswer = answerTable.getSelectionModel().getSelectedItem();

            if (selectedAnswer == null) {
                System.out.println("Error: No answer selected.");
                return;
            }
            try {
                this.answers.deleteAnswer(selectedAnswer.getId());
                loadAnswers(selectedAnswer.getQuestionId()); // Refresh table
                answerInput.clear();
                System.out.println("Answer deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.acceptAnswerBtn.setOnAction((event) -> {
            try {
                Answer selected = (Answer)this.answerTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    this.answers.markAnswerAsAccepted(selected.getId());
                    this.loadAnswers(selected.getQuestionId());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Table for displaying answers
        this.answerTable = new TableView<>();
        TableColumn<Answer, String> answerCol = new TableColumn<>("Answer");
        answerCol.setCellValueFactory(a -> a.getValue().answerTextProperty());
        TableColumn<Answer, String> answerTimestampCol = new TableColumn<>("Timestamp");
        answerTimestampCol.setCellValueFactory(a -> a.getValue().timestampProperty());
        TableColumn<Answer, String> isAcceptedCol = new TableColumn<>("Accepted");
        isAcceptedCol.setCellValueFactory(a -> a.getValue().isAcceptedProperty());
        this.answerTable.getColumns().addAll(answerCol, answerTimestampCol, isAcceptedCol);

        // Layout configuration
        layout.getChildren().addAll(questionLabel, this.questionInput, this.addQuestionBtn, this.updateQuestionBtn,
                this.deleteQuestionBtn, this.questionTable, answerLabel, this.answerInput, this.addAnswerBtn,
                this.updateAnswerBtn, this.deleteAnswerBtn, this.acceptAnswerBtn, this.answerTable, this.goBackBtn);

        primaryStage.setScene(new Scene(layout, 800, 600));
        primaryStage.setTitle("Q&A Management");
        primaryStage.show();
    }

    /**
     * Loads all questions from the database and populates the question table.
     */
    private void loadQuestions() throws SQLException {
        List<String[]> questionsList = this.questions.getAllQuestions();
        ObservableList<Question> questionObservableList = FXCollections.observableArrayList();
        for (String[] q : questionsList) {
            questionObservableList.add(new Question(Integer.parseInt(q[0]), q[1], q[2], q[3]));
        }
        this.questionTable.setItems(questionObservableList);
    }

    private void loadAnswers(int questionId) throws SQLException {
        List<Answer> answersList = this.answers.getAnswersForQuestion(questionId);
        ObservableList<Answer> answerObservableList = FXCollections.observableArrayList(answersList);
        this.answerTable.setItems(answerObservableList);
    }
}
