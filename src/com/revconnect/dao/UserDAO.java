package com.revconnect.dao;

import com.revconnect.model.User;
import java.util.List;

public interface UserDAO {
    boolean registerUser(User user);
    User findByEmail(String email);
    User findByUsername(String username);
    User findById(int userId);
    boolean updatePassword(int userId, String newPasswordHash);
    List<User> searchUsers(String keyword);
    boolean updatePrivacy(int userId, boolean isPrivate);
}