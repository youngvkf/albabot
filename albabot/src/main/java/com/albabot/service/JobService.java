package com.albabot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.albabot.dao.JobDao;
import com.albabot.model.Job;

@Service
public class JobService {
	private final JobDao jobDao;
	
	public JobService(JobDao jobDao) {
		this.jobDao = jobDao;
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
}
