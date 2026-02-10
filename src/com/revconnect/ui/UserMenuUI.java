// FILE LOCATION: src/main/java/com/revconnect/ui/UserMenuUI.java

package com.revconnect.ui;

import com.revconnect.model.User;
import com.revconnect.model.Notification;
import com.revconnect.service.NotificationService;
import com.revconnect.serviceimpl.NotificationServiceImpl;

import java.util.List;
import java.util.Scanner;

public class UserMenuUI {
    
    private User loggedInUser;
    private NotificationService notificationService;
    private Scanner scanner;
    
    public UserMenuUI(User user) {
        this.loggedInUser = user;
        this.notificationService = new NotificationServiceImpl();
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        boolean running = true;
        
        while (running) {
            displayWelcomeHeader();
            displayMainMenu();
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1:
                        navigateToPostsAndFeed();
                        break;
                    case 2:
                        navigateToNetwork();
                        break;
                    case 3:
                        navigateToMessages();
                        break;
                    case 4:
                        navigateToSettings();
                        break;
                    case 5:
                        viewNotifications();
                        break;
                    case 0:
                        running = false;
                        System.out.println("\nLogging out...");
                        System.out.println("Thank you for using RevConnect!");
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
    
    private void displayWelcomeHeader() {
        try {
            int unreadCount = notificationService.getUnreadCount(loggedInUser.getUserId());
            
            System.out.println("\n========================================");
            System.out.println("  Welcome back, " + loggedInUser.getUsername() + "!");
            System.out.println("  Account Type: " + loggedInUser.getUserType());
            if (unreadCount > 0) {
                System.out.println("  You have " + unreadCount + " unread notification(s)");
            }
            System.out.println("========================================");
        } catch (Exception e) {
            System.out.println("\n========================================");
            System.out.println("  Welcome back, " + loggedInUser.getUsername() + "!");
            System.out.println("========================================");
        }
    }
    
    private void displayMainMenu() {
    	System.out.println("\n----------------------------------------");
    	System.out.println("             DASHBOARD");
    	System.out.println("----------------------------------------");
    	System.out.println("[1] Posts & Feed");
    	System.out.println("[2] Network & Connections");
    	System.out.println("[3] Messages");
    	System.out.println("[4] Settings");
    	System.out.println("[5] View Notifications");
    	System.out.println("[0] Logout");
    	System.out.println("----------------------------------------");
    	System.out.print("Enter selection: ");

    }
    
    private void navigateToPostsAndFeed() {
        PostUI postUI = new PostUI(loggedInUser);
        postUI.start();
    }
    
    private void navigateToNetwork() {
        NetworkUI networkUI = new NetworkUI(loggedInUser);
        networkUI.start();
    }
    
    private void navigateToMessages() {
        MessagingUI messagingUI = new MessagingUI(loggedInUser);
        messagingUI.start();
    }
    
    private void navigateToSettings() {
        SettingsUI settingsUI = new SettingsUI(loggedInUser);
        settingsUI.start();
    }
    
    private void viewNotifications() {
        System.out.println("\n========================================");
        System.out.println("         YOUR NOTIFICATIONS");
        System.out.println("========================================");
        
        try {
            List<Notification> notifications = notificationService.getNotifications(loggedInUser.getUserId());
            
            if (notifications.isEmpty()) {
                System.out.println("\nNo notifications to display.");
                return;
            }
            
            System.out.println("\nTotal notifications: " + notifications.size());
            System.out.println("----------------------------------------");
            
            for (int i = 0; i < notifications.size(); i++) {
                Notification notification = notifications.get(i);
                
                String status = notification.isRead() ? "[READ]" : "[UNREAD]";
                
                System.out.println((i + 1) + ". " + status + " " + notification.getType());
                System.out.println("   " + notification.getMessage());
                System.out.println("   Time: " + notification.getCreatedAt());
                System.out.println("----------------------------------------");
            }
            
            System.out.print("\nMark all as read? (yes/no): ");
            String markRead = scanner.nextLine().trim().toLowerCase();
            
            if (markRead.equals("yes") || markRead.equals("y")) {
                boolean success = notificationService.markAllAsRead(loggedInUser.getUserId());
                if (success) {
                    System.out.println("All notifications marked as read.");
                } else {
                    System.out.println("Failed to mark notifications as read.");
                }
            }
            
        } catch (Exception e) {
            System.out.println("\nError loading notifications: " + e.getMessage());
        }
    }
}