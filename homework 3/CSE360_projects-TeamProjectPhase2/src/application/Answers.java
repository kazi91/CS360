package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;

/**
 * The Answers class provides an interface to interact with answers stored in the database.
 * It allows fetching, creating, updating, and deleting answers.
 */
public class Answers {
    private final DatabaseHelper dbHelper; // Database helper instance to manage database operations

    /**
     * Constructor to initialize the Answers class with a database helper.
     *
     * @param dbHelper The DatabaseHelper instance to interact with the database.
     */
    public Answers(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Retrieves all answers for a specific question.
     *
     * @param questionId The ID of the question for which answers are requested.
     * @return A list of Answer objects for the given question.
     * @throws SQLException If a database error occurs.
     */    public List<Answer> getAnswersForQuestion(int questionId) throws SQLException {
        return this.dbHelper.getAnswersForQuestion(questionId);
    }

    /**
     * Creates a new answer for a specific question.
     *
     * @param questionId The ID of the question being answered.
     * @param userId The ID of the user providing the answer.
     * @param answerText The text content of the answer.
     * @throws SQLException If a database error occurs.
     */
    public void createAnswer(int questionId, int userId, String answerText) throws SQLException {
        this.dbHelper.createAnswer(questionId, userId, answerText);
    }

    /**
     * Updates an existing answer.
     *
     * @param answerId The ID of the answer to be updated.
     * @param newText The new text content for the answer.
     * @throws SQLException If a database error occurs.
     */
    public void updateAnswer(int answerId, String newText) throws SQLException {
        this.dbHelper.updateAnswer(answerId, newText);
    }

    /**
     * Deletes an answer from the database.
     *
     * @param answerId The ID of the answer to be deleted.
     * @throws SQLException If a database error occurs.
     */
    public void deleteAnswer(int answerId) throws SQLException {
        this.dbHelper.deleteAnswer(answerId);
    }

    /**
     * Marks an answer as accepted.
     *
     * @param answerId The ID of the answer to be marked as accepted.
     * @throws SQLException If a database error occurs.
     */
    public void markAnswerAsAccepted(int answerId) throws SQLException {
        this.dbHelper.markAnswerAsAccepted(answerId);
    }
}
