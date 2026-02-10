// FILE LOCATION: src/main/java/com/revconnect/ui/PostUI.java

package com.revconnect.ui;

import com.revconnect.model.User;
import com.revconnect.model.Post;
import com.revconnect.model.Comment;
import com.revconnect.model.PostAnalytics;
import com.revconnect.service.PostService;
import com.revconnect.serviceimplementation.PostServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class PostUI {
    
    private User loggedInUser;
    private PostService postService;
    private Scanner scanner;
    
    public PostUI(User user) {
        this.loggedInUser = user;
        this.postService = new PostServiceImpl();
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        boolean running = true;
        
        while (running) {
            displayMenu();
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1: createPost(); break;
                    case 2: viewMyPosts(); break;
                    case 3: viewFeed(); break;
                    case 4: filterFeed(); break;
                    case 5: viewTrendingPosts(); break;
                    case 6: searchByHashtag(); break;
                    case 7: likePost(); break;
                    case 8: unlikePost(); break;
                    case 9: commentOnPost(); break;
                    case 10: viewComments(); break;
                    case 11: deleteComment(); break;
                    case 12: sharePost(); break;
                    case 13:
                        if (!loggedInUser.getUserType().equals("PERSONAL")) {
                            schedulePost();
                        } else {
                            System.out.println("\nInvalid choice.");
                        }
                        break;
                    case 14:
                        if (!loggedInUser.getUserType().equals("PERSONAL")) {
                            pinPost();
                        } else {
                            System.out.println("\nInvalid choice.");
                        }
                        break;
                    case 15:
                        if (!loggedInUser.getUserType().equals("PERSONAL")) {
                            viewPostAnalytics();
                        } else {
                            System.out.println("\nInvalid choice.");
                        }
                        break;
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
    	System.out.println("           POSTS & FEED MODULE");
    	System.out.println("----------------------------------------");
    	System.out.println("[01] Create Post");
    	System.out.println("[02] View My Posts");
    	System.out.println("[03] View Feed");
    	System.out.println("[04] Filter Feed");
    	System.out.println("[05] View Trending Posts");
    	System.out.println("[06] Search by Hashtag");
    	System.out.println("[07] Like a Post");
    	System.out.println("[08] Unlike a Post");
    	System.out.println("[09] Comment on Post");
    	System.out.println("[10] View Comments");
    	System.out.println("[11] Delete My Comment");
    	System.out.println("[12] Share Post");
    	System.out.println("----------------------------------------");
    	System.out.print("Enter selection: ");


        
        if (!loggedInUser.getUserType().equals("PERSONAL")) {
            System.out.println("--- Business/Creator Features ---");
            System.out.println("13. Schedule Post");
            System.out.println("14. Pin Post");
            System.out.println("15. View Post Analytics");
        }
        
        System.out.println("0. Back to Main Menu");
        System.out.println("========================================");
        System.out.print("Enter your choice: ");
    }
    
    private void createPost() {
        System.out.println("\n--- Create New Post ---");
        
        System.out.print("Enter post content: ");
        String content = scanner.nextLine().trim();
        
        if (content.isEmpty()) {
            System.out.println("Post content cannot be empty.");
            return;
        }
        
        System.out.print("Enter hashtags (space-separated, optional): ");
        String hashtags = scanner.nextLine().trim();
        
        boolean isPromotional = false;
        if (!loggedInUser.getUserType().equals("PERSONAL")) {
            System.out.print("Is this a promotional post? (yes/no): ");
            String promoInput = scanner.nextLine().trim().toLowerCase();
            isPromotional = promoInput.equals("yes") || promoInput.equals("y");
        }
        
        Post post = new Post();
        post.setUserId(loggedInUser.getUserId());
        post.setContent(content);
        post.setHashtags(hashtags.isEmpty() ? null : hashtags);
        post.setPromotional(isPromotional);
        
        boolean success = postService.createPost(post);
        
        if (success) {
            System.out.println("\nPost created successfully!");
        } else {
            System.out.println("\nFailed to create post.");
        }
    }
    
    private void viewMyPosts() {
        System.out.println("\n=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
        System.out.println("           MY POSTS");
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
        
        List<Post> posts = postService.getMyPosts(loggedInUser.getUserId());
        
        if (posts.isEmpty()) {
            System.out.println("\nYou haven't created any posts yet.");
            return;
        }
        
        displayPosts(posts);
    }
    
    private void viewFeed() {
        System.out.println("\n=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
        System.out.println("          YOUR FEED");
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
        
        List<Post> feed = postService.getFeed(loggedInUser.getUserId());
        
        if (feed.isEmpty()) {
            System.out.println("\nNo posts to display. Connect with users to see their posts.");
            return;
        }
        
        displayPosts(feed);
    }
    
    private void filterFeed() {
        System.out.println("\n--+-+-+-+- Filter Feed -+-+-+-+-");
        
        System.out.println("Filter by Post Type:");
        System.out.println("1. All Posts");
        System.out.println("2. Promotional Only");
        System.out.println("3. Regular Only");
        System.out.print("Enter choice: ");
        
        int typeChoice = Integer.parseInt(scanner.nextLine().trim());
        String postType = null;
        
        switch (typeChoice) {
            case 2: postType = "PROMOTIONAL"; break;
            case 3: postType = "REGULAR"; break;
            default: postType = null;
        }
        
        System.out.println("\nFilter by User Type:");
        System.out.println("1. All Users");
        System.out.println("2. Personal Accounts");
        System.out.println("3. Business Accounts");
        System.out.println("4. Creator Accounts");
        System.out.print("Enter choice: ");
        
        int userTypeChoice = Integer.parseInt(scanner.nextLine().trim());
        String userType = null;
        
        switch (userTypeChoice) {
            case 2: userType = "PERSONAL"; break;
            case 3: userType = "BUSINESS"; break;
            case 4: userType = "CREATOR"; break;
            default: userType = null;
        }
        
        List<Post> filteredFeed = postService.getFilteredFeed(loggedInUser.getUserId(), postType, userType);
        
        if (filteredFeed.isEmpty()) {
            System.out.println("\nNo posts match your filters.");
            return;
        }
        
        displayPosts(filteredFeed);
    }
    
    private void viewTrendingPosts() {
        System.out.println("\n=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
        System.out.println("        TRENDING POSTS");
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
        
        List<Post> trending = postService.getTrendingPosts();
        
        if (trending.isEmpty()) {
            System.out.println("\nNo trending posts at the moment.");
            return;
        }
        
        displayPosts(trending);
    }
    
    private void searchByHashtag() {
        System.out.print("\nEnter hashtag to search (without #): ");
        String hashtag = scanner.nextLine().trim();
        
        if (hashtag.isEmpty()) {
            System.out.println("Hashtag cannot be empty.");
            return;
        }
        
        List<Post> posts = postService.searchByHashtag(hashtag);
        
        if (posts.isEmpty()) {
            System.out.println("\nNo posts found with hashtag: #" + hashtag);
            return;
        }
        
        System.out.println("\nPosts with #" + hashtag + ":");
        displayPosts(posts);
    }
    
    private void likePost() {
        System.out.print("\nEnter Post ID to like: ");
        int postId = Integer.parseInt(scanner.nextLine().trim());
        
        boolean success = postService.likePost(postId, loggedInUser.getUserId());
        
        if (success) {
            System.out.println("Post liked successfully!");
        } else {
            System.out.println("Failed to like post. You may have already liked it.");
        }
    }
    
    private void unlikePost() {
        System.out.print("\nEnter Post ID to unlike: ");
        int postId = Integer.parseInt(scanner.nextLine().trim());
        
        boolean success = postService.unlikePost(postId, loggedInUser.getUserId());
        
        if (success) {
            System.out.println("Post unliked successfully!");
        } else {
            System.out.println("Failed to unlike post.");
        }
    }
    
    private void commentOnPost() {
        System.out.print("\nEnter Post ID: ");
        int postId = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Enter your comment: ");
        String commentText = scanner.nextLine().trim();
        
        if (commentText.isEmpty()) {
            System.out.println("Comment cannot be empty.");
            return;
        }
        
        boolean success = postService.commentOnPost(postId, loggedInUser.getUserId(), commentText);
        
        if (success) {
            System.out.println("Comment added successfully!");
        } else {
            System.out.println("Failed to add comment.");
        }
    }
    
    private void viewComments() {
        System.out.print("\nEnter Post ID: ");
        int postId = Integer.parseInt(scanner.nextLine().trim());
        
        List<Comment> comments = postService.getComments(postId);
        
        if (comments.isEmpty()) {
            System.out.println("\nNo comments on this post yet.");
            return;
        }
        
        System.out.println("\n--- Comments ---");
        for (Comment comment : comments) {
            System.out.println("ID: " + comment.getCommentId() + " | User ID: " + comment.getUserId());
            System.out.println("  " + comment.getCommentText());
            System.out.println("  " + comment.getCreatedAt());
            System.out.println("----------------------------------------");
        }
    }
    
    private void deleteComment() {
        System.out.print("\nEnter Comment ID to delete: ");
        int commentId = Integer.parseInt(scanner.nextLine().trim());
        
        boolean success = postService.deleteComment(commentId, loggedInUser.getUserId());
        
        if (success) {
            System.out.println("Comment deleted successfully!");
        } else {
            System.out.println("Failed to delete comment. You can only delete your own comments.");
        }
    }
    
    private void sharePost() {
        System.out.print("\nEnter Post ID to share: ");
        int postId = Integer.parseInt(scanner.nextLine().trim());
        
        boolean success = postService.sharePost(postId, loggedInUser.getUserId());
        
        if (success) {
            System.out.println("Post shared successfully!");
        } else {
            System.out.println("Failed to share post.");
        }
    }
    
    private void schedulePost() {
        System.out.println("\n--- Schedule Post ---");
        
        System.out.print("Enter post content: ");
        String content = scanner.nextLine().trim();
        
        if (content.isEmpty()) {
            System.out.println("Post content cannot be empty.");
            return;
        }
        
        System.out.print("Enter hashtags (optional): ");
        String hashtags = scanner.nextLine().trim();
        
        System.out.print("Is this promotional? (yes/no): ");
        String promoInput = scanner.nextLine().trim().toLowerCase();
        boolean isPromotional = promoInput.equals("yes") || promoInput.equals("y");
        
        System.out.print("Schedule for (format: yyyy-MM-dd HH:mm): ");
        String scheduledTimeStr = scanner.nextLine().trim();
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime scheduledTime = LocalDateTime.parse(scheduledTimeStr, formatter);
            
            Post post = new Post();
            post.setUserId(loggedInUser.getUserId());
            post.setContent(content);
            post.setHashtags(hashtags.isEmpty() ? null : hashtags);
            post.setPromotional(isPromotional);
            
            boolean success = postService.schedulePost(post);
            
            if (success) {
                System.out.println("\nPost scheduled successfully for " + scheduledTime);
            } else {
                System.out.println("\nFailed to schedule post.");
            }
        } catch (Exception e) {
            System.out.println("\nInvalid date format. Please use yyyy-MM-dd HH:mm");
        }
    }
    
    private void pinPost() {
        System.out.print("\nEnter Post ID to pin: ");
        int postId = Integer.parseInt(scanner.nextLine().trim());
        
        boolean success = postService.pinPost(postId, loggedInUser.getUserId());
        
        if (success) {
            System.out.println("Post pinned successfully!");
        } else {
            System.out.println("Failed to pin post.");
        }
    }
    
    private void viewPostAnalytics() {
        System.out.print("\nEnter Post ID: ");
        int postId = Integer.parseInt(scanner.nextLine().trim());
        
        PostAnalytics analytics = postService.getAnalytics(postId);
        
        if (analytics == null) {
            System.out.println("\nNo analytics available for this post.");
            return;
        }
        
        System.out.println("\n=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
        System.out.println("       POST ANALYTICS");
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
        System.out.println("Post ID: " + analytics.getPostId());
        System.out.println("Total Likes: " + analytics.getTotalLikes());
        System.out.println("Total Comments: " + analytics.getTotalComments());
        System.out.println("Total Shares: " + analytics.getTotalShares());
        System.out.println("Unique Viewers: " + analytics.getUniqueViewers());
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
    }
    
    private void displayPosts(List<Post> posts) {
        System.out.println("\nTotal posts: " + posts.size());
        System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
        
        for (Post post : posts) {
            System.out.println("Post ID: " + post.getPostId());
            System.out.println("User ID: " + post.getUserId());
            System.out.println("Content: " + post.getContent());
            if (post.getHashtags() != null && !post.getHashtags().isEmpty()) {
                System.out.println("Hashtags: " + post.getHashtags());
            }
            if (post.isPromotional()) {
                System.out.println("[PROMOTIONAL]");
            }
            if (post.isPinned()) {
                System.out.println("[PINNED - Order: " + post.getPinOrder() + "]");
            }
            System.out.println("Created: " + post.getCreatedAt());
            System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
        }
    }
}