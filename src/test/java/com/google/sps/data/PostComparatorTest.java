package com.google.sps.data;

import static org.junit.Assert.assertTrue;
import com.google.sps.data.Comment;
import com.google.sps.data.Post;
import com.google.sps.data.Reaction;
import java.util.ArrayList;
import java.util.Collections; 
import java.util.HashMap; 
import java.util.List;
import java.util.Map; 
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class PostComparatorTest {
    private Post newer; 
    private List<Post> posts; 
    private Comment oldQuestion; 
    private Comment oldAnswer; 

    @Before 
    public void setUp() {
        Comment newQuestion = new Comment("Sally", "What about climate change?", -1); 
        Comment newAnswer = new Comment("Donald", "It's fake.", -1); 
        oldQuestion = new Comment("Bob", "Why are you president?", -1); 
        oldAnswer = new Comment("Donald", "Because I want to be.", -1); 
        newer = new Post(newQuestion, newAnswer, new ArrayList<>(), new ArrayList<>(), -1, 
            new HashMap<Reaction, Long>(), 15953616479L); 
        posts = new ArrayList<>(); 
        posts.add(newer); 
    }

    @Test
    public void testPostComparatorForTimestamp() {
        Post older = new Post(oldQuestion, oldAnswer, new ArrayList<>(), new ArrayList<>(), -1, 
            new HashMap<Reaction, Long>(), 15953616469L); 
        posts.add(older); 

        Collections.sort(posts, new Post.PostComparator());
        assertTrue(posts.get(0).equals(newer)); 
        assertTrue(posts.get(1).equals(older)); 
    }

    @Test
    public void testPostComparatorForReactions() {
        Map<Reaction, Long> reactionsOlder = new HashMap<Reaction, Long>(); 
        reactionsOlder.put(Reaction.THUMBS_UP, 10000L); 
        Post older = new Post(oldQuestion, oldAnswer, new ArrayList<>(), new ArrayList<>(), -1, 
            reactionsOlder, 15953616469L); 
        posts.add(older);  

        Collections.sort(posts, new Post.PostComparator());
        assertTrue(posts.get(0).equals(older)); 
        assertTrue(posts.get(1).equals(newer)); 
    }
}
