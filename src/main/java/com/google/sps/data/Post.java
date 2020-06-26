package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;
import com.google.sps.data.Comment;


public final class Post{
    private final Comment question;
    private final Comment answer;
    private final List<Comment> replies;
    private final long id; 

    public Post(Comment question, Comment answer, List<Comment> replies, long id){
        this.question = question;
        this.answer = answer;
        this.replies = replies;
        this.id = id;
    }
    public Comment getQuestion(){
        return question; 
    }

    public Comment getAnswer(){
        return answer; 
    }

    public List<Comment> getReplies(){
        return replies; 
    }

    public long getID() {
        return id; 
    }

    @Override
    public boolean equals(Object o) {   
        if (!(o instanceof Post)) { 
            return false; 
        } 
         
        Post that = (Post) o;
        return that.getQuestion().equals(this.question) && 
               that.getAnswer().equals(this.answer) && 
               that.getReplies().equals(this.replies); 
    } 

    @Override 
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Post: ").append(System.getProperty("line.separator")); 
        sb.append("Question: ").append(System.getProperty("line.separator")); 
        sb.append(question.toString()); 
        sb.append("Answer: ").append(System.getProperty("line.separator")); 
        sb.append(answer.toString()); 
        sb.append("Replies: ").append(System.getProperty("line.separator")); 
        for (Comment reply : replies) {
            sb.append(reply.toString()); 
        } 
        return sb.toString();
    }
}
