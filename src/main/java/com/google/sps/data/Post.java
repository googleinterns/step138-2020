package com.google.sps.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.google.sps.data.Comment;
import com.google.sps.data.Reaction;

public final class Post{
    private final Comment question;
    private final Comment answer;
    private final List<Comment> replies;
    private final long id; 
    private final Map<Reaction, Long> reactions; 

    public Post(Comment question, Comment answer, List<Comment> replies, long id, Map<Reaction, Long> reactions){
        this.question = question;
        this.answer = answer;
        this.replies = replies;
        this.id = id;
        this.reactions = reactions; 
    }

    public Map<Reaction, Long> getReactions() {
        return new HashMap<Reaction, Long>(reactions);
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
               that.getReactions().equals(this.reactions); 
    } 

    @Override
    public int hashCode() {
        return Objects.hash(question, answer, replies, reactions);
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
        sb.append("Reactions: ").append(System.getProperty("line.separator")); 
        for (Map.Entry<Reaction, Long> entry : reactions.entrySet())
        {
            sb.append(entry.getKey().getValue() + ": "); 
            sb.append(String.valueOf(entry.getValue())).append(System.getProperty("line.separator")); 
        }
        return sb.toString();
    }
}
