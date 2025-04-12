package application;

public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private String messageText;
    private String timestamp;

    public Message(int id, int senderId, int receiverId, String messageText, String timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }
    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }
    public String getTimestamp() { return timestamp; }
}
