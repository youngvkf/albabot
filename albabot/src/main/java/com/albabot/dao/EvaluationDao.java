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
import com.albabot.model.Evaluation;

@Repository
public class EvaluationDao {

    @Autowired
    private DataSource dataSource;

    // 1. 특정 공고의 리뷰 목록을 작성자 이름과 함께 가져오기 (JOIN 활용)
    public List<Evaluation> getEvaluationsByJobId(int jobId) {
        List<Evaluation> list = new ArrayList<>();
        String sql = "SELECT e.*, u.name AS reviewer_name " +
                     "FROM evaluations e " +
                     "JOIN users u ON e.reviewer_id = u.user_id " +
                     "WHERE e.job_id = ? " +
                     "ORDER BY e.created_at DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jobId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Evaluation eval = new Evaluation();
                    eval.setEvalId(rs.getInt("eval_id"));
                    eval.setReviewerId(rs.getInt("reviewer_id"));
                    eval.setRevieweeId(rs.getInt("reviewee_id"));
                    eval.setJobId(rs.getInt("job_id"));
                    eval.setScore(rs.getInt("score"));
                    eval.setComment(rs.getString("comment"));
                    eval.setEvalType(rs.getString("eval_type"));
                    eval.setReviewerName(rs.getString("reviewer_name")); // JOIN 결과 매핑
                    list.add(eval);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. 새로운 업무 리뷰 등록하기
    public void insertEvaluation(Evaluation eval) {
        String sql = "INSERT INTO evaluations (reviewer_id, reviewee_id, job_id, score, comment, eval_type) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, eval.getReviewerId());
            pstmt.setInt(2, eval.getRevieweeId());
            pstmt.setInt(3, eval.getJobId());
            pstmt.setInt(4, eval.getScore());
            pstmt.setString(5, eval.getComment());
            pstmt.setString(6, eval.getEvalType());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}