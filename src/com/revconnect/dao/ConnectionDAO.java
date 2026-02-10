package com.revconnect.dao;

import com.revconnect.model.ConnectionRequest;
import com.revconnect.model.User;
import java.util.List;

public interface ConnectionDAO {
    boolean sendRequest(int senderId, int receiverId);
    boolean acceptRequest(int requestId);
    boolean rejectRequest(int requestId);
    List<ConnectionRequest> findPendingRequests(int userId);
    List<User> findConnections(int userId);
    boolean removeConnection(int userId, int connectionId);
    boolean areConnected(int userId1, int userId2);
}