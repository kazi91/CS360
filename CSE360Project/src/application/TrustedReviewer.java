package application;

public class TrustedReviewer {
    private int studentId;
    private int reviewerId;
    private int weight;
    private String reviewerName;

    public TrustedReviewer(int studentId, int reviewerId, int weight, String reviewerName) {
        this.studentId = studentId;
        this.reviewerId = reviewerId;
        this.weight = weight;
        this.reviewerName = reviewerName;
    }

    public int getStudentId() { return studentId; }
    public int getReviewerId() { return reviewerId; }
    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }
    public String getReviewerName() { return reviewerName; }
}
