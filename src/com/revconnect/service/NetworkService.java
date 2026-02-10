package com.revconnect.service;

import com.revconnect.model.User;
import com.revconnect.model.ConnectionRequest;
import java.util.List;

public interface NetworkService {
    boolean sendConnectionRequest(int senderId, int receiverId);
    boolean acceptConnectionRequest(int requestId);
    boolean rejectConnectionRequest(int requestId);
    List<ConnectionRequest> getPendingRequests(int userId);
    List<User> getConnections(int userId);
    boolean removeConnection(int userId, int connectionId);
    boolean followUser(int followerId, int followingId);
    boolean unfollowUser(int followerId, int followingId);
    List<User> getFollowers(int userId);
    List<User> getFollowing(int userId);
}