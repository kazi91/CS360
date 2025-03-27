package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;

/**
 * The Questions class manages all database interactions related to questions.
 */
public class Questions {
    private final DatabaseHelper dbHelper; // Database helper instance for performing database operations.

    /**
     * Constructor to initialize the Questions class with a database helper.
     *
     * @param dbHelper An instance of DatabaseHelper for database operations.
     */
    public Questions(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Retrieves a list of all questions stored in the database.
     *
     * @return A list of questions represented as String arrays.
     * @throws SQLException If there is an issue accessing the database.
     */
    public List<String[]> getAllQuestions() throws SQLException {
        return this.dbHelper.getAllQuestions();
    }

    /**
     * Searches for related questions in the database that contain a specific keyword.
     *
     * @param keyword The search term to find relevant questions.
     * @return A list of matching questions as String arrays.
     * @throws SQLException If there is an issue accessing the database.
     */
    public List<String[]> searchRelatedQuestions(String keyword) throws SQLException {
        return this.dbHelper.searchRelatedQuestions(keyword);
    }

    /**
     * Creates a new question in the database.
     *
     * @param userId       The ID of the user posting the question.
     * @param questionText The text of the question being asked.
     * @throws SQLException If there is an issue accessing the database.
     */
    public void createQuestion(int userId, String questionText) throws SQLException {
        this.dbHelper.createQuestion(userId, questionText);
    }

    /**
     * Updates the content of an existing question.
     *
     * @param questionId The ID of the question to be updated.
     * @param newText    The new text for the question.
     * @throws SQLException If there is an issue accessing the database.
     */
    public void updateQuestion(int questionId, String newText) throws SQLException {
        this.dbHelper.updateQuestion(questionId, newText);
    }

    /**
     * Deletes a question from the database.
     *
     * @param questionId The ID of the question to be deleted.
     * @throws SQLException If there is an issue accessing the database.
     */
    public void deleteQuestion(int questionId) throws SQLException {
        this.dbHelper.deleteQuestion(questionId);
    }
}
