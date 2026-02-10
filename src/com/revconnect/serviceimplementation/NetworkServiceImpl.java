package com.revconnect.serviceimplementation;

import com.revconnect.service.NetworkService;
import com.revconnect.service.NotificationService;
import com.revconnect.model.User;
import com.revconnect.repo.ConnectionRepo;
import com.revconnect.repo.FollowRepo;
import com.revconnect.model.ConnectionRequest;
import com.revconnect.model.Notification;
import com.revconnect.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

public class NetworkServiceImpl implements NetworkService {

    private final ConnectionRepo connectionRepo;
    private final FollowRepo followRepo;
    private final NotificationService notificationService;

    public NetworkServiceImpl() {
        this.connectionRepo = new ConnectionRepo();
        this.followRepo = new FollowRepo();
        this.notificationService = new NotificationServiceImpl();
    }

    @Override
    public boolean sendConnectionRequest(int senderId, int receiverId) {

        if (senderId == receiverId) {
            throw new ValidationException("Cannot send connection request to yourself");
        }

        if (connectionRepo.areConnected(senderId, receiverId)) {
            throw new ValidationException("Already connected with this user");
        }

        boolean sent = connectionRepo.sendRequest(senderId, receiverId);

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

        boolean accepted = connectionRepo.acceptRequest(requestId);

        if (accepted) {
        }

        return accepted;
    }

    @Override
    public boolean rejectConnectionRequest(int requestId) {
        return connectionRepo.rejectRequest(requestId);
    }

    @Override
    public List<ConnectionRequest> getPendingRequests(int userId) {
        return connectionRepo.findPendingRequests(userId);
    }

    @Override
    public List<User> getConnections(int userId) {
        return connectionRepo.findConnections(userId);
    }

    @Override
    public boolean removeConnection(int userId, int connectionId) {

        if (!connectionRepo.areConnected(userId, connectionId)) {
            throw new ValidationException("Not connected with this user");
        }

        return connectionRepo.removeConnection(userId, connectionId);
    }

    @Override
    public boolean followUser(int followerId, int followingId) {

        if (followerId == followingId) {
            throw new ValidationException("Cannot follow yourself");
        }

        boolean followed = followRepo.followUser(followerId, followingId);

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
        return followRepo.unfollowUser(followerId, followingId);
    }

    @Override
    public List<User> getFollowers(int userId) {
        return followRepo.findFollowers(userId);
    }

    @Override
    public List<User> getFollowing(int userId) {
        return followRepo.findFollowing(userId);
    }
}
