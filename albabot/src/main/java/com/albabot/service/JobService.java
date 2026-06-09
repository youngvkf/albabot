package com.albabot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.albabot.dao.ApplicationDao;
import com.albabot.dao.JobDao;
import com.albabot.model.Application;
import com.albabot.model.Job;
import com.albabot.model.User;

@Service
public class JobService {
	private final JobDao jobDao;
	private final ApplicationDao applicationDao;
	
	public JobService(JobDao jobDao, ApplicationDao applicationDao) {
		this.jobDao = jobDao;
		this.applicationDao = applicationDao;
	}
	
	public List<Job> showAllJobs(){
		List<Job> jobs = jobDao.getAllJobs();
		
		if (jobs.isEmpty() || jobs == null) {
			return new ArrayList<>();
		}
		
		return jobs;
	}
	
	public List<Job> showJobsByCategory(String category){
		List<Job> jobs = jobDao.getAllJobs();
		List<Job> filteredJobs = new ArrayList<Job>();
		
		if (jobs.isEmpty()) {
			return null;
		}
		
		for (Job job : jobs) {
			if (job.getCategory().equals(category)) {
				filteredJobs.add(job);
			}
		}
		
		return filteredJobs;
	}
	
	public void addJob(Job job) {
		jobDao.insertJob(job);
	}
	
	public boolean applicate(int jobId, User loginUser, String coverLetter) {
		Job job = jobDao.getJobById(jobId);
		
		if (job == null || loginUser == null) {
	        return false;
	    }

	    if (job.getEmployerId() == loginUser.getUserId()) {
	        return false;
	    }

	    if (applicationDao.hasApplied(loginUser.getUserId(), jobId)) {
	        return false;
	    }
		
		Application app = new Application();
		app.setUserId(loginUser.getUserId());
		app.setJobId(jobId);
		app.setStatus("PENDING");
		app.setCoverLetter(coverLetter);
		applicationDao.insertApplication(app);
		
		return true;
	}
	
	public int countApplication(int jobId) {
		return applicationDao.getApplicationByJobId(jobId);
	}
}
