package com.revconnect.dao;

public interface LikeDAO {
    boolean addLike(int postId, int userId);
    boolean removeLike(int postId, int userId);
    int getLikeCount(int postId);
    boolean hasUserLiked(int postId, int userId);
}