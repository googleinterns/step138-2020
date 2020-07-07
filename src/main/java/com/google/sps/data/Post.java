package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.google.sps.data.Comment;

public final class Post{
    private final Comment question;
    private final Comment answer;
    private final List<Comment> replies;
    private final String tab;
    private final long id; 

    public Post(Comment question, Comment answer, List<Comment> replies, String tab, long id){
        this.question = question;
        this.answer = answer;
        this.replies = replies;
        this.tab = tab;
        this.id = id;
    }

    public Comment getQuestion(){
        return question; 
    }

    public Comment getAnswer(){
        return answer; 
    }

    public List<Comment> getReplies(){
        return new ArrayList<Comment>(replies);
    }

    public String getTab(){
        return tab;
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

        // answer might be null if rep hasn't yet answered 
        boolean answerEquality = false; 
        if (that.getAnswer() == null && this.answer == null) {
            answerEquality = true; 
        }
        if (that.getAnswer() != null && this.answer != null 
            && that.getAnswer().equals(this.answer)) {
                answerEquality = true; 
        }
        
        return that.getQuestion().equals(this.question) && 
               answerEquality && 
               that.getReplies().equals(this.replies) &&
               that.getTab().equals(this.tab); 
    } 

    @Override
    public int hashCode() {
        return Objects.hash(question, answer, replies, tab);
    }

    @Override 
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Post: ").append(System.getProperty("line.separator")); 
        sb.append("Question: ").append(System.getProperty("line.separator")); 
        sb.append(question.toString()); 
        sb.append("Answer: ").append(System.getProperty("line.separator")); 
        sb = (answer == null) ? sb.append("") : sb.append(answer.toString()); 
        sb.append("Replies: ").append(System.getProperty("line.separator")); 
        for (Comment reply : replies) {
            sb.append(reply.toString()); 
        } 
        sb.append("Tab: ").append(System.getProperty("line.separator")); 
        sb.append(tab.toString());         
        return sb.toString();
    }
}
