package com.google.sps.data;

import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.AnalyzeCommentResponse;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.AttributeScore;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.AttributeType;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.PerspectiveAPI;
import java.lang.IllegalStateException; 
import java.util.Map;
import java.util.Objects;
import com.google.sps.data.Constants;

public final class Comment {
    private final String name;
    private final String comment;
    private final long id;
    private static final String apiKey = System.getenv(Constants.PERSPECTIVE_API_KEY); 

    public Comment(String name, String comment, long id) {
        this.name = name;
        this.comment = comment;
        this.id = id; 
    }

    public String getDisplayName() {
        return name; 
    }

    public String getComment() {
        return comment; 
    }

    public long getID() {
        return id; 
    }

    public static boolean isCommentToxic(String msg) {
        PerspectiveAPI pAPI = PerspectiveAPI.create(apiKey); 
        AnalyzeCommentResponse response = pAPI.analyze(msg); 
        if (response == null) {
            throw new IllegalStateException("Perspective API was unable to analyze your comment"); 
        }
        Map<AttributeType, AttributeScore> scores = response.getAttributeScores(); 
        if (!scores.containsKey(AttributeType.TOXICITY)) {
            return false; 
        }
        AttributeScore score = scores.get(AttributeType.TOXICITY);
        double toxicity = score.getSummaryScore().getValue(); 
        return toxicity >= Constants.COMMENT_TOXICITY_THRESHOLD; 
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
