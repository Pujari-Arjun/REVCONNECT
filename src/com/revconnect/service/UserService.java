package com.revconnect.service;

import com.revconnect.model.User;
import com.revconnect.model.Profile;
import java.util.List;

public interface UserService {
    boolean register(User user, Profile profile);
    User login(String emailOrUsername, String password);
    boolean changePassword(int userId, String currentPassword, String newPassword);
    boolean resetPassword(String email, int questionId, String answer, String newPassword);
    List<User> searchUsers(String keyword);
    Profile viewProfile(int userId);
    boolean updateProfile(Profile profile);
    boolean updatePrivacy(int userId, boolean isPrivate);
}