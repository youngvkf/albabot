package com.albabot.model;

import java.time.LocalDateTime;

public class Evaluation {
    private int evalId;
    private int reviewerId;
    private int revieweeId;
    private int jobId;
    private int score;
    private String comment;
    private String evalType; // EMPLOYER_TO_SEEKER, SEEKER_TO_EMPLOYER
    private LocalDateTime createdAt;
    
    // detail.html 렌더링을 위해 JOIN 쿼리로 가져온 데이터를 담을 필드
    private String reviewerName; 

    public Evaluation() {}

    // Getter 및 Setter 메서드
    public int getEvalId() { return evalId; }
    public void setEvalId(int evalId) { this.evalId = evalId; }
    public int getReviewerId() { return reviewerId; }
    public void setReviewerId(int reviewerId) { this.reviewerId = reviewerId; }
    public int getRevieweeId() { return revieweeId; }
    public void setRevieweeId(int revieweeId) { this.revieweeId = revieweeId; }
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getEvalType() { return evalType; }
    public void setEvalType(String evalType) { this.evalType = evalType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
}