package com.revconnect.ui;

import com.revconnect.model.User;
import com.revconnect.model.SecurityQuestion;
import com.revconnect.service.UserService;
import com.revconnect.serviceimpl.UserServiceImpl;
import com.revconnect.repository.SecurityQuestionRepository;
import com.revconnect.exception.AuthenticationException;
import com.revconnect.exception.ValidationException;

import java.util.List;
import java.util.Scanner;

public class LoginUI {
    
    private UserService userService;
    private SecurityQuestionRepository securityQuestionRepository;
    private Scanner scanner;
    
    public LoginUI() {
        this.userService = new UserServiceImpl();
        this.securityQuestionRepository = new SecurityQuestionRepository();
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        boolean running = true;
        
        while (running) {
            displayMainMenu();
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1:
                        login();
                        break;
                    case 2:
                        forgotPassword();
                        break;
                    case 3:
                        navigateToRegistration();
                        break;
                    case 0:
                        running = false;
                        System.out.println("\nThank you for using RevConnect. Goodbye!");
                        break;
                    default:
                        System.out.println("\nInvalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a number.");
            } catch (Exception e) {
                System.out.println("\nAn error occurred: " + e.getMessage());
            }
        }
    }
    
    private void displayMainMenu() {
    	System.out.println("\n-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
    	System.out.println("           REVCONNECT PORTAL");
    	System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
    	System.out.println("[1] Login");
    	System.out.println("[2] Forgot Password");
    	System.out.println("[3] Register New Account");
    	System.out.println("[0] Exit Application");
    	System.out.println("----------------------------------------");
    	System.out.print("Enter selection: ");

    }
    
    private void login() {
    	System.out.println("\n=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
    	System.out.println("           Welcome Back!");
    	System.out.println("                 LOGIN");
    	System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");

        
        System.out.print("Enter Email or Username: ");
        String emailOrUsername = scanner.nextLine().trim();
        
        if (emailOrUsername.isEmpty()) {
            System.out.println("\nError: Email or username cannot be empty.");
            return;
        }
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        
        if (password.isEmpty()) {
            System.out.println("\nError: Password cannot be empty.");
            return;
        }
        
        try {
            User user = userService.login(emailOrUsername, password);
            
            if (user != null) {
                System.out.println("\n=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
                System.out.println("Login successful! Welcome, " + user.getUsername());
                System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
                
                // Navigate to User Menu
                UserMenuUI userMenuUI = new UserMenuUI(user);
                userMenuUI.start();
            } else {
                System.out.println("\nLogin failed. Invalid credentials.");
            }
        } catch (AuthenticationException e) {
            System.out.println("\nLogin failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nAn error occurred during login: " + e.getMessage());
        }
    }
    
    private void forgotPassword() {
    	System.out.println("\n=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
    	System.out.println("        Trouble Logging In?");
    	System.out.println("          RESET PASSWORD");
    	System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");

        
        System.out.print("Enter your Email: ");
        String email = scanner.nextLine().trim();
        
        if (email.isEmpty()) {
            System.out.println("\nError: Email cannot be empty.");
            return;
        }
        
        try {
            // Get all security questions
            List<SecurityQuestion> questions = securityQuestionRepository.getAllQuestions();
            
            if (questions.isEmpty()) {
                System.out.println("\nError: No security questions available.");
                return;
            }
            
            // Display security questions
            System.out.println("\nSelect your security question:");
            for (int i = 0; i < questions.size(); i++) {
                System.out.println((i + 1) + ". " + questions.get(i).getQuestionText());
            }
            
            System.out.print("\nEnter question number: ");
            int questionIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            if (questionIndex < 0 || questionIndex >= questions.size()) {
                System.out.println("\nError: Invalid question selection.");
                return;
            }
            
            SecurityQuestion selectedQuestion = questions.get(questionIndex);
            
            System.out.print("Enter your answer: ");
            String answer = scanner.nextLine().trim();
            
            if (answer.isEmpty()) {
                System.out.println("\nError: Answer cannot be empty.");
                return;
            }
            
            System.out.print("Enter new password (minimum 8 characters): ");
            String newPassword = scanner.nextLine();
            
            if (newPassword.length() <= 8) {
                System.out.println("\nError: Password must be at least 8 characters long.");
                return;
            }
            
            System.out.print("Confirm new password: ");
            String confirmPassword = scanner.nextLine();
            
            if (!newPassword.equals(confirmPassword)) {
                System.out.println("\nError: Passwords do not match.");
                return;
            }
            
            // Attempt password reset
            boolean success = userService.resetPassword(
                email, 
                selectedQuestion.getQuestionId(), 
                answer, 
                newPassword
            );
            
            if (success) {
            	System.out.println("\n=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
            	System.out.println(" Your password has been reset!");
            	System.out.println(" You're all set — go ahead and login.");
            	System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");

            } else {
                System.out.println("\nPassword reset failed. Please verify your email and security answer.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("\nError: Invalid input. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("\nError during password reset: " + e.getMessage());
        }
    }
    
    private void navigateToRegistration() {
        RegistrationUI registrationUI = new RegistrationUI();
        registrationUI.start();
    }
}