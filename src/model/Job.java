package model;

import java.time.LocalDateTime;

public class Job {
	private int jobId;
	private int employerId;
	private String title;
	private String category;
	private int hourlyWage;
	private String location;
	private String work_hours;
	private LocalDateTime deadline;
	private String description;
	private LocalDateTime createdAt;
	
	private enum Status{
		OPEN,
		CLOSED
	}
	
	public Job() {};
	
	public Job(int employerId, String title, String category, String description){
		this.employerId = employerId;
		this.title = title;
		this.category = category;
		this.description = description;
	}
	
	public Job(int jobId, int employerId, String title, String category, String description){
		this.jobId = jobId;
		this.employerId = employerId;
		this.title = title;
		this.category = category;
		this.description = description;
	}

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public int getEmployerId() {
		return employerId;
	}

	public void setEmployerId(int employerId) {
		this.employerId = employerId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getHourlyWage() {
		return hourlyWage;
	}

	public void setHourlyWage(int hourlyWage) {
		this.hourlyWage = hourlyWage;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getWork_hours() {
		return work_hours;
	}

	public void setWork_hours(String work_hours) {
		this.work_hours = work_hours;
	}

	public LocalDateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDateTime deadline) {
		this.deadline = deadline;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	
}
