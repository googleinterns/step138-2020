package com.google.sps.data;

import com.google.sps.data.Post;
import java.util.List;

public class Representative{
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
        return posts; 
    }

    public long getID() {
        return id; 
    }

    @Override
    public boolean equals(Object o) {   
        if (o == this) { 
            return true; 
        } 
  
        if (!(o instanceof Representative)) { 
            return false; 
        } 
         
        Representative r = (Representative) o; 
        return r.getName() == name && 
               r.getTitle() == title && 
               r.getPosts().equals(posts); 
    } 
}
