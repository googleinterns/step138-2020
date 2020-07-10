package com.google.sps.data;

import java.util.Objects;
import com.google.sps.data.Constants;

public final class Comment{
    private static final long COMMENT_TOXICITY_THRESHOLD = 0.8;  
    private final String name;
    private final String comment;
    private final long id;

    public Comment(String name, String comment, long id){
        this.name = name;
        this.comment = comment;
        this.id = id; 
    }

    public static long toxicityDetector(String msg) {
        //TODO 
    }

    public String getDisplayName(){
        return name; 
    }

    public String getComment(){
        return comment; 
    }

    public long getID(){
        return id; 
    }

    @Override
    public boolean equals(Object o) {   
        if (!(o instanceof Comment)) { 
            return false; 
        } 
        Comment that = (Comment) o;
        return that.getComment().equals(this.comment)
            && that.getDisplayName().equals(this.name);
    } 

    @Override 
    public int hashCode() {
        return Objects.hash(name, comment);
    }
    
    @Override 
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nickname: ").append(name).append(System.getProperty("line.separator")); 
        sb.append("Message: ").append(comment).append(System.getProperty("line.separator")); 
        return sb.toString();
    }
}
