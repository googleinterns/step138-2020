package com.google.sps.data;

public final class Comment{
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
        if (!(o instanceof Comment)) { 
            return false; 
        } 
        Comment that = (Comment) o;
        return that.getComment().equals(this.comment)
            && that.getDisplayName().equals(this.name);
    } 

    @Override 
    public int hashCode() {
        int prime = 31;
        return prime + name.hashCode() + comment.hashCode();    
    }
    
    @Override 
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nickname: ").append(name).append(System.getProperty("line.separator")); 
        sb.append("Message: ").append(comment).append(System.getProperty("line.separator")); 
        return sb.toString();
    }
}
