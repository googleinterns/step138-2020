package com.google.sps.data;

import java.util.Arrays;

/** 
 * Reactions a user can add to a post 
 */
public enum Reaction {
    THUMBS_UP; 

    public Reaction stringToEnumReaction(String reactionStr) {
        return Reaction.valueOf(reactionStr); 
    }

    public List<String> getReactionsAsStrings() {
        return Arrays.asList(Reaction.values());
    }
}