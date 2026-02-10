package com.revconnect.serviceimplementation;

import com.revconnect.service.MessageService;
import com.revconnect.model.Message;
import com.revconnect.model.User;
import com.revconnect.repository.MessageRepository;
import com.revconnect.repository.BlockedUserRepository;
import com.revconnect.configuration.AppConfig;
import com.revconnect.exception.MessagingException;
import com.revconnect.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final BlockedUserRepository blockedUserRepository;

    public MessageServiceImpl() {
        this.messageRepository = new MessageRepository();
        this.blockedUserRepository = new BlockedUserRepository();
    }

    @Override
    public boolean sendMessage(Message message) {

        if (message.getSenderId() == message.getReceiverId()) {
            throw new MessagingException("Cannot send message to yourself");
        }

        if (blockedUserRepository.isBlocked(message.getReceiverId(), message.getSenderId())) {
            throw new MessagingException("You have been blocked by this user");
        }

        if (blockedUserRepository.isBlocked(message.getSenderId(), message.getReceiverId())) {
            throw new MessagingException("You have blocked this user. Unblock to send messages");
        }

        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty()) {
            throw new ValidationException("Message cannot be empty");
        }

        if (message.getMessageText().length() > AppConfig.MAX_MESSAGE_LENGTH) {
            throw new ValidationException(
                    "Message exceeds maximum length of "
                            + AppConfig.MAX_MESSAGE_LENGTH
                            + " characters");
        }

        message.setCreatedAt(LocalDateTime.now());
        message.setRead(false);

        return messageRepository.sendMessage(message);
    }

    @Override
    public List<Message> getConversation(int userId, int otherUserId) {
        return messageRepository.getConversation(userId, otherUserId);
    }

    @Override
    public boolean markConversationAsRead(int userId, int otherUserId) {
        return messageRepository.markAsRead(userId, otherUserId);
    }

    @Override
    public boolean deleteConversation(int userId, int otherUserId) {
        return messageRepository.deleteConversation(userId, otherUserId);
    }

    @Override
    public int getUnreadMessageCount(int userId) {
        return messageRepository.getUnreadCount(userId);
    }

    @Override
    public boolean blockUser(int blockerId, int blockedId) {

        if (blockerId == blockedId) {
            throw new ValidationException("Cannot block yourself");
        }

        boolean blocked = blockedUserRepository.blockUser(blockerId, blockedId);

        if (blocked) {
            messageRepository.deleteConversation(blockerId, blockedId);
        }

        return blocked;
    }

    @Override
    public boolean unblockUser(int blockerId, int blockedId) {
        return blockedUserRepository.unblockUser(blockerId, blockedId);
    }

    @Override
    public List<User> getBlockedUsers(int blockerId) {
        return blockedUserRepository.findBlockedUsers(blockerId);
    }
}
