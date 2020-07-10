package com.google.sps.data;

import static org.junit.Assert.assertTrue;
import com.google.sps.data.Comment;
import com.google.sps.data.Representative;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class CommentTest {
    @Test
    public void testToxicityDetector() {
        double toxicity = Comment.toxicityDetector("I like you"); 

        assertTrue(toxicity != -1.0); 
    }
}
