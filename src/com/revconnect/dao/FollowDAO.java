package com.revconnect.dao;

import com.revconnect.model.User;
import java.util.List;

public interface FollowDAO {
    boolean followUser(int followerId, int followingId);
    boolean unfollowUser(int followerId, int followingId);
    List<User> findFollowers(int userId);
    List<User> findFollowing(int userId);
    boolean isFollowing(int followerId, int followingId);
}