package albabot_backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
	String url = "jdbc:mysql://localhost:3306/albabot";
	String user = "root";
	String password = "youngvkf";
	Connection conn = null;
	
	public Connection getConnection() {
		try {
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("DB 연결 성공");
		} catch(SQLException e) {
			System.out.println("DB 연결 실패");
			e.printStackTrace();
		}
		return conn;
	}
}
