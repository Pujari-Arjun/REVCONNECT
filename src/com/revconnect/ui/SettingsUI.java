// FILE LOCATION: src/main/java/com/revconnect/ui/SettingsUI.java

package com.revconnect.ui;

import com.revconnect.model.User;
import com.revconnect.model.Profile;
import com.revconnect.model.NotificationPreference;
import com.revconnect.service.UserService;
import com.revconnect.serviceimplementation.NotificationServiceImpl;
import com.revconnect.serviceimplementation.UserServiceImpl;
import com.revconnect.service.NotificationService;

import java.util.Scanner;

public class SettingsUI {
    
    private User loggedInUser;
    private UserService userService;
    private NotificationService notificationService;
    private Scanner scanner;
    
    public SettingsUI(User user) {
        this.loggedInUser = user;
        this.userService = new UserServiceImpl();
        this.notificationService = new NotificationServiceImpl();
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        boolean running = true;
        
        while (running) {
            displayMenu();
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1: viewProfile(); break;
                    case 2: editProfile(); break;
                    case 3: changePassword(); break;
                    case 4: privacySettings(); break;
                    case 5: notificationPreferences(); break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("\nInvalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a number.");
            } catch (Exception e) {
                System.out.println("\nError: " + e.getMessage());
            }
        }
    }
    
    private void displayMenu() {
    	System.out.println("\n----------------------------------------");
    	System.out.println("               SETTINGS");
    	System.out.println("----------------------------------------");
    	System.out.println("[1] View My Profile");
    	System.out.println("[2] Edit Profile");
    	System.out.println("[3] Change Password");
    	System.out.println("[4] Privacy Settings");
    	System.out.println("[5] Notification Preferences");
    	System.out.println("[0] Back to Main Menu");
    	System.out.println("----------------------------------------");
    	System.out.print("Enter selection: ");

    }
    
    private void viewProfile() {
    	System.out.println("\n----------------------------------------");
    	System.out.println("              USER PROFILE");
    	System.out.println("----------------------------------------");

        try {
            Profile profile = userService.viewProfile(loggedInUser.getUserId());
            
            if (profile == null) {
                System.out.println("\nProfile not found.");
                return;
            }
            
            System.out.println("\n--- Account Information ---");
            System.out.println("User ID: " + loggedInUser.getUserId());
            System.out.println("Username: " + loggedInUser.getUsername());
            System.out.println("Email: " + loggedInUser.getEmail());
            System.out.println("Account Type: " + loggedInUser.getUserType());
            System.out.println("Privacy: " + (loggedInUser.isPrivate() ? "Private" : "Public"));
            System.out.println("Created: " + loggedInUser.getCreatedAt());
            
            System.out.println("\n--- Profile Information ---");
            System.out.println("Name: " + profile.getName());
            
            if (profile.getBio() != null && !profile.getBio().isEmpty()) {
                System.out.println("Bio: " + profile.getBio());
            }
            
            if (profile.getLocation() != null && !profile.getLocation().isEmpty()) {
                System.out.println("Location: " + profile.getLocation());
            }
            
            if (profile.getWebsite() != null && !profile.getWebsite().isEmpty()) {
                System.out.println("Website: " + profile.getWebsite());
            }
            
            if (!loggedInUser.getUserType().equals("PERSONAL")) {
                System.out.println("\n--- Business/Creator Information ---");
                
                if (profile.getCategory() != null) {
                    System.out.println("Category: " + profile.getCategory());
                }
                
                if (profile.getContactInfo() != null) {
                    System.out.println("Contact Info: " + profile.getContactInfo());
                }
                
                if (loggedInUser.getUserType().equals("BUSINESS")) {
                    if (profile.getBusinessAddress() != null) {
                        System.out.println("Business Address: " + profile.getBusinessAddress());
                    }
                    
                    if (profile.getBusinessHours() != null) {
                        System.out.println("Business Hours: " + profile.getBusinessHours());
                    }
                }
            }
            
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.out.println("\nError loading profile: " + e.getMessage());
        }
    }
    
    private void editProfile() {
        System.out.println("\n--- Edit Profile ---");
        
        try {
            Profile profile = userService.viewProfile(loggedInUser.getUserId());
            
            if (profile == null) {
                System.out.println("\nProfile not found.");
                return;
            }
            
            System.out.println("Leave fields blank to keep current values.");
            
            System.out.print("Name [" + profile.getName() + "]: ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                profile.setName(name);
            }
            
            System.out.print("Bio [" + (profile.getBio() != null ? profile.getBio() : "None") + "]: ");
            String bio = scanner.nextLine().trim();
            if (!bio.isEmpty()) {
                profile.setBio(bio);
            }
            
            System.out.print("Location [" + (profile.getLocation() != null ? profile.getLocation() : "None") + "]: ");
            String location = scanner.nextLine().trim();
            if (!location.isEmpty()) {
                profile.setLocation(location);
            }
            
            System.out.print("Website [" + (profile.getWebsite() != null ? profile.getWebsite() : "None") + "]: ");
            String website = scanner.nextLine().trim();
            if (!website.isEmpty()) {
                profile.setWebsite(website);
            }
            
            if (!loggedInUser.getUserType().equals("PERSONAL")) {
                System.out.print("Category [" + (profile.getCategory() != null ? profile.getCategory() : "None") + "]: ");
                String category = scanner.nextLine().trim();
                if (!category.isEmpty()) {
                    profile.setCategory(category);
                }
                
                System.out.print("Contact Info [" + (profile.getContactInfo() != null ? profile.getContactInfo() : "None") + "]: ");
                String contactInfo = scanner.nextLine().trim();
                if (!contactInfo.isEmpty()) {
                    profile.setContactInfo(contactInfo);
                }
                
                if (loggedInUser.getUserType().equals("BUSINESS")) {
                    System.out.print("Business Address [" + (profile.getBusinessAddress() != null ? profile.getBusinessAddress() : "None") + "]: ");
                    String businessAddress = scanner.nextLine().trim();
                    if (!businessAddress.isEmpty()) {
                        profile.setBusinessAddress(businessAddress);
                    }
                    
                    System.out.print("Business Hours [" + (profile.getBusinessHours() != null ? profile.getBusinessHours() : "None") + "]: ");
                    String businessHours = scanner.nextLine().trim();
                    if (!businessHours.isEmpty()) {
                        profile.setBusinessHours(businessHours);
                    }
                }
            }
            
            boolean success = userService.updateProfile(profile);
            
            if (success) {
                System.out.println("\nProfile updated successfully!");
            } else {
                System.out.println("\nFailed to update profile.");
            }
            
        } catch (Exception e) {
            System.out.println("\nError updating profile: " + e.getMessage());
        }
    }
    
    private void changePassword() {
        System.out.println("\n--- Change Password ---");
        
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        
        System.out.print("Enter new password (minimum 8 characters): ");
        String newPassword = scanner.nextLine();
        
        if (newPassword.length() < 8) {
            System.out.println("\nPassword must be at least 8 characters long.");
            return;
        }
        
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("\nPasswords do not match.");
            return;
        }
        
        try {
            boolean success = userService.changePassword(
                loggedInUser.getUserId(), 
                currentPassword, 
                newPassword
            );
            
            if (success) {
                System.out.println("\nPassword changed successfully!");
            } else {
                System.out.println("\nFailed to change password. Current password may be incorrect.");
            }
            
        } catch (Exception e) {
            System.out.println("\nError changing password: " + e.getMessage());
        }
    }
    
    private void privacySettings() {
    	System.out.println("\n========== PRIVACY SETTINGS ==========");
    	System.out.println("Status : " + (loggedInUser.isPrivate() ? "Private Account" : "Public Account"));

    	System.out.println("\n[ Private Account ]");
    	System.out.println("  - Posts visible only to connections");
    	System.out.println("  - Connection requests require approval");

    	System.out.println("\n[ Public Account ]");
    	System.out.println("  - Posts visible to everyone");
    	System.out.println("  - Anyone can follow you");
    	System.out.println("======================================");

        
        System.out.print("\nMake account private? (yes/no): ");
        String input = scanner.nextLine().trim().toLowerCase();
        
        boolean makePrivate = input.equals("yes") || input.equals("y");
        
        try {
            boolean success = userService.updatePrivacy(loggedInUser.getUserId(), makePrivate);
            
            if (success) {
                loggedInUser.setPrivate(makePrivate);
                System.out.println("\nPrivacy settings updated successfully!");
                System.out.println("Your account is now: " + (makePrivate ? "Private" : "Public"));
            } else {
                System.out.println("\nFailed to update privacy settings.");
            }
            
        } catch (Exception e) {
            System.out.println("\nError updating privacy settings: " + e.getMessage());
        }
    }
    
    private void notificationPreferences() {
        System.out.println("\n========================================");
        System.out.println("    NOTIFICATION PREFERENCES");
        System.out.println("========================================");
        
        try {
            NotificationPreference preferences = notificationService.getPreferences(loggedInUser.getUserId());
            
            if (preferences == null) {
                preferences = new NotificationPreference();
                preferences.setUserId(loggedInUser.getUserId());
                preferences.setConnectionRequests(true);
                preferences.setNewFollowers(true);
                preferences.setLikes(true);
                preferences.setComments(true);
                preferences.setShares(true);
                preferences.setNewPosts(true);
            }
            
            System.out.println("\nCurrent Preferences:");
            System.out.println("1. Connection Requests: " + (preferences.isConnectionRequests() ? "Enabled" : "Disabled"));
            System.out.println("2. New Followers: " + (preferences.isNewFollowers() ? "Enabled" : "Disabled"));
            System.out.println("3. Likes: " + (preferences.isLikes() ? "Enabled" : "Disabled"));
            System.out.println("4. Comments: " + (preferences.isComments() ? "Enabled" : "Disabled"));
            System.out.println("5. Shares: " + (preferences.isShares() ? "Enabled" : "Disabled"));
            System.out.println("6. New Posts from Connections: " + (preferences.isNewPosts() ? "Enabled" : "Disabled"));
            System.out.println("0. Save and Exit");
            
            System.out.print("\nEnter number to toggle (0 to save): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            switch (choice) {
                case 1:
                    preferences.setConnectionRequests(!preferences.isConnectionRequests());
                    notificationPreferences();
                    return;
                case 2:
                    preferences.setNewFollowers(!preferences.isNewFollowers());
                    notificationPreferences();
                    return;
                case 3:
                    preferences.setLikes(!preferences.isLikes());
                    notificationPreferences();
                    return;
                case 4:
                    preferences.setComments(!preferences.isComments());
                    notificationPreferences();
                    return;
                case 5:
                    preferences.setShares(!preferences.isShares());
                    notificationPreferences();
                    return;
                case 6:
                    preferences.setNewPosts(!preferences.isNewPosts());
                    notificationPreferences();
                    return;
                case 0:
                    boolean success = notificationService.updatePreferences(preferences);
                    if (success) {
                        System.out.println("\nNotification preferences saved successfully!");
                    } else {
                        System.out.println("\nFailed to save notification preferences.");
                    }
                    break;
                default:
                    System.out.println("\nInvalid choice.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid input. Please enter a number.");
        } catch (Exception e) {
            System.out.println("\nError managing notification preferences: " + e.getMessage());
        }
    }
}