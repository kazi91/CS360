package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Answer class represents a response to a question in the system.
 * It stores the answer's details such as ID, associated question ID,
 * user who provided the answer, the text of the answer, timestamp, and
 * whether the answer is accepted or pending.
 */
public class Answer {
    private int id; // Unique identifier for the answer
    private int questionId; // ID of the question this answer belongs to
    private StringProperty userName; // Username of the person who submitted the answer
    private StringProperty text; // The actual answer text
    private StringProperty timestamp; // The timestamp of when the answer was submitted
    private StringProperty isAccepted; // Stores the status of the answer (Accepted/Pending)

    /**
     * Constructor to initialize an Answer object.
     *
     * @param id         The unique ID of the answer.
     * @param questionId The ID of the associated question.
     * @param userName   The username of the answer's author.
     * @param text       The text content of the answer.
     * @param timestamp  The timestamp when the answer was posted.
     * @param isAccepted Boolean indicating if the answer is accepted or pending.
     */
    public Answer(int id, int questionId, String userName, String text, String timestamp, boolean isAccepted) {
        this.id = id;
        this.questionId = questionId;
        this.userName = new SimpleStringProperty(userName);
        this.text = new SimpleStringProperty(text);
        this.timestamp = new SimpleStringProperty(timestamp);
        this.isAccepted = new SimpleStringProperty(isAccepted ? "Accepted" : "Pending acception");
    }

    /**
     * Gets the ID of this answer.
     *
     * @return The answer's ID.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the ID of the question this answer is associated with.
     *
     * @return The question ID.
     */
    public int getQuestionId() {
        return this.questionId;
    }

    /**
     * Gets the username as a JavaFX property.
     *
     * @return A StringProperty containing the username.
     */
    public StringProperty userNameProperty() {
        return this.userName;
    }

    /**
     * Gets the answer text as a JavaFX property.
     *
     * @return A StringProperty containing the answer text.
     */
    public StringProperty answerTextProperty() {
        return this.text;
    }

    /**
     * Gets the timestamp of when the answer was submitted as a JavaFX property.
     *
     * @return A StringProperty containing the timestamp.
     */
    public StringProperty timestampProperty() {
        return this.timestamp;
    }

    /**
     * Gets the acceptance status of the answer as a JavaFX property.
     *
     * @return A StringProperty containing the acceptance status ("Accepted" or "Pending acception").
     */
    public StringProperty isAcceptedProperty() {
        return this.isAccepted;
    }
}
