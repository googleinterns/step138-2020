package com.google.sps.data;

import com.google.sps.data.Post;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Representative{
    private final String name;
    private final String title;
    private final String username;
    private final String password;
    private final List<Post> posts;
    private final long id; 

    public Representative(String name, String title, String username, 
    String password, List<Post> posts, long id){
        this.name = name;
        this.title = title;
        this.username = username;
        this.password = password;
        this.posts = posts;
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
               that.getPosts().equals(this.posts); 
    } 

    @Override 
    public int hashCode() {
        return Objects.hash(name, title, username, password, posts);
    }

    @Override 
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Representative: ").append(System.getProperty("line.separator")); 
        sb.append("Representative name: ").append(name).append(System.getProperty("line.separator")); 
        sb.append("Representative title: ").append(title).append(System.getProperty("line.separator")); 
        for (Post post : posts) {
            sb.append(post.toString()); 
        } 
        return sb.toString();
    }
}
