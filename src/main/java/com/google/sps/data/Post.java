package com.google.sps.data;

import java.util.List;
import com.google.sps.data.Comment;


public class Post{
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
        if (o == this) { 
            return true; 
        } 
  
        if (!(o instanceof Post)) { 
            return false; 
        } 
         
        Post p = (Post) o; 
        return p.getQuestion() == question && 
               p.getAnswer() == answer && 
               p.getReplies().equals(replies); 
    } 
}
