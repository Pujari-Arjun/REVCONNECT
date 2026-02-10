package com.revconnect.dao;

import com.revconnect.model.User;
import java.util.List;

public interface BlockedUserDAO {
    boolean blockUser(int blockerId, int blockedId);
    boolean unblockUser(int blockerId, int blockedId);
    boolean isBlocked(int blockerId, int blockedId);
    List<User> findBlockedUsers(int blockerId);
}