package com.politics.karma;


public class ItemComment {


    // ===== VARIABLES
    protected String commentId;
    protected String commentUsername;
    protected String commentDate;
    protected String commentContent;


    // ===== CONSTRUCTOR
    public ItemComment() {}


    // ===== CONSTRUCTOR
    public ItemComment(String commentId, String commentUsername, String commentDate, String commentContent) {
        this.commentId = commentId;
        this.commentUsername = commentUsername;
        this.commentDate = commentDate;
        this.commentContent = commentContent;
    }


    // ===== ID
    public String getCommentId() {
        return commentId;
    }
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }


    // ===== USERNAME
    public String getCommentUsername() {
        return commentUsername;
    }
    public void setCommentUsername(String commentUsername) {
        this.commentUsername = commentUsername;
    }


    // ===== DATE
    public String getCommentDate() {
        return commentDate;
    }
    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }


    // ===== CONTENT
    public String getCommentContent() {
        return commentContent;
    }
    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
