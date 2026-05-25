package com.albabot.dao;
import model.Job;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import albabot_backend.DatabaseConnector;

public class JobDao {
	private final DatabaseConnector connection;
	
	public JobDao(DatabaseConnector connection) {
		this.connection = connection;
	}
	
	public List<Job> getAllJobs() {
		List<Job> jobs = new ArrayList<>();
		String sql = "SELECT * FROM jobs ORDER BY job_id ASC";
		
		try (Connection conn = connection.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql)){
				while(rs.next()) {
					Job j = new Job();
					j.setJobId(rs.getInt("job_id"));
					j.setEmployerId(rs.getInt("employer_id"));
					j.setTitle(rs.getString("title"));
					j.setCategory(rs.getString("category"));
					j.setDescription(rs.getString("description"));
					jobs.add(j);
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jobs;
	}
	
	public void insertJob(Job job) {
		String sql = "INSERT INTO jobs (employer_id, title, category, description) VALUES (?, ?, ?, ?)";
		
		try (Connection conn = connection.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
				
			pstmt.setInt(1, job.getEmployerId());
			pstmt.setString(2, job.getTitle());
			pstmt.setString(3, job.getCategory());
			pstmt.setString(4, job.getDescription());
			
			pstmt.executeUpdate();
			
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()){
				if (generatedKeys.next()) {
					int no = generatedKeys.getInt(1);
					job.setJobId(no);
					System.out.println(no + "게시글이 작성되었습니다.");
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateJob(Job job) {
		String sql = "UPDATE jobs SET employer_id = ?, title = ?, category = ?, description = ? WHERE job_id = ?";

		try (Connection conn = connection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, job.getEmployerId());
			pstmt.setString(2, job.getTitle());
			pstmt.setString(3, job.getCategory());
			pstmt.setString(4, job.getDescription());
			pstmt.setInt(5, job.getJobId()); 

			int rows = pstmt.executeUpdate();
			if (rows > 0) {
				System.out.println("게시글이 수정되었습니다.");
			} else {
				System.out.println("해당 job_id의 게시글이 존재하지 않습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteJob(int jobId) {
		String sql = "DELETE FROM jobs WHERE job_id = ?";

		try (Connection conn = connection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, jobId);

			int rows = pstmt.executeUpdate();
			if (rows > 0) {
				System.out.println("게시글이 삭제되었습니다.");
			} else {
				System.out.println("해당 job_id의 게시글이 존재하지 않습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

