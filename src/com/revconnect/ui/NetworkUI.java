// FILE LOCATION: src/main/java/com/revconnect/ui/NetworkUI.java

package com.revconnect.ui;

import com.revconnect.model.User;
import com.revconnect.model.ConnectionRequest;
import com.revconnect.service.NetworkService;
import com.revconnect.service.UserService;
import com.revconnect.serviceimpl.NetworkServiceImpl;
import com.revconnect.serviceimpl.UserServiceImpl;

import java.util.List;
import java.util.Scanner;

public class NetworkUI {
    
    private User loggedInUser;
    private NetworkService networkService;
    private UserService userService;
    private Scanner scanner;
    
    public NetworkUI(User user) {
        this.loggedInUser = user;
        this.networkService = new NetworkServiceImpl();
        this.userService = new UserServiceImpl();
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        boolean running = true;
        
        while (running) {
            displayMenu();
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1: sendConnectionRequest(); break;
                    case 2: viewPendingRequests(); break;
                    case 3: acceptConnectionRequest(); break;
                    case 4: rejectConnectionRequest(); break;
                    case 5: viewConnections(); break;
                    case 6: removeConnection(); break;
                    case 7: followUser(); break;
                    case 8: unfollowUser(); break;
                    case 9: viewFollowers(); break;
                    case 10: viewFollowing(); break;
                    case 11: searchUsers(); break;
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
    	System.out.println("\n=========== NETWORK & CONNECTIONS ===========");

    	System.out.println("\n---------- CONNECTION REQUESTS ------------");
    	System.out.println("1. Send Request");
    	System.out.println("2. View Pending Requests");
    	System.out.println("3. Accept Request");
    	System.out.println("4. Reject Request");

    	System.out.println("\n----------------- MY NETWORK ----------------");
    	System.out.println("5. View My Connections");
    	System.out.println("6. Remove Connection");

    	System.out.println("\n------------- FOLLOW SYSTEM ------------------");
    	System.out.println("7. Follow User");
    	System.out.println("8. Unfollow User");
    	System.out.println("9. View My Followers");
    	System.out.println("10. View Who I'm Following");

    	System.out.println("\n-------------- DISCOVER ---------------");
    	System.out.println("11. Search Users");

    	System.out.println("\n0. Back to Main Menu");
    	System.out.println("=============================================");
    	System.out.print("Enter your choice: ");


    }
    
    private void sendConnectionRequest() {
        System.out.println("\n--- Send Connection Request ---");
        
        System.out.print("Search users by name or username: ");
        String keyword = scanner.nextLine().trim();
        
        if (keyword.isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return;
        }
        
        List<User> users = userService.searchUsers(keyword);
        
        if (users.isEmpty()) {
            System.out.println("\nNo users found matching: " + keyword);
            return;
        }
        
        System.out.println("\nSearch Results:");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getUserId() != loggedInUser.getUserId()) {
                System.out.println((i + 1) + ". ID: " + user.getUserId() + " | " + user.getUsername() + 
                                   " (" + user.getUserType() + ")");
            }
        }
        
        System.out.print("\nEnter User ID to send connection request: ");
        int receiverId = Integer.parseInt(scanner.nextLine().trim());
        
        if (receiverId == loggedInUser.getUserId()) {
            System.out.println("You cannot send a connection request to yourself.");
            return;
        }
        
        boolean success = networkService.sendConnectionRequest(loggedInUser.getUserId(), receiverId);
        
