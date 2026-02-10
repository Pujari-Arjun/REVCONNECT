package com.revconnect.service;

import com.revconnect.model.Post;
import com.revconnect.model.PostAnalytics;
import com.revconnect.model.Comment;
import java.util.List;

public interface PostService {
    boolean createPost(Post post);
    boolean updatePost(Post post);
    boolean deletePost(int postId, int userId);
    List<Post> getMyPosts(int userId);
    List<Post> getFeed(int userId);
    List<Post> getFilteredFeed(int userId, String postType, String userType);
    List<Post> getTrendingPosts();
    List<Post> searchByHashtag(String hashtag);
    boolean likePost(int postId, int userId);
    boolean unlikePost(int postId, int userId);
    boolean commentOnPost(int postId, int userId, String commentText);
    List<Comment> getComments(int postId);
    boolean deleteComment(int commentId, int userId);
    boolean sharePost(int postId, int userId);
    boolean schedulePost(Post post);
    boolean pinPost(int postId, int userId);
    PostAnalytics getAnalytics(int postId);
}