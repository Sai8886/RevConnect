package com.revconnect.serviceimplementation;

import com.revconnect.service.MessageService;
import com.revconnect.model.Message;
import com.revconnect.model.User;
import com.revconnect.repo.BlockedUserRepo;
import com.revconnect.repo.MessageRepo;
import com.revconnect.configuration.ApplicationConfiguration;
import com.revconnect.exception.MessagingException;
import com.revconnect.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

public class MessageServiceImpl implements MessageService {

    private final MessageRepo messageRepo;
    private final BlockedUserRepo blockedUserRepo;

    public MessageServiceImpl() {
        this.messageRepo = new MessageRepo();
        this.blockedUserRepo = new BlockedUserRepo();
    }

    @Override
    public boolean sendMessage(Message message) {

        if (message.getSenderId() == message.getReceiverId()) {
            throw new MessagingException("Cannot send message to yourself");
        }

        if (blockedUserRepo.isBlocked(message.getReceiverId(), message.getSenderId())) {
            throw new MessagingException("You have been blocked by this user");
        }

        if (blockedUserRepo.isBlocked(message.getSenderId(), message.getReceiverId())) {
            throw new MessagingException("You have blocked this user. Unblock to send messages");
        }

        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty()) {
            throw new ValidationException("Message cannot be empty");
        }

        if (message.getMessageText().length() > ApplicationConfiguration.MAX_MESSAGE_LENGTH) {
            throw new ValidationException(
                    "Message exceeds maximum length of "
                            + ApplicationConfiguration.MAX_MESSAGE_LENGTH
                            + " characters");
        }

        message.setCreatedAt(LocalDateTime.now());
        message.setRead(false);

        return messageRepo.sendMessage(message);
    }

    @Override
    public List<Message> getConversation(int userId, int otherUserId) {
        return messageRepo.getConversation(userId, otherUserId);
    }

    @Override
    public boolean markConversationAsRead(int userId, int otherUserId) {
        return messageRepo.markAsRead(userId, otherUserId);
    }

    @Override
    public boolean deleteConversation(int userId, int otherUserId) {
        return messageRepo.deleteConversation(userId, otherUserId);
    }

    @Override
    public int getUnreadMessageCount(int userId) {
        return messageRepo.getUnreadCount(userId);
    }

    @Override
    public boolean blockUser(int blockerId, int blockedId) {

        if (blockerId == blockedId) {
            throw new ValidationException("Cannot block yourself");
        }

        boolean blocked = blockedUserRepo.blockUser(blockerId, blockedId);

        if (blocked) {
            messageRepo.deleteConversation(blockerId, blockedId);
        }

        return blocked;
    }

    @Override
    public boolean unblockUser(int blockerId, int blockedId) {
        return blockedUserRepo.unblockUser(blockerId, blockedId);
    }

    @Override
    public List<User> getBlockedUsers(int blockerId) {
        return blockedUserRepo.findBlockedUsers(blockerId);
    }
}
