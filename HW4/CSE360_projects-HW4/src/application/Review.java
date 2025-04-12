package application;

public class Review {
    private int id;
    private int reviewerId;
    private int reviewedItemId;
    private String reviewType;
    private String reviewText;
    private String timestamp;

    public Review(int id, int reviewerId, int reviewedItemId, String reviewType, String reviewText, String timestamp) {
        this.id = id;
        this.reviewerId = reviewerId;
        this.reviewedItemId = reviewedItemId;
        this.reviewType = reviewType;
        this.reviewText = reviewText;
        this.timestamp = timestamp;
    }

    public int getUserId() { return id; }
    public int getReviewerId() { return reviewerId; }
    public int getReviewedItemId() { return reviewedItemId; }
    public String getReviewType() { return reviewType; }
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    public String getTimestamp() { return timestamp; }
}
