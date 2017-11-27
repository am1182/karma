package com.politics.karma;


public class ItemUser {

    private String userId, username;
    private long followers, following, posts, groups;


    public ItemUser(String userId, String username, long followers, long following, long posts, long groups) {
        this.userId = userId;
        this.username = username;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
        this.groups = groups;
    }


    // ===== USER ID
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }


    // ===== USERNAME
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) { this.username = username; }


    // ===== FOLLOWERS
    public long getFollowers() {
        return followers;
    }
    public void setFollowers(long followers) {
        this.followers = followers;
    }


    // ===== FOLLOWING
    public long getFollowing() {
        return following;
    }
    public void setFollowing(long following) {
        this.following = following;
    }


    // ===== POSTS
    public long getPosts() {
        return posts;
    }
    public void setPosts(long posts) {
        this.posts = posts;
    }


    // ===== GROUPS
    public long getGroups() {
        return groups;
    }
    public void setGroups(long groups) {
        this.groups = groups;
    }
}
