package com.albabot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.albabot.model.User;
import com.albabot.model.Role;

@Repository
public class UserDao {
	
	@Autowired
	private DataSource dataSource;
	
	public User findByEmail(String email){
		
		String sql = "SELECT * FROM users WHERE email = ?";
		
		try (Connection conn = dataSource.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)
				){
					pstmt.setString(1, email);
					ResultSet rs = pstmt.executeQuery();
			
					if(rs.next()) {
						User u = new User();
						
						u.setUserId(rs.getInt("user_id"));
						u.setEmail(rs.getString("email"));
						u.setPasswordHash(rs.getString("password_hash"));
						u.setName(rs.getString("name"));
						u.setPhone(rs.getString("phone"));
						u.setRole(Role.valueOf(rs.getString("role")));
						u.setRegion(rs.getString("region"));
						u.setPreferredTime(rs.getString("preferred_time"));
						u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
						u.setActive(rs.getBoolean("is_active"));
						
						System.out.println(rs.getString("email"));
						return u;
					}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return null;
	}
}
