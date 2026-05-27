package com.albabot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource; // 스프링 표준 데이터소스로 변경
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.albabot.model.Job;
import com.albabot.model.Status;

@Repository // 스프링 빈으로 등록
public class JobDao {

	@Autowired
	private DataSource dataSource; // UserDao와 동일하게 DataSource 사용
	
	public List<Job> getAllJobs() {
		List<Job> jobs = new ArrayList<>();
		String sql = "SELECT * FROM jobs ORDER BY job_id ASC";
		
		try (Connection conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql)){
				while(rs.next()) {
					Job j = new Job();
					j.setJobId(rs.getInt("job_id"));
					j.setEmployerId(rs.getInt("employer_id"));
					j.setTitle(rs.getString("title"));
					j.setCategory(rs.getString("category"));
					j.setHourlyWage(rs.getString("hourly_wage"));
					j.setLocation(rs.getString("location"));
					j.setWork_hours(rs.getString("work_hours"));
					j.setDeadline(rs.getTimestamp("deadline").toLocalDateTime());
					j.setDescription(rs.getString("description"));
					j.setStatus(Status.valueOf(rs.getString("status")));
					j.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
					jobs.add(j);
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jobs;
	}
	
	public void insertJob(Job job) {
		String sql = "INSERT INTO jobs (employer_id, title, category, hourly_wage, location, work_hours, deadline, description, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
				
			pstmt.setInt(1, job.getEmployerId());
			pstmt.setString(2, job.getTitle());
			pstmt.setString(3, job.getCategory());
			pstmt.setString(4, job.getHourlyWage());
			pstmt.setString(5, job.getLocation());
			pstmt.setString(6, job.getWork_hours());
			pstmt.setTimestamp(7, Timestamp.valueOf(job.getDeadline()));
			pstmt.setString(8, job.getDescription());
			pstmt.setString(9, job.getStatus().name());
			pstmt.setTimestamp(9, Timestamp.valueOf(job.getCreatedAt()));
			
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

		try (Connection conn = dataSource.getConnection();
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

		try (Connection conn = dataSource.getConnection();
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

	// 공고 ID로 단건 조회하는 메서드
	public Job getJobById(int jobId) {
		String sql = "SELECT * FROM jobs WHERE job_id = ?";
		
		try (Connection conn = dataSource.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setInt(1, jobId);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					Job j = new Job();
					j.setJobId(rs.getInt("job_id"));
					j.setEmployerId(rs.getInt("employer_id"));
					j.setTitle(rs.getString("title"));
					j.setCategory(rs.getString("category"));
					j.setHourlyWage(rs.getString("hourly_wage")); 
					j.setLocation(rs.getString("location"));   
					j.setWork_hours(rs.getString("work_hours")); 
					j.setDescription(rs.getString("description"));
					
					if (rs.getTimestamp("deadline") != null) {
						j.setDeadline(rs.getTimestamp("deadline").toLocalDateTime());
					}
					if (rs.getTimestamp("created_at") != null) {
						j.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
					}
					
					return j;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
