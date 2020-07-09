package com.google.sps.data;

import com.google.sps.data.Post;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Representative {
    private final String name;
    private final String title;
    private final String username;
    private final String password;
    private final List<Post> posts;
    private final String intro;
    private final List<Tab> tabs;
    private final long id; 

    public Representative(String name, String title, String username, 
    String password, List<Post> posts, String intro, List<Tab> tabs, long id){
        this.name = name;
        this.title = title;
        this.username = username;
        this.password = password;
        this.posts = posts;
        this.intro = intro;
        this.tabs = tabs;
        this.id = id; 
    }

    public String getName(){
        return name; 
    }

    public String getTitle(){
        return title; 
    }

    public String getUsername(){
        return username; 
    }

    public String getPassword(){
        return password; 
    }

    public List<Post> getPosts(){
        return new ArrayList<Post>(posts);
    }

    public String getIntro(){
        return intro;
    }

    public List<Tab> getTabs(){
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
               that.getTabs().equals(this.tabs);
    } 

    @Override 
    public int hashCode() {
        return Objects.hash(name, title, username, password, posts, intro, tabs);
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
        if (intro != null){
            sb.append("Representative intro: ").append(intro).append(
                System.getProperty("line.separator"));
        } 
        if (tabs != null){
            for (Tab tab : tabs) {
                sb.append(tab.getTabName() + ": " + tab.getPlatform()); 
            } 
        }
        return sb.toString();
    }
}
