package com.revconnect.dao;

import com.revconnect.model.Comment;
import java.util.List;

public interface CommentDAO {
    boolean addComment(int postId, int userId, String text);
    List<Comment> findByPostId(int postId);
    boolean deleteComment(int commentId, int userId);
}