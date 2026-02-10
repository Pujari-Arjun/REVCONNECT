package com.revconnect.serviceimplementation;

import com.revconnect.service.UserService;
import com.revconnect.model.User;
import com.revconnect.model.Profile;
import com.revconnect.model.UserSecurityAnswer;
import com.revconnect.repository.UserRepository;
import com.revconnect.repository.ProfileRepository;
import com.revconnect.repository.SecurityQuestionRepository;
import com.revconnect.util.PasswordUtil;
import com.revconnect.util.ValidationUtil;
import com.revconnect.exception.AuthenticationException;
import com.revconnect.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final SecurityQuestionRepository securityQuestionRepository;

    public UserServiceImpl() {
        this.userRepository = new UserRepository();
        this.profileRepository = new ProfileRepository();
        this.securityQuestionRepository = new SecurityQuestionRepository();
    }

    @Override
    public boolean register(User user, Profile profile) {

        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            throw new ValidationException("Invalid email format");
        }

        if (!ValidationUtil.isValidUsername(user.getUsername())) {
            throw new ValidationException("Invalid username.");
        }

        if (!ValidationUtil.isValidPassword(user.getPassword())) {
            throw new ValidationException("Invalid password.");
        }

        User existingUserByEmail = userRepository.findByEmail(user.getEmail());
        if (existingUserByEmail != null) {
            throw new ValidationException("Email already registered");
        }

        User existingUserByUsername = userRepository.findByUsername(user.getUsername());
        if (existingUserByUsername != null) {
            throw new ValidationException("Username already taken");
        }

        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        user.setCreatedAt(LocalDateTime.now());

        boolean userCreated = userRepository.registerUser(user);

        if (userCreated && user.getUserId() > 0) {
            profile.setUserId(user.getUserId());
            profile.setCreatedAt(LocalDateTime.now());
            boolean profileCreated = profileRepository.createProfile(profile);

            if (profileCreated) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    @Override
    public User login(String emailOrUsername, String password) {

        if (emailOrUsername == null || emailOrUsername.trim().isEmpty()) {
            throw new AuthenticationException("Email/Username cannot be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Password cannot be empty");
        }

        User user = null;

        if (emailOrUsername.contains("@")) {
            user = userRepository.findByEmail(emailOrUsername);
        } else {
            user = userRepository.findByUsername(emailOrUsername);
        }

        if (user == null) {
            throw new AuthenticationException("Invalid email/username or password");
        }

        boolean passwordMatches =
                PasswordUtil.checkPassword(password, user.getPassword());

        if (!passwordMatches) {
            throw new AuthenticationException("Invalid email/username or password");
        }

        return user;
    }

    @Override
    public boolean changePassword(int userId,
                                  String currentPassword,
                                  String newPassword) {

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        boolean currentPasswordMatches =
                PasswordUtil.checkPassword(
                        currentPassword,
                        user.getPassword());

        if (!currentPasswordMatches) {
            throw new AuthenticationException("Current password is incorrect");
        }

        if (!ValidationUtil.isValidPassword(newPassword)) {
            throw new ValidationException(
                    "New password must be at least 8 characters with 1 uppercase and 1 number");
        }

        if (currentPassword.equals(newPassword)) {
            throw new ValidationException(
                    "New password must be different from current password");
        }

        String newHashedPassword =
                PasswordUtil.hashPassword(newPassword);

        boolean updated =
                userRepository.updatePassword(
                        userId,
                        newHashedPassword);

        if (updated) {
            return true;
        }

        return false;
    }

    @Override
    public boolean resetPassword(String email,
                                 int questionId,
                                 String answer,
                                 String newPassword) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("Email not found");
        }

        UserSecurityAnswer savedAnswer =
                securityQuestionRepository.findAnswer(
                        user.getUserId(),
                        questionId);

        if (savedAnswer == null) {
            throw new AuthenticationException(
                    "Security question not answered for this account");
        }

        boolean answerMatches =
                PasswordUtil.checkPassword(
                        answer.trim().toLowerCase(),
                        savedAnswer.getAnswerHash());

        if (!answerMatches) {
            throw new AuthenticationException(
                    "Security answer is incorrect");
        }

        if (!ValidationUtil.isValidPassword(newPassword)) {
            throw new ValidationException(
                    "New password must be at least 8 characters with 1 uppercase and 1 number");
        }

        String newHashedPassword =
                PasswordUtil.hashPassword(newPassword);

        boolean updated =
                userRepository.updatePassword(
                        user.getUserId(),
                        newHashedPassword);

        if (updated) {
            return true;
        }

        return false;
    }

    @Override
    public List<User> searchUsers(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new ValidationException("Search keyword cannot be empty");
        }

        String sanitizedKeyword =
                ValidationUtil.sanitizeInput(keyword);

        return userRepository.searchUsers(sanitizedKeyword);
    }

    @Override
    public Profile viewProfile(int userId) {
        return profileRepository.findByUserId(userId);
    }

    @Override
    public boolean updateProfile(Profile profile) {

        if (profile.getName() != null
                && profile.getName().trim().isEmpty()) {

            throw new ValidationException("Name cannot be empty");
        }

        profile.setUpdatedAt(LocalDateTime.now());
        return profileRepository.updateProfile(profile);
    }

    @Override
    public boolean updatePrivacy(int userId,
                                 boolean isPrivate) {
        return userRepository.updatePrivacy(userId, isPrivate);
    }
}
