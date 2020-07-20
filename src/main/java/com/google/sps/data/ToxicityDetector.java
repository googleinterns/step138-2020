package com.google.sps.data;

import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.AnalyzeCommentResponse;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.AttributeScore;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.AttributeType;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.PerspectiveAPI;
import java.util.Map;
import com.google.sps.data.Constants;

public final class ToxicityDetector {
    private static final String apiKey = System.getenv(Constants.PERSPECTIVE_API_KEY); 

    public static boolean isCommentToxic(String msg) {
        PerspectiveAPI pAPI = PerspectiveAPI.create(apiKey); 
        AnalyzeCommentResponse response = pAPI.analyze(msg); 
        if (response == null) {
            return false; 
        }

        Map<AttributeType, AttributeScore> scores = response.getAttributeScores(); 
        if (!scores.containsKey(AttributeType.TOXICITY)) {
            return false; 
        }
        
        AttributeScore score = scores.get(AttributeType.TOXICITY);
        double toxicity = score.getSummaryScore().getValue(); 
        return toxicity >= Constants.COMMENT_TOXICITY_THRESHOLD; 
    }

    private ToxicityDetector() {}
}
