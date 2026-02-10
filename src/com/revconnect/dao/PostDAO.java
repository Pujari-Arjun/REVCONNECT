package com.revconnect.dao;

import com.revconnect.model.Post;
import com.revconnect.model.PostAnalytics;
import java.util.List;

public interface PostDAO {
    boolean createPost(Post post);
    boolean updatePost(Post post);
    boolean deletePost(int postId);
    Post findById(int postId);
    List<Post> findByUserId(int userId);
    List<Post> getFeed(int userId);
    List<Post> getTrendingPosts();
    List<Post> searchByHashtag(String hashtag);
    List<Post> getFilteredFeed(int userId, String postType, String userType);
    boolean schedulePost(Post post);
    boolean pinPost(int postId, int userId);
    boolean unpinPost(int postId, int userId);
    PostAnalytics getAnalytics(int postId);
}