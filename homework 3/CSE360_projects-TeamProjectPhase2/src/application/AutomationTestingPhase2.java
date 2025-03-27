package application;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import databasePart1.DatabaseHelper;

class AutomationTestingPhase2 {
    DatabaseHelper dbHelper;
    User testUser;
    Questions questions;
    Answers answers;
    int userId;

    @BeforeEach
    void setUp() throws SQLException {
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();

        questions = new Questions(dbHelper);
        answers = new Answers(dbHelper);
        
        dbHelper.connectToDatabase();

        // Step 2: Check if database is empty (first-time setup scenario)
        if (dbHelper.isDatabaseEmpty()) { // Ensure this method is public
            System.out.println("Database is empty. Setting up first user as admin...");
            User firstAdmin = new User("adminUser", "adminPass", "", "", "");
            dbHelper.register(firstAdmin); // Add first user
            dbHelper.addUserRole("adminUser", "admin"); // Assign admin role
        }

        // Step 3: Register a new user
        String testUserName = "testUser";
        String testPassword = "password123";
        String testRole = "student";
        testUser = new User(testUserName, testPassword, testUserName, testRole, dbHelper);

        if (!dbHelper.doesUserExist(testUserName)) { // Ensure doesUserExist() is public
            System.out.println("\nRegistering a new user: " + testUserName);
            dbHelper.register(testUser);
        } else {
            System.out.println("\nUser already exists: " + testUserName);
        }
        
        String userIdStr = dbHelper.getUserIdByUsername(testUser.getUserName());
        if(userIdStr.isEmpty()){
            fail("User ID not found for " + testUser.getUserName());
        }
        
        userId = Integer.parseInt(userIdStr);
    }

    @Test
    void testCreateQuestion() throws SQLException {
    	
        String questionText = "Test Question 1?";
        questions.createQuestion(userId, questionText);

        List<String[]> allQuestions = questions.getAllQuestions();
        boolean found = false;
        for (String[] eachQuestion : allQuestions) {
            if (eachQuestion[2].equals(questionText)) {
                found = true;
                break;
            }
        }
        assertTrue(found, "The created question is in the database.");
    }
    
    @Test
    public void testUpdateQuestion() throws SQLException {
        String originalText = "Test Original Question 2?";
        questions.createQuestion(userId, originalText);
        
        List<String[]> allQuestions = questions.getAllQuestions();
        int questionId = -1;
        for (String[] eachQuestion : allQuestions) {
            if (eachQuestion[2].equals(originalText)) {
                questionId = Integer.parseInt(eachQuestion[0]);
                break;
            }
        }
        assertNotEquals(-1, questionId, "Question ID is found");
        
        String updatedText = "Test Updated Question 2?";
        questions.updateQuestion(questionId, updatedText);
        
        List<String[]> updatedQuestions = questions.getAllQuestions();
        boolean found = false;
        for (String[] eachQuestion : updatedQuestions) {
            if (Integer.parseInt(eachQuestion[0]) == questionId && eachQuestion[2].equals(updatedText)) {
                found = true;
                break;
            }
        }
        assertTrue(found, "The updated question text is found.");
    }
    
    @Test
    public void testDeleteQuestion() throws SQLException {
        String questionText = "Test Question 3?";
        questions.createQuestion(userId, questionText);
        
        List<String[]> allQuestions = questions.getAllQuestions();
        int questionId = -1;
        for (String[] eachQuestion : allQuestions) {
            if (eachQuestion[2].equals(questionText)) {
                questionId = Integer.parseInt(eachQuestion[0]);
                break;
            }
        }
        assertNotEquals(-1, questionId, "Question ID is found before deletion.");
        
        questions.deleteQuestion(questionId);
        
        List<String[]> remainingQuestions = questions.getAllQuestions();
        boolean found = false;
        for (String[] eachQuestion: remainingQuestions) {
            if (Integer.parseInt(eachQuestion[0]) == questionId) {
                found = true;
                break;
            }
        }
        assertFalse(found, "The question is deleted from the database.");
    }
    
    @Test
    public void testCreateAnswer() throws SQLException {
        String questionText = "Test Question 1?";
        questions.createQuestion(userId, questionText);
        
        List<String[]> allQuestions = questions.getAllQuestions();
        int questionId = -1;
        for (String[] eachQuestion : allQuestions) {
            if (eachQuestion[2].equals(questionText)) {
                questionId = Integer.parseInt(eachQuestion[0]);
                break;
            }
        }
        assertNotEquals(-1, questionId, "Question ID is found.");
        
        String answerText = "Test Answer 1";
        answers.createAnswer(questionId, userId, answerText);
        
        List<Answer> answerList = answers.getAnswersForQuestion(questionId);
        boolean found = false;
        for (Answer eachAnswer : answerList) {
            if (eachAnswer.answerTextProperty().get().equals(answerText)) {
                found = true;
                break;
            }
        }
        assertTrue(found, "The created answer is found in the database.");
    }
    
