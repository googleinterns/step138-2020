package com.google.sps.data;

import com.google.sps.data.Post;
import java.lang.Double; 
import java.lang.System; 
import java.util.ArrayList;
import java.util.Collections; 
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Representative {
    private final String name;
    private final String title;
    private final String username;
    private final String password;
    private final List<Post> posts;
    private final String intro;
    private final String blobKeyUrl;
    private final List<Tab> tabs;
    private final long id; 

    public Representative(String name, String title, String username, 
    String password, List<Post> posts, String intro, String blobKeyUrl, List<Tab> tabs, long id) {
        this.name = name;
        this.title = title;
        this.username = username;
        this.password = password;
        this.posts = posts;
        this.intro = intro;
        this.blobKeyUrl = blobKeyUrl;
        this.tabs = tabs;
        this.id = id; 

        Collections.sort(this.posts, new Post.PostComparator());
    }

    public String getName() {
        return name; 
    }

    public String getTitle() {
        return title; 
    }

    public String getUsername() {
        return username; 
    }

    public String getPassword()  {
        return password; 
    }

    public List<Post> getPosts() {
        return new ArrayList<Post>(posts);
    }

    public String getIntro() {
        return intro;
    }

    public String getBlobKeyUrl() {
        return blobKeyUrl;
    }

    public List<Tab> getTabs() {
        return new ArrayList<Tab>(tabs);
    }

    public long getID() {
        return id; 
    }

    @Override
    public boolean equals(Object o) {   
        if (!(o instanceof Representative)) { 
            return false; 
        } 
         
        Representative that = (Representative) o;  
        return that.getName().equals(this.name) && 
               that.getTitle().equals(this.title) && 
               that.getUsername().equals(this.username) &&
               that.getPassword().equals(this.password) &&
               that.getPosts().equals(this.posts) &&
               that.getIntro().equals(this.intro) &&
               that.getBlobKeyUrl().equals(this.blobKeyUrl) &&
               that.getTabs().equals(this.tabs);
    } 

    @Override 
    public int hashCode() {
        return Objects.hash(name, title, username, password, posts, intro, blobKeyUrl, tabs);
    }

    @Override 
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Representative: ").append(System.getProperty("line.separator")); 
        sb.append("Representative name: ").append(name).append(
            System.getProperty("line.separator")); 
        sb.append("Representative title: ").append(title).append(
            System.getProperty("line.separator")); 
        for (Post post : posts) {
            sb.append(post.toString()); 
        } 
        if (intro != null) {
            sb.append("Representative intro: ").append(intro).append(
                System.getProperty("line.separator"));
        } 
        sb.append("Representative blobKeyUrl: ").append(blobKeyUrl).append(
            System.getProperty("line.separator")); 
        if (tabs != null) {
            for (Tab tab : tabs) {
                sb.append(tab.getTabName() + ": " + tab.getPlatform()); 
            } 
        }
        return sb.toString();
    }
}
