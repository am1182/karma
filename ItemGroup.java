package com.politics.karma;


public class ItemGroup {

    private String name, description, creator;
    private long posts, members;


    public ItemGroup() {}


    public ItemGroup(String name, String description, String creator, long posts, long members) {
        this.name = name;
        this.description = description;
        this.posts = posts;
        this.members = members;
        this.creator = creator;
    }


    // ===== NAME
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // ===== CREATOR
    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {this.creator = creator;}


    // ===== DESCRIPTION
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    // ===== POSTS
    public long getPosts() {
        return posts;
    }
    public void setPosts(long posts) {
        this.posts = posts;
    }


    // ===== MEMBERS
    public long getMembers() {
        return members;
    }
    public void setMembers(long members) { this.members = members; }
}
