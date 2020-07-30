package com.google.sps.data;

import static java.lang.Math.abs; 
import java.util.ArrayList;
import java.util.Comparator; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.google.sps.data.Comment;
import com.google.sps.data.Reaction;

public final class Post {
    private final Comment question;
    private final Comment answer;
    private final List<Comment> replies;
    private final List<String> tabs;
    private final long id; 
    private final Map<Reaction, Long> reactions;
    private final long timestamp;  

    public Post(Comment question, Comment answer, List<Comment> replies, List<String> tabs, 
                long id, Map<Reaction, Long> reactions, long timestamp) {
        this.question = question;
        this.answer = answer;
        this.replies = replies;
        this.tabs = tabs;
        this.id = id;
        this.reactions = reactions; 
        this.timestamp = timestamp; 
    }

    public Map<Reaction, Long> getReactions() {
        return new HashMap<Reaction, Long>(reactions);
    }

    public Comment getQuestion() {
        return question; 
    }

    public Comment getAnswer() {
        return answer; 
    }

    public List<Comment> getReplies() {
        return new ArrayList<Comment>(replies);
    }

    public List<String> getTabs() {
        return new ArrayList<String> (tabs);
    }

    public long getID() {
        return id; 
    }

    public long getTimestamp() {
        return timestamp; 
    }

    public static long totalReactionCount(Post post) {
        long count = 0; 
        for (Map.Entry<Reaction, Long> entry : post.getReactions().entrySet()) {
            count += entry.getValue(); 
        }
        return count; 
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
               that.getReactions().equals(this.reactions) && 
               that.getTabs().equals(this.tabs); 
    } 

    @Override
    public int hashCode() {
        return Objects.hash(question, answer, replies, tabs, reactions);
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
        for (Map.Entry<Reaction, Long> entry : reactions.entrySet()) {
            sb.append(entry.getKey().getValue() + ": "); 
            sb.append(String.valueOf(entry.getValue())).append(System.getProperty("line.separator")); 
        }
        sb.append("Tabs: ").append(System.getProperty("line.separator")); 
        for (String tab : tabs) {
            sb.append(tab.toString()); 
        } 
        return sb.toString();
    }

    static class PostComparator implements Comparator<Post> {
        @Override
        public int compare(Post a, Post b) {
            long currTime = System.currentTimeMillis();

            //Cast the timestamps into double between 0 and 1 
            double aScore = (double) a.getTimestamp() / (double) currTime;
            double bScore = (double) b.getTimestamp() / (double) currTime;

            //Difference between number of reactions in a post falls into a particular bucket
            //which shows how much of a boost should be given to the post with more reactions 
            long deltaReactions = abs(Post.totalReactionCount(a) - Post.totalReactionCount(b));
            int reactionBoostIndex = Constants.REACTION_BUCKET_BOOSTS.length - 1;
            for (int i = 0; i < Constants.REACTION_DELTA_BUCKETS.length; i++) {
                if (deltaReactions <= Constants.REACTION_DELTA_BUCKETS[i]) {
                    reactionBoostIndex = i; 
                    break; 
                }
            }

            double reactionBoost = Constants.REACTION_BUCKET_BOOSTS[reactionBoostIndex]; 
            if (Post.totalReactionCount(a) >= Post.totalReactionCount(b)) {
                aScore += reactionBoost; 
            }
            else {
                bScore += reactionBoost; 
            }

            //Want to sort in descending order of scores 
            //Post with bigger timestamp and more reactions at top of feed 
            return Double.compare(bScore, aScore); 
        }
    }
}
