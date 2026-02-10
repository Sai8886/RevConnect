package com.revconnect.serviceimplementation;

import com.revconnect.service.NotificationService;
import com.revconnect.model.Notification;
import com.revconnect.model.NotificationPreference;
import com.revconnect.repo.NotificationRepo;

import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepo notificationRepo;

    public NotificationServiceImpl() {
        this.notificationRepo = new NotificationRepo();
    }

    @Override
    public boolean createNotification(Notification notification) {

        NotificationPreference preferences =
                notificationRepo.getPreferences(notification.getUserId());

        boolean shouldNotify =
                shouldCreateNotification(notification.getType(), preferences);

        if (!shouldNotify) {
            return false;
        }

        return notificationRepo.saveNotification(notification);
    }

    @Override
    public List<Notification> getNotifications(int userId) {
        return notificationRepo.findByUserId(userId);
    }

    @Override
    public int getUnreadCount(int userId) {
        return notificationRepo.countUnread(userId);
    }

    @Override
    public boolean markAsRead(int notificationId) {
        return notificationRepo.markAsRead(notificationId);
    }

    @Override
    public boolean markAllAsRead(int userId) {
        return notificationRepo.markAllAsRead(userId);
    }

    @Override
    public NotificationPreference getPreferences(int userId) {
        return notificationRepo.getPreferences(userId);
    }

    @Override
    public boolean updatePreferences(NotificationPreference preferences) {
        return notificationRepo.updatePreferences(preferences);
    }

    private boolean shouldCreateNotification(
            String notificationType,
            NotificationPreference preferences) {

        switch (notificationType) {
            case "CONNECTION_REQUEST":
                return preferences.isConnectionRequests();
            case "NEW_FOLLOWER":
                return preferences.isNewFollowers();
            case "LIKE":
                return preferences.isLikes();
            case "COMMENT":
                return preferences.isComments();
            case "SHARE":
                return preferences.isShares();
            case "NEW_POST":
                return preferences.isNewPosts();
            default:
                return true;
        }
    }
}
