package com.google.sps.data;

import com.google.sps.data.Post;
import java.util.ArrayList;
import java.util.List;

public final class Representative{
    private final String name;
    private final String title;
    private final List<Post> posts;
    private final long id; 

    public Representative(String name, String title, List<Post> posts, long id){
        this.name = name;
        this.title = title;
        this.posts = posts;
        this.id = id; 
    }

    public String getName(){
        return name; 
    }

    public String getTitle(){
        return title; 
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
               that.getPosts().equals(this.posts); 
    } 

    @Override
    public int hashCode(){
        return 42;
        //TODO(swetha-gangu): implement hashCode()
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
