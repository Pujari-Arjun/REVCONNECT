// FILE LOCATION: src/main/java/com/revconnect/ui/RegistrationUI.java

package com.revconnect.ui;

import com.revconnect.model.User;
import com.revconnect.model.Profile;
import com.revconnect.model.SecurityQuestion;
import com.revconnect.model.UserSecurityAnswer;
import com.revconnect.service.UserService;
import com.revconnect.serviceimpl.UserServiceImpl;
import com.revconnect.repository.SecurityQuestionRepository;
import com.revconnect.util.ValidationUtil;
import com.revconnect.util.PasswordUtil;
import com.revconnect.exception.ValidationException;

import java.util.List;
import java.util.Scanner;

public class RegistrationUI {
    
    private UserService userService;
    private SecurityQuestionRepository securityQuestionRepository;
    private Scanner scanner;
    
    public RegistrationUI() {
        this.userService = new UserServiceImpl();
        this.securityQuestionRepository = new SecurityQuestionRepository();
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        System.out.println("\n========================================");
        System.out.println("       USER REGISTRATION");
        System.out.println("========================================");
        
        try {
            // Collect user information
            User user = collectUserInfo();
            if (user == null) {
                System.out.println("\nRegistration cancelled.");
                return;
            }
            
            // Collect profile information
            Profile profile = collectProfileInfo(user.getUserType());
            if (profile == null) {
                System.out.println("\nRegistration cancelled.");
                return;
            }
            
            // Register user - returns boolean
            boolean registrationSuccess = userService.register(user, profile);
            
            if (registrationSuccess && user.getUserId() > 0) {
                // Setup security question
                setupSecurityQuestion(user.getUserId());
                
                System.out.println("\n========================================");
                System.out.println("Registration successful!");
                System.out.println("Username: " + user.getUsername());
                System.out.println("You can now login with your credentials.");
                System.out.println("========================================");
            } else {
                System.out.println("\nRegistration failed. Email or username may already exist.");
            }
            
        } catch (ValidationException e) {
            System.out.println("\nValidation error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid input. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("\nRegistration error: " + e.getMessage());
        }
    }
    
    private User collectUserInfo() {
        try {
            User user = new User();
            
            // Select user type
            System.out.println("\nChoose Your Account Type:");
            System.out.println("1. Personal  - Connect with friends and family");
            System.out.println("2. Business  - Promote your company or brand");
            System.out.println("3. Creator   - Share content and grow an audience");
            System.out.print("Enter your choice (1-3): ");

            
            int typeChoice = Integer.parseInt(scanner.nextLine().trim());
            String userType;
            
            switch (typeChoice) {
                case 1:
                    userType = "PERSONAL";
                    break;
                case 2:
                    userType = "BUSINESS";
                    break;
                case 3:
                    userType = "CREATOR";
                    break;
                default:
                    System.out.println("\nInvalid choice.");
                    return null;
            }
            
            user.setUserType(userType);
            
            // Email
            while (true) {
                System.out.print("\nEnter Email: ");
                String email = scanner.nextLine().trim();
                
                if (!ValidationUtil.isValidEmail(email)) {
                    System.out.println("Invalid email format. Please try again.");
                    continue;
                }
                
                user.setEmail(email);
                break;
            }
            
            // Username
            while (true) {
                System.out.print("Enter Username: ");
                String username = scanner.nextLine().trim();
                
                if (!ValidationUtil.isValidUsername(username)) {
                    System.out.println("Invalid username");
                    continue;
                }
                
                user.setUsername(username);
                break;
            }
            
            // Password
            while (true) {
                System.out.print("Enter Password (minimum 8 characters): ");
                String password = scanner.nextLine();
                
                if (!ValidationUtil.isValidPassword(password)) {
                    System.out.println("Password must be at least 8 characters long.");
                    continue;
                }
                
                System.out.print("Confirm Password: ");
                String confirmPassword = scanner.nextLine();
                
                if (!password.equals(confirmPassword)) {
                    System.out.println("Passwords do not match. Please try again.");
                    continue;
                }
                
                // Set the plain password - UserService.register() will hash it
                user.setPassword(password);
                break;
            }
            
            // Privacy setting
            System.out.print("Make account private? (yes/no): ");
            String privacyInput = scanner.nextLine().trim().toLowerCase();
            user.setPrivate(privacyInput.equals("yes") || privacyInput.equals("y"));
            
            return user;
            
        } catch (Exception e) {
            System.out.println("\nError collecting user information: " + e.getMessage());
            return null;
        }
    }
    
    private Profile collectProfileInfo(String userType) {
        try {
            Profile profile = new Profile();
            
            System.out.println("\n--- Profile Information ---");
            
            // Name
            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name is required.");
                return null;
            }
            profile.setName(name);
            
            // Bio
            System.out.print("Enter Bio (optional): ");
            String bio = scanner.nextLine().trim();
            if (!bio.isEmpty()) {
                profile.setBio(bio);
            }
            
            // Location
            System.out.print("Enter Location (optional): ");
            String location = scanner.nextLine().trim();
            if (!location.isEmpty()) {
                profile.setLocation(location);
            }
            
            // Website
            System.out.print("Enter Website URL (optional): ");
            String website = scanner.nextLine().trim();
            if (!website.isEmpty()) {
                profile.setWebsite(website);
            }
            
            // Business/Creator specific fields
            if (userType.equals("BUSINESS") || userType.equals("CREATOR")) {
                System.out.print("Enter Category (e.g., Technology, Fashion, Food): ");
                String category = scanner.nextLine().trim();
                if (!category.isEmpty()) {
                    profile.setCategory(category);
                }
                
                System.out.print("Enter Contact Info (phone/email): ");
                String contactInfo = scanner.nextLine().trim();
                if (!contactInfo.isEmpty()) {
                    profile.setContactInfo(contactInfo);
                }
            }
            
            // Business specific fields
            if (userType.equals("BUSINESS")) {
                System.out.print("Enter Business Address: ");
                String businessAddress = scanner.nextLine().trim();
                if (!businessAddress.isEmpty()) {
                    profile.setBusinessAddress(businessAddress);
                }
                
                System.out.print("Enter Business Hours (e.g., Mon-Fri 9AM-5PM): ");
                String businessHours = scanner.nextLine().trim();
                if (!businessHours.isEmpty()) {
                    profile.setBusinessHours(businessHours);
                }
            }
            
            return profile;
            
        } catch (Exception e) {
            System.out.println("\nError collecting profile information: " + e.getMessage());
            return null;
        }
    }
    
