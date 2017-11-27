package com.politics.karma;


import android.graphics.drawable.Drawable;

public class ItemNotification {

    private String message, postContent, senderId, postId;
    private Drawable icon;


    // ===== FOLLOW
    public ItemNotification (String message, String senderId, Drawable icon) {
        this.message = message;
        this.senderId = senderId;
        this.icon = icon;
    }


    // ===== COMMENT
    public ItemNotification (String message, String postId, String postContent, Drawable icon) {
        this.message = message;
        this.postId = postId;
        this.postContent = postContent;
        this.icon = icon;
    }


    // ===== MESSAGE
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }


    // ===== POST CONTENT
    public String getPostContent() {
        return postContent;
    }
    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }


    // ===== SENDER ID
    public String getSenderId() {
        return senderId;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }


    // ===== POST ID
    public String getPostId() {
        return postId;
    }
    public void setPostId(String postId) {
        this.postId = postId;
    }


    // ===== ICON
    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
