package com.revconnect.repository;

import com.revconnect.dao.SecurityQuestionDAO;
import com.revconnect.model.SecurityQuestion;
import com.revconnect.model.UserSecurityAnswer;
import com.revconnect.util.DBConnectionUtil;
import com.revconnect.exception.DatabaseException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SecurityQuestionRepository implements SecurityQuestionDAO {

    @Override
    public List<SecurityQuestion> getAllQuestions() {
        String sql =
                "SELECT * FROM security_questions ORDER BY question_id";

        List<SecurityQuestion> questions = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SecurityQuestion question = new SecurityQuestion();
                question.setQuestionId(rs.getInt("question_id"));
                question.setQuestionText(rs.getString("question_text"));
                questions.add(question);
            }

            return questions;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to retrieve security questions: " + e.getMessage());
        }
    }

    @Override
    public boolean saveAnswer(UserSecurityAnswer answer) {
        String checkSql =
                "SELECT COUNT(*) as count FROM user_security_answers " +
                "WHERE user_id = ? AND question_id = ?";

        String insertSql =
                "INSERT INTO user_security_answers " +
                "(user_id, question_id, answer_hash, created_at) " +
                "VALUES (?, ?, ?, ?)";

        String updateSql =
                "UPDATE user_security_answers SET answer_hash = ? " +
                "WHERE user_id = ? AND question_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection()) {

            boolean exists = false;

            try (PreparedStatement checkPstmt =
                         conn.prepareStatement(checkSql)) {

                checkPstmt.setInt(1, answer.getUserId());
                checkPstmt.setInt(2, answer.getQuestionId());
                ResultSet rs = checkPstmt.executeQuery();

                if (rs.next()) {
                    exists = rs.getInt("count") > 0;
                }
            }

            if (exists) {
                try (PreparedStatement pstmt =
                             conn.prepareStatement(updateSql)) {

                    pstmt.setString(1, answer.getAnswerHash());
                    pstmt.setInt(2, answer.getUserId());
                    pstmt.setInt(3, answer.getQuestionId());

                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        return true;
                    }
                }

            } else {
                try (PreparedStatement pstmt =
                             conn.prepareStatement(insertSql,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    pstmt.setInt(1, answer.getUserId());
                    pstmt.setInt(2, answer.getQuestionId());
                    pstmt.setString(3, answer.getAnswerHash());
                    pstmt.setTimestamp(
                            4, Timestamp.valueOf(LocalDateTime.now()));

                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        ResultSet rs = pstmt.getGeneratedKeys();
                        if (rs.next()) {
                            answer.setAnswerId(rs.getInt(1));
                            return true;
                        }
                    }
                }
            }

            return false;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to save security answer: " + e.getMessage());
        }
    }

    @Override
    public UserSecurityAnswer findAnswer(int userId, int questionId) {
        String sql =
                "SELECT * FROM user_security_answers " +
                "WHERE user_id = ? AND question_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, questionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                UserSecurityAnswer answer = new UserSecurityAnswer();
                answer.setAnswerId(rs.getInt("answer_id"));
                answer.setUserId(rs.getInt("user_id"));
                answer.setQuestionId(rs.getInt("question_id"));
                answer.setAnswerHash(rs.getString("answer_hash"));
                answer.setCreatedAt(
                        rs.getTimestamp("created_at").toLocalDateTime());
                return answer;
            }

            return null;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to find security answer: " + e.getMessage());
        }
    }
}
