package com.google.sps.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** 
 * Reactions a user can add to a post 
 */
public enum Reaction {
    THUMBS_UP; 

    public static Reaction fromString(String reactionStr) {
        return Reaction.valueOf(reactionStr); 
    }

    public static List<String> allValues() {
        List<Reaction> reactionEnums = Arrays.asList(Reaction.values());
        List<String> reactionStrings = new ArrayList<String>(); 
        for (Reaction reaction : reactionEnums) {
            reactionStrings.add(reaction.toString()); 
        }
        return reactionStrings; 
    }

    // @Override 
    // public String toString() {
    //     return this.reaction; 
    // }

    // @Override
    // public int hashCode() {
    //     return Objects.hash(reaction);
    // }
}
