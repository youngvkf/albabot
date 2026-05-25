package com.albabot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import albabot_backend.DatabaseConnector;
import model.Application;

public class ApplicationDao {
	private final DatabaseConnector connector;
	
	public ApplicationDao(DatabaseConnector connector) {
		this.connector = connector;
	}
	
	public void insertApplication(Application app) {
		String sql = "INSERT INTO applications (user_id, job_id, cover_letter) VALUES (?, ?, ?)";
		
		try (Connection conn = connector.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
				
			pstmt.setInt(1, app.getUserId());
			pstmt.setInt(2, app.getJobId());
			pstmt.setString(3, app.getCoverLetter());
			
			pstmt.executeUpdate();
			
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()){
				if (generatedKeys.next()) {
					int no = generatedKeys.getInt(1);
					app.setJobId(no);
					System.out.println(no + "해당 공고에 지원되었습니다.");
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
