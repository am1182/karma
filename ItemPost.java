package com.politics.karma;


public class ItemPost {

    private String id, username, date, content, group, creator;
    private long likes, dislikes, comments;


    public ItemPost() {}


    public ItemPost(String id, String creator, String username, String date,
                    String content, String group, long likes, long dislikes, long comments) {
        this.id = id;
        this.creator = creator;
        this.username = username;
        this.date = date;
        this.content = content;
        this.group = group;

        this.likes = likes;
        this.dislikes = dislikes;
        this.comments = comments;
    }


    // ===== ID
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }


    // ===== CREATOR
    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }


    // ===== USERNAME
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }


    // ===== TIMESTAMP
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }


    // ===== CONTENT
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }


    // ===== GROUP
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }


    // ===== LIKES
    public long getLikes() {
        return likes;
    }
    public void setLikes(long likes) {
        this.likes = likes;
    }


    // ===== DISLIKES
    public long getDislikes() {
        return dislikes;
    }
    public void setDislikes(long dislikes) { this.dislikes = dislikes; }


    // ===== COMMENTS
    public long getComments() {
        return comments;
    }
    public void setComments(long comments) { this.comments = comments; }
}

















