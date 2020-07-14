package com.google.sps.data;

import java.lang.IllegalArgumentException; 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 
 * Reactions a user can add to a post 
 */
public enum Reaction {
    THUMBS_UP("thumbs_up"),
    THUMBS_DOWN("thumbs_down"),
    ANGRY("angry"),
    CRYING("crying"),
    HEART("heart"),
    LAUGHING("laughing");

    private final String value; 

    Reaction(final String value) {
        this.value = value;
    }

    public static Reaction fromString(String reactionStr)
    throws IllegalArgumentException {
        for (Reaction r : Reaction.values()) {
            if (r.getValue() == reactionStr) {
                return r; 
            }
        }
        throw new IllegalArgumentException("No such reaction exists with string value: " + reactionStr);
    }

    public String getValue() {
        return value; 
    }

    public static List<String> allValues() {
        List<Reaction> reactionEnums = Arrays.asList(Reaction.values());
        List<String> reactionStrings = new ArrayList<String>(); 
        for (Reaction reaction : reactionEnums) {
            reactionStrings.add(reaction.getValue()); 
        }
        return reactionStrings; 
    }
}
