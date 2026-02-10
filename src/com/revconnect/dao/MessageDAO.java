package com.revconnect.dao;

import com.revconnect.model.Message;
import java.util.List;

public interface MessageDAO {
    boolean sendMessage(Message message);
    List<Message> getConversation(int userId, int otherUserId);
    boolean markAsRead(int userId, int otherUserId);
    boolean deleteConversation(int userId, int otherUserId);
    int getUnreadCount(int userId);
}