    @Test
    public void testUpdateAnswer() throws SQLException {
        String questionText = "Test Question 2?";
        questions.createQuestion(userId, questionText);
        
        List<String[]> allQuestions = questions.getAllQuestions();
        int questionId = -1;
        for (String[] eachQuestion : allQuestions) {
            if (eachQuestion[2].equals(questionText)) {
                questionId = Integer.parseInt(eachQuestion[0]);
                break;
            }
        }
        assertNotEquals(-1, questionId, "Question ID is found.");
        
        String answerText = "Test Answer 2";
        answers.createAnswer(questionId, userId, answerText);
        
        List<Answer> answerList = answers.getAnswersForQuestion(questionId);
        int answerId = -1;
        for (Answer eachAnswer : answerList) {
            if (eachAnswer.answerTextProperty().get().equals(answerText)) {
                answerId = eachAnswer.getId();
                break;
            }
        }
        assertNotEquals(-1, answerId, "Answer ID is found.");
        
        String updatedAnswer = "Updated Test Answer 2";
        answers.updateAnswer(answerId, updatedAnswer);
        
        List<Answer> updatedAnswerList = answers.getAnswersForQuestion(questionId);
        boolean found = false;
        for (Answer eachAnswer : updatedAnswerList) {
            if (eachAnswer.getId() == answerId && eachAnswer.answerTextProperty().get().equals(updatedAnswer)) {
                found = true;
                break;
            }
        }
        assertTrue(found, "The updated answer is in the database.");
    }
    
    @Test
    public void testDeleteAnswer() throws SQLException {
        String questionText = "Test Question 3?";
        
        questions.createQuestion(userId, questionText);
        
        List<String[]> allQuestions = questions.getAllQuestions();
        int questionId = -1;
        for (String[] eachQuestion : allQuestions) {
            if (eachQuestion[2].equals(questionText)) {
                questionId = Integer.parseInt(eachQuestion[0]);
                break;
            }
        }
        assertNotEquals(-1, questionId, "Question ID is found.");
        
        String answerText = "Test Answer 3";
        answers.createAnswer(questionId, userId, answerText);
        
        List<Answer> answerList = answers.getAnswersForQuestion(questionId);
        int answerId = -1;
        for (Answer eachAnswer : answerList) {
            if (eachAnswer.answerTextProperty().get().equals(answerText)) {
                answerId = eachAnswer.getId();
                break;
            }
        }
        assertNotEquals(-1, answerId, "Answer ID is found.");
        
        answers.deleteAnswer(answerId);
        
        List<Answer> remainingAnswers = answers.getAnswersForQuestion(questionId);
        boolean found = false;
        for (Answer eachAnswer : remainingAnswers) {
            if (eachAnswer.getId() == answerId) {
                found = true;
                break;
            }
        }
        assertFalse(found, "The answer is deleted from the database.");
    }
    
    @Test
    public void testMarkAnswerAsAccepted() throws SQLException {
        String questionText = "Test Question 4?";

        questions.createQuestion(userId, questionText);
        
        List<String[]> allQuestions = questions.getAllQuestions();
        int questionId = -1;
        for (String[] eachQuestion : allQuestions) {
            if (eachQuestion[2].equals(questionText)) {
                questionId = Integer.parseInt(eachQuestion[0]);
                break;
            }
        }
        assertNotEquals(-1, questionId, "Question ID is found.");
        
        String answerText = "Test Answer 4";
        answers.createAnswer(questionId, userId, answerText);
        
        List<Answer> answerList = answers.getAnswersForQuestion(questionId);
        int answerId = -1;
        for (Answer eachAnswer : answerList) {
            if (eachAnswer.answerTextProperty().get().equals(answerText)) {
                answerId = eachAnswer.getId();
                break;
            }
        }
        assertNotEquals(-1, answerId, "Answer ID is found.");
        
        answers.markAnswerAsAccepted(answerId);
        
        List<Answer> updatedAnswerList = answers.getAnswersForQuestion(questionId);
        boolean accepted = false;
        for (Answer eachAnswer : updatedAnswerList) {
            if (eachAnswer.getId() == answerId && eachAnswer.isAcceptedProperty().get().equals("Accepted")) {
                accepted = true;
                break;
            }
        }
        assertTrue(accepted, "The answer is marked as accepted.");
    }
}
