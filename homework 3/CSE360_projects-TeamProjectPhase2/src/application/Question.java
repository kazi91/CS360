package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Question class represents a question posted by a user in the system.
 * It contains details such as question ID, username of the asker, the question text, and timestamp.
 */
public class Question {
    private int id; // Unique identifier for the question
    private StringProperty userName; // The username of the person who asked the question
    private StringProperty questionText; // The actual question content
    private StringProperty timestamp; // The time when the question was posted

    /**
     * Constructor for initializing a Question object.
     *
     * @param id         Unique identifier for the question.
     * @param userName   Name of the user who posted the question.
     * @param text       The text content of the question.
     * @param timestamp  Timestamp when the question was created.
     */
    public Question(int id, String userName, String text, String timestamp) {
        this.id = id;
        this.userName = new SimpleStringProperty(userName);
        this.questionText = new SimpleStringProperty(text);
        this.timestamp = new SimpleStringProperty(timestamp);
    }

    /**
     * Retrieves the ID of the question.
     *
     * @return The unique question ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the username property (JavaFX property format).
     *
     * @return StringProperty representing the username.
     */
    public StringProperty userNameProperty() {
        return userName;
    }

    /**
     * Gets the question text property (JavaFX property format).
     *
     * @return StringProperty representing the question content.
     */
    public StringProperty questionTextProperty() {
        return questionText;
    }

    /**
     * Gets the timestamp property (JavaFX property format).
     *
     * @return StringProperty representing when the question was created.
     */
    public StringProperty timestampProperty() {
        return timestamp;
    }

    /**
     * Converts the question object to a readable string format.
     * This is useful when displaying the question in a UI component.
     *
     * @return The question text as a string.
     */
    @Override
    public String toString() {
        return questionText.get();
    }
}
