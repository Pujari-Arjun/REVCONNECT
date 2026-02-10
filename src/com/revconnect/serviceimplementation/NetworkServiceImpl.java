package com.revconnect.serviceimplementation;

import com.revconnect.service.NetworkService;
import com.revconnect.service.NotificationService;
import com.revconnect.model.User;
import com.revconnect.model.ConnectionRequest;
import com.revconnect.model.Notification;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.repository.FollowRepository;
import com.revconnect.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

public class NetworkServiceImpl implements NetworkService {

    private final ConnectionRepository connectionRepository;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;

    public NetworkServiceImpl() {
        this.connectionRepository = new ConnectionRepository();
        this.followRepository = new FollowRepository();
        this.notificationService = new NotificationServiceImpl();
    }

    @Override
    public boolean sendConnectionRequest(int senderId, int receiverId) {

        if (senderId == receiverId) {
            throw new ValidationException("Cannot send connection request to yourself");
        }

        if (connectionRepository.areConnected(senderId, receiverId)) {
            throw new ValidationException("Already connected with this user");
        }

        boolean sent = connectionRepository.sendRequest(senderId, receiverId);

        if (sent) {
            Notification notification = new Notification();
            notification.setUserId(receiverId);
            notification.setType("CONNECTION_REQUEST");
            notification.setMessage("You have a new connection request");
            notification.setRead(false);
            notification.setRelatedUserId(senderId);
            notification.setCreatedAt(LocalDateTime.now());
            notificationService.createNotification(notification);
        }

        return sent;
    }

    @Override
    public boolean acceptConnectionRequest(int requestId) {

        boolean accepted = connectionRepository.acceptRequest(requestId);

        if (accepted) {
        }

        return accepted;
    }

    @Override
    public boolean rejectConnectionRequest(int requestId) {
        return connectionRepository.rejectRequest(requestId);
    }

    @Override
    public List<ConnectionRequest> getPendingRequests(int userId) {
        return connectionRepository.findPendingRequests(userId);
    }

    @Override
    public List<User> getConnections(int userId) {
        return connectionRepository.findConnections(userId);
    }

    @Override
    public boolean removeConnection(int userId, int connectionId) {

        if (!connectionRepository.areConnected(userId, connectionId)) {
            throw new ValidationException("Not connected with this user");
        }

        return connectionRepository.removeConnection(userId, connectionId);
    }

    @Override
    public boolean followUser(int followerId, int followingId) {

        if (followerId == followingId) {
            throw new ValidationException("Cannot follow yourself");
        }

        boolean followed = followRepository.followUser(followerId, followingId);

        if (followed) {
            Notification notification = new Notification();
            notification.setUserId(followingId);
            notification.setType("NEW_FOLLOWER");
            notification.setMessage("You have a new follower");
            notification.setRead(false);
            notification.setRelatedUserId(followerId);
            notification.setCreatedAt(LocalDateTime.now());
            notificationService.createNotification(notification);
        }

        return followed;
    }

    @Override
    public boolean unfollowUser(int followerId, int followingId) {
        return followRepository.unfollowUser(followerId, followingId);
    }

    @Override
    public List<User> getFollowers(int userId) {
        return followRepository.findFollowers(userId);
    }

    @Override
    public List<User> getFollowing(int userId) {
        return followRepository.findFollowing(userId);
    }
}
