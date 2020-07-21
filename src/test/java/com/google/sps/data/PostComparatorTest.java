package com.google.sps.data;

import static org.junit.Assert.assertTrue;
import com.google.sps.data.Comment;
import com.google.sps.data.Post;
import com.google.sps.data.Reaction;
import java.util.ArrayList;
import java.util.Collections; 
import java.util.HashMap; 
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class PostComparatorTest {
    private List<Post> posts;
    Post older; 
    Post newer; 

    @Before
    public void setUp() {
        Comment oldQuestion = new Comment("Bob", "Why are you president?", -1); 
        Comment oldAnswer = new Comment("Donald", "Because I want to be.", -1); 
        older = new Post(oldQuestion, oldAnswer, new ArrayList<>(), "", -1, 
            new HashMap<Reaction, Long>(), 15953616469L); 

        Comment newQuestion = new Comment("Sally", "What about climate change?", -1); 
        Comment newAnswer = new Comment("Donald", "It's fake.", -1); 
        newer = new Post(newQuestion, newAnswer, new ArrayList<>(), "", -1, 
            new HashMap<Reaction, Long>(), 15953616479L); 

        posts = new ArrayList<>(); 
        posts.add(newer); 
        posts.add(older); 
    }

    @Test
    public void testPostComparator() {
        Collections.sort(posts, new Post.PostComparator());
        assertTrue(posts.get(0).equals(older)); 
        assertTrue(posts.get(1).equals(newer)); 
    }
}
