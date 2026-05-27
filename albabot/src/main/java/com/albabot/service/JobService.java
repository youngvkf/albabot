package com.albabot.service;

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
		
		if (jobs.isEmpty()) {
			return null;
		}
		
		return jobs;
	}
}
