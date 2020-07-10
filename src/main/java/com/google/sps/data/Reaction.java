package com.google.sps.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 
 * Reactions a user can add to a post 
 */
public enum Reaction {
    THUMBS_UP; 

    public static Reaction stringToEnumReaction(String reactionStr) {
        return Reaction.valueOf(reactionStr); 
    }

    public static List<String> getReactionsAsStrings() {
        List<Reaction> reactionEnums = Arrays.asList(Reaction.values());
        List<String> reactionStrings = new ArrayList<String>(); 
        for (Reaction reaction : reactionEnums) {
            reactionStrings.add(reaction.toString()); 
        }
        return reactionStrings; 
    }
}
