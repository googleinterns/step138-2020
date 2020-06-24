package com.google.sps.data;

public class Comment{
    private final String name;
    private final String comment;
    private final long id;

    public Comment(String name, String comment, long id){
        this.name = name;
        this.comment = comment;
        this.id = id; 
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
        if (o == this) { 
            return true; 
        } 
  
        if (!(o instanceof Comment)) { 
            return false; 
        } 
         
        Comment c = (Comment) o; 
        return c.getComment() == comment && 
               c.getDisplayName() == name; 
    } 
}
