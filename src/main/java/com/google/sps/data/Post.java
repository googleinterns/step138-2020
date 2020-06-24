package com.google.sps.data;

import com.google.sps.data.Comment;
import java.util.List;

public class Post{
    private final String question;
    private final String answer;
    private final List<Comment> replies;
    private final long id; 

    public Post(String question, String answer, List<Comment> replies, long id){
        this.question = question;
        this.answer = answer;
        this.replies = replies;
        this.id = id; 
    }

    public String getQuestion(){
        return question; 
    }

    public String getAnswer(){
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
