package com.albabot.dao;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
 
import javax.sql.DataSource;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import com.albabot.model.Application;
import com.albabot.model.User;
 
@Repository
public class MypageDao {
 
    @Autowired
    private DataSource dataSource;
 
    // 내 지원 현황 조회 (applications + jobs JOIN)
    public List<Application> findApplicationsByUserId(int userId) {
 
        String sql = """
                SELECT a.application_id, a.user_id, a.job_id,
                       a.applied_at, a.status, a.cover_letter,
                       j.title AS job_title
                FROM applications a
                JOIN jobs j ON a.job_id = j.job_id
                WHERE a.user_id = ?
                ORDER BY a.applied_at DESC
                """;
 
        List<Application> list = new ArrayList<>();
 
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
 
            while (rs.next()) {
                Application app = new Application();
                app.setApplicationId(rs.getInt("application_id"));
                app.setUserId(rs.getInt("user_id"));
                app.setJobId(rs.getInt("job_id"));
                app.setAppliedAt(rs.getTimestamp("applied_at").toLocalDateTime());
                app.setStatus(rs.getString("status"));
                app.setJobTitle(rs.getString("job_title"));
                app.setCoverLetter(rs.getString("cover_letter"));
                list.add(app);
            }
 
        } catch (SQLException e) {
            e.printStackTrace();
        }
 
        return list;
    }
 
    // 내 선호 카테고리 조회
    public List<String> findCategoriesByUserId(int userId) {
 
        String sql = "SELECT category FROM user_categories WHERE user_id = ?";
        List<String> categories = new ArrayList<>();
 
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
 
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
 
        } catch (SQLException e) {
            e.printStackTrace();
        }
 
        return categories;
    }
}
 
