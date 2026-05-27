package com.albabot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource; // DatabaseConnector 대신 스프링 표준 DataSource 사용
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.albabot.model.Application; // 잘못되었던 패키지 임포트 경로 수정

@Repository // 스프링 컨테이너에 빈으로 등록
public class ApplicationDao {

	@Autowired
	private DataSource dataSource; // 스프링 DataSource 자동 주입
	
	public void insertApplication(Application app) {
		String sql = "INSERT INTO applications (user_id, job_id, cover_letter) VALUES (?, ?, ?)";
		
		try (Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
				
			pstmt.setInt(1, app.getUserId());
			pstmt.setInt(2, app.getJobId());
			pstmt.setString(3, app.getCoverLetter());
			
			pstmt.executeUpdate();
			
			// 데이터베이스에서 자동으로 발행된 applications 테이블의 PK(application_id)를 가져옴
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()){
				if (generatedKeys.next()) {
					int no = generatedKeys.getInt(1);
					
					// 생성된 지원서 번호(application_id)를 객체에 저장
					app.setApplicationId(no); 
					System.out.println(no + "번 지원서로 해당 공고에 지원되었습니다.");
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}