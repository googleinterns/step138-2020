package com.google.sps.data;

import static org.junit.Assert.assertTrue;
import com.google.sps.data.ToxicityDetector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class ToxicityDetectorTest {
    private ToxicityDetector detector;

    @Before
    public void setUp() {
        detector = new ToxicityDetector(); 
    }

    @Test
    public void testToxicComment() {
        String toxicComment = "I hate you and everything you stand for."; 
        assertTrue(detector.isCommentToxic(toxicComment)); 
    }

    @Test
    public void testNonToxicComment() {
        String nonToxicComment = "Do you like pie?"; 
        assertTrue(!detector.isCommentToxic(nonToxicComment)); 
    }
}
