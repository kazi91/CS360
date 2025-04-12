package application;

public class ReviewerRequest {
    private int id;
    private int studentId;
    private String studentName;  // New field to store student name
    private String requestText;
    private String status;
    private String timestamp;

    public ReviewerRequest(int id, int studentId, String studentName, String requestText, String status, String timestamp) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.requestText = requestText;
        this.status = status;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getRequestText() { return requestText; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTimestamp() { return timestamp; }
}
