package com.revconnect.serviceimplementation;

import com.revconnect.service.PostService;
import com.revconnect.service.NotificationService;
import com.revconnect.model.Post;
import com.revconnect.model.PostAnalytics;
import com.revconnect.repo.CommentRepo;
import com.revconnect.repo.LikeRepo;
import com.revconnect.repo.PostRepo;
import com.revconnect.repo.ShareRepo;
import com.revconnect.model.Comment;
import com.revconnect.model.Notification;
import com.revconnect.configuration.ApplicationConfiguration;
import com.revconnect.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

public class PostServiceImpl implements PostService {

    private final PostRepo postRepo;
    private final CommentRepo commentRepo;
    private final LikeRepo likeRepo;
    private final ShareRepo shareRepo;
    private final NotificationService notificationService;

    public PostServiceImpl() {
        this.postRepo = new PostRepo();
        this.commentRepo = new CommentRepo();
        this.likeRepo = new LikeRepo();
        this.shareRepo = new ShareRepo();
        this.notificationService = new NotificationServiceImpl();
    }

    @Override
    public boolean createPost(Post post) {

        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            throw new ValidationException("Post content cannot be empty");
        }

        if (post.getContent().length() > ApplicationConfiguration.MAX_POST_LENGTH) {
            throw new ValidationException(
                    "Post content exceeds maximum length of "
                            + ApplicationConfiguration.MAX_POST_LENGTH
                            + " characters");
        }

        post.setCreatedAt(LocalDateTime.now());
        return postRepo.createPost(post);
    }

    @Override
    public boolean updatePost(Post post) {

        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            throw new ValidationException("Post content cannot be empty");
        }

        if (post.getContent().length() > ApplicationConfiguration.MAX_POST_LENGTH) {
            throw new ValidationException(
                    "Post content exceeds maximum length of "
                            + ApplicationConfiguration.MAX_POST_LENGTH
                            + " characters");
        }

        post.setUpdatedAt(LocalDateTime.now());
        return postRepo.updatePost(post);
    }

    @Override
    public boolean deletePost(int postId, int userId) {

        Post post = postRepo.findById(postId);
        if (post == null) {
            throw new ValidationException("Post not found");
        }

        if (post.getUserId() != userId) {
            throw new ValidationException("You can only delete your own posts");
        }

        return postRepo.deletePost(postId);
    }

    @Override
    public List<Post> getMyPosts(int userId) {
        return postRepo.findByUserId(userId);
    }

    @Override
    public List<Post> getFeed(int userId) {
        return postRepo.getFeed(userId);
    }

    @Override
    public List<Post> getFilteredFeed(int userId,
                                      String postType,
                                      String userType) {
        return postRepo.getFilteredFeed(userId, postType, userType);
    }

    @Override
    public List<Post> getTrendingPosts() {
        return postRepo.getTrendingPosts();
    }

    @Override
    public List<Post> searchByHashtag(String hashtag) {

        if (hashtag == null || hashtag.trim().isEmpty()) {
            throw new ValidationException("Hashtag cannot be empty");
        }

        String cleanHashtag =
                hashtag.startsWith("#")
                        ? hashtag.substring(1)
                        : hashtag;

        return postRepo.searchByHashtag(cleanHashtag);
    }

    @Override
    public boolean likePost(int postId, int userId) {

        Post post = postRepo.findById(postId);
        if (post == null) {
            throw new ValidationException("Post not found");
        }

        boolean liked =
                likeRepo.addLike(postId, userId);

        if (liked && post.getUserId() != userId) {

            Notification notification = new Notification();
            notification.setUserId(post.getUserId());
            notification.setType("LIKE");
            notification.setMessage("Someone liked your post");
            notification.setRead(false);
            notification.setRelatedUserId(userId);
            notification.setRelatedPostId(postId);
            notification.setCreatedAt(LocalDateTime.now());

            notificationService.createNotification(notification);
        }

        return liked;
    }

    @Override
    public boolean unlikePost(int postId, int userId) {

        Post post = postRepo.findById(postId);
        if (post == null) {
            throw new ValidationException("Post not found");
        }

        return likeRepo.removeLike(postId, userId);
    }

    @Override
    public boolean commentOnPost(int postId,
                                 int userId,
                                 String commentText) {

        Post post = postRepo.findById(postId);
        if (post == null) {
            throw new ValidationException("Post not found");
        }

        if (commentText == null || commentText.trim().isEmpty()) {
            throw new ValidationException("Comment cannot be empty");
        }

        if (commentText.length()
                > ApplicationConfiguration.MAX_COMMENT_LENGTH) {

            throw new ValidationException(
                    "Comment exceeds maximum length of "
                            + ApplicationConfiguration.MAX_COMMENT_LENGTH
                            + " characters");
        }

        boolean commented =
                commentRepo.addComment(
                        postId, userId, commentText);

        if (commented && post.getUserId() != userId) {

            Notification notification = new Notification();
            notification.setUserId(post.getUserId());
            notification.setType("COMMENT");
            notification.setMessage("Someone commented on your post");
            notification.setRead(false);
            notification.setRelatedUserId(userId);
            notification.setRelatedPostId(postId);
            notification.setCreatedAt(LocalDateTime.now());

            notificationService.createNotification(notification);
        }

        return commented;
    }

    @Override
    public List<Comment> getComments(int postId) {
        return commentRepo.findByPostId(postId);
    }

    @Override
    public boolean deleteComment(int commentId,
                                 int userId) {
        return commentRepo.deleteComment(commentId, userId);
    }

    @Override
    public boolean sharePost(int postId, int userId) {

        Post post = postRepo.findById(postId);
        if (post == null) {
            throw new ValidationException("Post not found");
        }

        boolean shared =
                shareRepo.sharePost(postId, userId);

        if (shared && post.getUserId() != userId) {

            Notification notification = new Notification();
            notification.setUserId(post.getUserId());
            notification.setType("SHARE");
            notification.setMessage("Someone shared your post");
            notification.setRead(false);
            notification.setRelatedUserId(userId);
            notification.setRelatedPostId(postId);
            notification.setCreatedAt(LocalDateTime.now());

            notificationService.createNotification(notification);
        }

        return shared;
    }

    @Override
    public boolean schedulePost(Post post) {

        if (post.getScheduledTime() == null) {
            throw new ValidationException(
                    "Scheduled time cannot be null");
        }

        if (post.getScheduledTime()
                .isBefore(LocalDateTime.now())) {

            throw new ValidationException(
                    "Scheduled time must be in the future");
        }

        if (post.getContent() == null
                || post.getContent().trim().isEmpty()) {

            throw new ValidationException(
                    "Post content cannot be empty");
        }

        if (post.getContent().length()
                > ApplicationConfiguration.MAX_POST_LENGTH) {

            throw new ValidationException(
                    "Post content exceeds maximum length");
        }

        return postRepo.schedulePost(post);
    }

    @Override
    public boolean pinPost(int postId, int userId) {

        Post post = postRepo.findById(postId);
        if (post == null) {
            throw new ValidationException("Post not found");
        }

        if (post.getUserId() != userId) {
            throw new ValidationException(
                    "You can only pin your own posts");
        }

        return postRepo.pinPost(postId, userId);
    }

    @Override
    public PostAnalytics getAnalytics(int postId) {

        Post post = postRepo.findById(postId);
        if (post == null) {
            throw new ValidationException("Post not found");
        }

        return postRepo.getAnalytics(postId);
    }
}
