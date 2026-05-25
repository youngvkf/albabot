package com.albabot.model;

import java.time.LocalDateTime;

public class Application {
	private int applicationId;
	private int userId;
	private int jobId;
	private LocalDateTime appliedAt;
	private String coverLetter;
	
	private enum Status{
		PENDING,
		ACCEPTED,
		REJECTED
	}
	
	public Application(){}
	
	public Application(int userId, int jobId, String coverLetter){
		this.userId = userId;
		this.jobId = jobId;
		this.coverLetter = coverLetter;
	}

	public int getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(int applicationId) {
		this.applicationId = applicationId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public LocalDateTime getAppliedAt() {
		return appliedAt;
	}

	public void setAppliedAt(LocalDateTime appliedAt) {
		this.appliedAt = appliedAt;
	}

	public String getCoverLetter() {
		return coverLetter;
	}

	public void setCoverLetter(String coverLetter) {
		this.coverLetter = coverLetter;
	}
	
	
}