        if (success) {
            System.out.println("\nConnection request sent successfully!");
        } else {
            System.out.println("\nFailed to send connection request. They may already be a connection or request is pending.");
        }
    }
    
    private void viewPendingRequests() {
    	System.out.println("\n----------------------------------------");
    	System.out.println("        CONNECTION REQUESTS (PENDING)");
    	System.out.println("----------------------------------------");

        
        List<ConnectionRequest> requests = networkService.getPendingRequests(loggedInUser.getUserId());
        
        if (requests.isEmpty()) {
            System.out.println("\nNo pending connection requests.");
            return;
        }
        
        System.out.println("\nTotal pending requests: " + requests.size());
        System.out.println("----------------------------------------");
        
        for (ConnectionRequest request : requests) {
            System.out.println("Request ID: " + request.getRequestId());
            System.out.println("From User ID: " + request.getSenderId());
            System.out.println("Status: " + request.getStatus());
            System.out.println("Sent at: " + request.getCreatedAt());
            System.out.println("----------------------------------------");
        }
    }
    
    private void acceptConnectionRequest() {
        System.out.print("\nEnter Request ID to accept: ");
        int requestId = Integer.parseInt(scanner.nextLine().trim());
        
        boolean success = networkService.acceptConnectionRequest(requestId);
        
        if (success) {
            System.out.println("\nConnection request accepted!");
        } else {
            System.out.println("\nFailed to accept connection request.");
        }
    }
    
    private void rejectConnectionRequest() {
        System.out.print("\nEnter Request ID to reject: ");
        int requestId = Integer.parseInt(scanner.nextLine().trim());
        
        boolean success = networkService.rejectConnectionRequest(requestId);
        
        if (success) {
            System.out.println("\nConnection request rejected.");
        } else {
            System.out.println("\nFailed to reject connection request.");
        }
    }
    
    private void viewConnections() {
    	System.out.println("\n+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
    	System.out.println("             MY CONNECTIONS");
    	System.out.println("+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");

        
        List<User> connections = networkService.getConnections(loggedInUser.getUserId());
        
        if (connections.isEmpty()) {
            System.out.println("\nYou have no connections yet. Send connection requests to start building your network!");
            return;
        }
        
        System.out.println("\nTotal connections: " + connections.size());
        System.out.println("----------------------------------------");
        
        for (User user : connections) {
            System.out.println("User ID: " + user.getUserId());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Account Type: " + user.getUserType());
            System.out.println("----------------------------------------");
        }
    }
    
    private void removeConnection() {
        System.out.print("\nEnter User ID to remove from connections: ");
        int connectionId = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Are you sure you want to remove this connection? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (!confirmation.equals("yes") && !confirmation.equals("y")) {
            System.out.println("Connection removal cancelled.");
            return;
        }
        
        boolean success = networkService.removeConnection(loggedInUser.getUserId(), connectionId);
        
        if (success) {
            System.out.println("\nConnection removed successfully.");
        } else {
            System.out.println("\nFailed to remove connection.");
        }
    }
    
    private void followUser() {
        System.out.println("\n--- Follow User ---");
        
        System.out.print("Search users by name or username: ");
        String keyword = scanner.nextLine().trim();
        
        if (keyword.isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return;
        }
        
        List<User> users = userService.searchUsers(keyword);
        
        if (users.isEmpty()) {
            System.out.println("\nNo users found matching: " + keyword);
            return;
        }
        
        System.out.println("\nSearch Results:");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getUserId() != loggedInUser.getUserId()) {
                System.out.println((i + 1) + ". ID: " + user.getUserId() + " | " + user.getUsername() + 
                                   " (" + user.getUserType() + ")");
            }
        }
        
        System.out.print("\nEnter User ID to follow: ");
        int followingId = Integer.parseInt(scanner.nextLine().trim());
        
        if (followingId == loggedInUser.getUserId()) {
            System.out.println("You cannot follow yourself.");
            return;
        }
        
        boolean success = networkService.followUser(loggedInUser.getUserId(), followingId);
        
        if (success) {
            System.out.println("\nUser followed successfully!");
        } else {
            System.out.println("\nFailed to follow user. You may already be following them.");
        }
    }
    
    private void unfollowUser() {
        System.out.print("\nEnter User ID to unfollow: ");
        int followingId = Integer.parseInt(scanner.nextLine().trim());
        
        boolean success = networkService.unfollowUser(loggedInUser.getUserId(), followingId);
        
        if (success) {
            System.out.println("\nUser unfollowed successfully.");
        } else {
            System.out.println("\nFailed to unfollow user.");
        }
    }
    
    private void viewFollowers() {
    	System.out.println("\n<> <> <> <> <> <> <> <> <> <> <> <> <>");
    	System.out.println("             MY FOLLOWERS");
    	System.out.println("<> <> <> <> <> <> <> <> <> <> <> <> <>");

        
        List<User> followers = networkService.getFollowers(loggedInUser.getUserId());
        
        if (followers.isEmpty()) {
            System.out.println("\nYou have no followers yet.");
            return;
        }
        
        System.out.println("\nTotal followers: " + followers.size());
        System.out.println("----------------------------------------");
        
        for (User user : followers) {
            System.out.println("User ID: " + user.getUserId());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Account Type: " + user.getUserType());
            System.out.println("----------------------------------------");
        }
    }
    
    private void viewFollowing() {
    	System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    	System.out.println("        USERS I'M FOLLOWING");
    	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        
        List<User> following = networkService.getFollowing(loggedInUser.getUserId());
        
        if (following.isEmpty()) {
            System.out.println("\nYou are not following anyone yet.");
            return;
        }
        
        System.out.println("\nTotal following: " + following.size());
        System.out.println("----------------------------------------");
        
        for (User user : following) {
            System.out.println("User ID: " + user.getUserId());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Account Type: " + user.getUserType());
            System.out.println("----------------------------------------");
        }
    }
    
    private void searchUsers() {
        System.out.print("\nEnter search keyword (name or username): ");
        String keyword = scanner.nextLine().trim();
        
        if (keyword.isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return;
        }
        
        List<User> users = userService.searchUsers(keyword);
        
        if (users.isEmpty()) {
            System.out.println("\nNo users found matching: " + keyword);
            return;
        }
        
        System.out.println("\n<> <> <> <> <> <> <> <> <> <> <> <> <>");
        System.out.println("           SEARCH RESULTS");
        System.out.println("<> <> <> <> <> <> <> <> <> <> <> <> <>");
        System.out.println("\nFound " + users.size() + " user(s):");
        System.out.println("----------------------------------------");
        
        for (User user : users) {
            System.out.println("User ID: " + user.getUserId());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Account Type: " + user.getUserType());
            System.out.println("Private: " + (user.isPrivate() ? "Yes" : "No"));
            System.out.println("----------------------------------------");
        }
    }
}