    private void setupSecurityQuestion(int userId) {
        try {
            // Get all security questions
            List<SecurityQuestion> questions = securityQuestionRepository.getAllQuestions();
            
            if (questions.isEmpty()) {
                System.out.println("\nWarning: No security questions available. Skipping security setup.");
                return;
            }
            
            System.out.println("\n--- Security Question Setup ---");
            System.out.println("Select a security question for password recovery:");
            
            for (int i = 0; i < questions.size(); i++) {
                System.out.println((i + 1) + ". " + questions.get(i).getQuestionText());
            }
            
            System.out.print("\nEnter question number: ");
            int questionIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            if (questionIndex < 0 || questionIndex >= questions.size()) {
                System.out.println("Invalid question selection. Skipping security setup.");
                return;
            }
            
            SecurityQuestion selectedQuestion = questions.get(questionIndex);
            
            System.out.print("Enter your answer: ");
            String answer = scanner.nextLine().trim();
            
            if (answer.isEmpty()) {
                System.out.println("Answer cannot be empty. Skipping security setup.");
                return;
            }
            
            // Create and save security answer
            UserSecurityAnswer userAnswer = new UserSecurityAnswer();
            userAnswer.setUserId(userId);
            userAnswer.setQuestionId(selectedQuestion.getQuestionId());
            userAnswer.setAnswerHash(PasswordUtil.hashPassword(answer));
            
            securityQuestionRepository.saveAnswer(userAnswer);
            
            System.out.println("Security question configured successfully.");
            
        } catch (Exception e) {
            System.out.println("Error setting up security question: " + e.getMessage());
        }
    }
}