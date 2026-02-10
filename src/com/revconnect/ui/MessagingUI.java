// FILE LOCATION: src/main/java/com/revconnect/ui/MessagingUI.java

package com.revconnect.ui;

import com.revconnect.model.User;
import com.revconnect.model.Message;
import com.revconnect.service.MessageService;
import com.revconnect.service.NetworkService;
import com.revconnect.serviceimpl.MessageServiceImpl;
import com.revconnect.serviceimpl.NetworkServiceImpl;

import java.util.List;
import java.util.Scanner;

public class MessagingUI {
    
    private User loggedInUser;
    private MessageService messageService;
    private NetworkService networkService;
    private Scanner scanner;
    
    public MessagingUI(User user) {
        this.loggedInUser = user;
        this.messageService = new MessageServiceImpl();
        this.networkService = new NetworkServiceImpl();
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        boolean running = true;
        
        while (running) {
            displayHeader();
            displayMenu();
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1: sendMessage(); break;
                    case 2: viewConversationsList(); break;
                    case 3: viewConversation(); break;
                    case 4: markConversationAsRead(); break;
                    case 5: deleteConversation(); break;
                    case 6: blockUser(); break;
                    case 7: unblockUser(); break;
                    case 8: viewBlockedUsers(); break;
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
    
    private void displayHeader() {
        try {
            int unreadCount = messageService.getUnreadMessageCount(loggedInUser.getUserId());
            
            System.out.println("\n========================================");
            System.out.println("           MESSAGES");
            if (unreadCount > 0) {
                System.out.println("  You have " + unreadCount + " unread message(s)");
            }
            System.out.println("========================================");
        } catch (Exception e) {
            System.out.println("\n========================================");
            System.out.println("           MESSAGES");
            System.out.println("========================================");
        }
    }
    
    private void displayMenu() {
    	System.out.println("\n----------------------------------------");
    	System.out.println("            MESSAGE CENTER");
    	System.out.println("----------------------------------------");
    	System.out.println("[1] Send Message");
    	System.out.println("[2] View Conversations");
    	System.out.println("[3] View Conversation with User");
    	System.out.println("[4] Mark Conversation as Read");
    	System.out.println("[5] Delete Conversation");
    	System.out.println("[6] Block User");
    	System.out.println("[7] Unblock User");
    	System.out.println("[8] View Blocked Users");
    	System.out.println("[0] Back to Main Menu");
    	System.out.println("----------------------------------------");
    	System.out.print("Enter selection: ");

    }
    
    private void sendMessage() {
        System.out.println("\n--- Send Message ---");
        
        List<User> connections = networkService.getConnections(loggedInUser.getUserId());
        
        if (connections.isEmpty()) {
            System.out.println("\nYou have no connections to message.");
            System.out.println("Build your network first by sending connection requests!");
            return;
        }
        
        System.out.println("\nYour Connections:");
        for (int i = 0; i < connections.size(); i++) {
            User user = connections.get(i);
            System.out.println((i + 1) + ". ID: " + user.getUserId() + " | " + user.getUsername());
        }
        
        System.out.print("\nEnter User ID to message: ");
        int receiverId = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Enter your message: ");
        String messageText = scanner.nextLine().trim();
        
        if (messageText.isEmpty()) {
            System.out.println("\nMessage cannot be empty.");
            return;
        }
        
        Message message = new Message();
        message.setSenderId(loggedInUser.getUserId());
        message.setReceiverId(receiverId);
        message.setMessageText(messageText);
        
        try {
            boolean success = messageService.sendMessage(message);
            
            if (success) {
                System.out.println("\nMessage sent successfully!");
            } else {
                System.out.println("\nFailed to send message. User may have blocked you or vice versa.");
            }
        } catch (Exception e) {
            System.out.println("\nError sending message: " + e.getMessage());
        }
    }
    
    private void viewConversationsList() {
        System.out.println("\n========================================");
        System.out.println("      RECENT CONVERSATIONS");
        System.out.println("========================================");
        
        List<User> connections = networkService.getConnections(loggedInUser.getUserId());
        
        if (connections.isEmpty()) {
            System.out.println("\nNo conversations yet. Connect with users to start messaging.");
            return;
        }
        
        System.out.println("\nConnections you can message:");
        System.out.println("----------------------------------------");
        
        for (User user : connections) {
            List<Message> conversation = messageService.getConversation(
                loggedInUser.getUserId(), 
                user.getUserId()
            );
            
            int unreadCount = 0;
            for (Message msg : conversation) {
                if (msg.getReceiverId() == loggedInUser.getUserId() && !msg.isRead()) {
                    unreadCount++;
                }
            }
            
            System.out.println("User ID: " + user.getUserId() + " | " + user.getUsername());
            System.out.println("  Messages: " + conversation.size());
            if (unreadCount > 0) {
                System.out.println("  Unread: " + unreadCount);
            }
            System.out.println("----------------------------------------");
        }
    }
    
    private void viewConversation() {
        System.out.print("\nEnter User ID to view conversation: ");
        int otherUserId = Integer.parseInt(scanner.nextLine().trim());
        
        try {
            List<Message> conversation = messageService.getConversation(
                loggedInUser.getUserId(), 
                otherUserId
            );
            
            if (conversation.isEmpty()) {
                System.out.println("\nNo messages in this conversation yet.");
                return;
            }
            
            System.out.println("\n========================================");
            System.out.println("      CONVERSATION HISTORY");
            System.out.println("========================================");
            System.out.println("Total messages: " + conversation.size());
            System.out.println("----------------------------------------");
            
            for (Message message : conversation) {
                String direction = message.getSenderId() == loggedInUser.getUserId() ? "You" : "Them";
                String status = message.isRead() ? "[READ]" : "[UNREAD]";
                
                System.out.println(direction + " " + status);
                System.out.println("  " + message.getMessageText());
                System.out.println("  " + message.getCreatedAt());
                System.out.println("----------------------------------------");
            }
            
        } catch (Exception e) {
            System.out.println("\nError loading conversation: " + e.getMessage());
        }
    }
    
    private void markConversationAsRead() {
        System.out.print("\nEnter User ID to mark conversation as read: ");
        int otherUserId = Integer.parseInt(scanner.nextLine().trim());
        
        try {
            boolean success = messageService.markConversationAsRead(
                loggedInUser.getUserId(), 
                otherUserId
            );
            
            if (success) {
                System.out.println("\nConversation marked as read.");
            } else {
                System.out.println("\nFailed to mark conversation as read.");
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    private void deleteConversation() {
        System.out.print("\nEnter User ID to delete conversation with: ");
        int otherUserId = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Are you sure? This cannot be undone. (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (!confirmation.equals("yes") && !confirmation.equals("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }
        
        try {
            boolean success = messageService.deleteConversation(
                loggedInUser.getUserId(), 
                otherUserId
            );
            
            if (success) {
                System.out.println("\nConversation deleted successfully.");
            } else {
                System.out.println("\nFailed to delete conversation.");
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    private void blockUser() {
        System.out.println("\n--- Block User ---");
        
        System.out.print("Enter User ID to block: ");
        int blockedId = Integer.parseInt(scanner.nextLine().trim());
        
        if (blockedId == loggedInUser.getUserId()) {
            System.out.println("\nYou cannot block yourself.");
            return;
        }
        
        System.out.print("Are you sure you want to block this user? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (!confirmation.equals("yes") && !confirmation.equals("y")) {
            System.out.println("Blocking cancelled.");
            return;
        }
        
        try {
            boolean success = messageService.blockUser(loggedInUser.getUserId(), blockedId);
            
            if (success) {
                System.out.println("\nUser blocked successfully.");
                System.out.println("All existing conversations have been deleted.");
                System.out.println("This user can no longer message you.");
            } else {
                System.out.println("\nFailed to block user. User may already be blocked.");
            }
        } catch (Exception e) {
            System.out.println("\nError blocking user: " + e.getMessage());
        }
    }
    
    private void unblockUser() {
        System.out.print("\nEnter User ID to unblock: ");
        int blockedId = Integer.parseInt(scanner.nextLine().trim());
        
        try {
            boolean success = messageService.unblockUser(loggedInUser.getUserId(), blockedId);
            
            if (success) {
                System.out.println("\nUser unblocked successfully.");
            } else {
                System.out.println("\nFailed to unblock user. User may not be blocked.");
            }
        } catch (Exception e) {
            System.out.println("\nError unblocking user: " + e.getMessage());
        }
    }
    
    private void viewBlockedUsers() {
        System.out.println("\n========================================");
        System.out.println("         BLOCKED USERS");
        System.out.println("========================================");
        
        try {
            List<User> blockedUsers = messageService.getBlockedUsers(loggedInUser.getUserId());
            
            if (blockedUsers.isEmpty()) {
                System.out.println("\nYou haven't blocked any users.");
                return;
            }
            
            System.out.println("\nTotal blocked users: " + blockedUsers.size());
            System.out.println("----------------------------------------");
            
            for (User user : blockedUsers) {
                System.out.println("User ID: " + user.getUserId());
                System.out.println("Username: " + user.getUsername());
                System.out.println("Email: " + user.getEmail());
                System.out.println("----------------------------------------");
            }
        } catch (Exception e) {
            System.out.println("\nError loading blocked users: " + e.getMessage());
        }
    }
}