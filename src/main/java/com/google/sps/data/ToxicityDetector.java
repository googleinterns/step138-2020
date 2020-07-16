package com.google.sps.data;

import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.AnalyzeCommentResponse;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.AttributeScore;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.AttributeType;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.PerspectiveAPI;
import java.lang.IllegalStateException; 
import java.util.Map;
import com.google.sps.data.Constants;

public final class ToxicityDetector {
    private final String apiKey; 

    public boolean isCommentToxic(String msg) {
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

    public ToxicityDetector() {
        apiKey = System.getenv(Constants.PERSPECTIVE_API_KEY); 
    }
}
