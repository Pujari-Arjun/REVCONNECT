package com.revconnect.service;

import com.revconnect.model.Message;
import com.revconnect.model.User;
import java.util.List;

public interface MessageService {
    boolean sendMessage(Message message);
    List<Message> getConversation(int userId, int otherUserId);
    boolean markConversationAsRead(int userId, int otherUserId);
    boolean deleteConversation(int userId, int otherUserId);
    int getUnreadMessageCount(int userId);
    boolean blockUser(int blockerId, int blockedId);
    boolean unblockUser(int blockerId, int blockedId);
    List<User> getBlockedUsers(int blockerId);
}