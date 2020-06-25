package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.Comment;
import com.google.sps.data.Parse;
import com.google.sps.data.Post;
import com.google.sps.data.Representative;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** */
@RunWith(JUnit4.class)
public final class ParseTest {
    private static final String REP_ENTITY_TYPE = "Representative";
    private static final String REP_NAME = "Name";
    private static final String REP_TITLE = "Official Title";
    private static final String REP_POSTS = "Posts";

    private static final String POST_ENTITY_TYPE = "Post";
    private static final String POST_QUESTION = "Question";
    private static final String POST_ANSWER = "Answer";
    private static final String POST_REPLIES = "Replies";

    private static final String COMMENT_ENTITY_TYPE = "Comment";
    private static final String COMMENT_NAME = "Nick Name";
    private static final String COMMENT_MSG = "Message";

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private DatastoreService ds; 
    private Representative donald_trump; 
    long rep_id; 

    @Before
    public void setUp() {
        // servlet = new FeedServlet();
        helper.setUp();
        ds = DatastoreServiceFactory.getDatastoreService();

        Entity comment_entity = new Entity(COMMENT_ENTITY_TYPE); 
        comment_entity.setProperty(COMMENT_MSG, "Nice dude"); 
        comment_entity.setProperty(COMMENT_NAME, "Anonymous"); 
        ds.put(comment_entity); 
        long comment_id = comment_entity.getKey().getId(); 
        List<Long> comment_ids = new ArrayList<>(); 
        comment_ids.add(comment_id); 

        Entity comment_entity_question = new Entity(COMMENT_ENTITY_TYPE); 
        comment_entity_question.setProperty(COMMENT_MSG, "Why are you in office?"); 
        comment_entity_question.setProperty(COMMENT_NAME, "Anonymous"); 
        ds.put(comment_entity_question); 
        long comment_id_question = comment_entity_question.getKey().getId(); 

        Entity comment_entity_answer = new Entity(COMMENT_ENTITY_TYPE); 
        comment_entity_answer.setProperty(COMMENT_MSG, "Because I want to be."); 
        comment_entity_answer.setProperty(COMMENT_NAME, "Donald Trump"); 
        ds.put(comment_entity_answer); 
        long comment_id_answer = comment_entity_answer.getKey().getId(); 

        Entity post_entity = new Entity(POST_ENTITY_TYPE); 
        post_entity.setProperty(POST_QUESTION, comment_id_question); 
        post_entity.setProperty(POST_ANSWER, comment_id_answer); 
        post_entity.setProperty(POST_REPLIES, comment_ids); 
        ds.put(post_entity); 
        long post_id = post_entity.getKey().getId(); 
        List<Long> post_ids = new ArrayList<>(); 
        post_ids.add(post_id); 

        Entity rep_entity = new Entity(REP_ENTITY_TYPE); 
        rep_entity.setProperty(REP_NAME, "Donald Trump"); 
        rep_entity.setProperty(REP_TITLE, "President of the US"); 
        rep_entity.setProperty(REP_POSTS, post_ids); 
        ds.put(rep_entity);
        this.rep_id = rep_entity.getKey().getId(); 

        Comment comment = new Comment("Anonymous", "Nice dude", comment_id); 
        List<Comment> replies = new ArrayList<>();
        replies.add(comment); 
        Comment comment_question = new Comment("Anonymous", "Why are you in office?", comment_id_question); 
        Comment comment_answer = new Comment("Donald Trump", "Because I want to be.", comment_id_answer); 
        Post post = new Post(comment_question, comment_answer, replies, post_id); 
        List<Post> posts = new ArrayList<>();
        posts.add(post); 
        donald_trump = new Representative("Donald Trump", "President of the US", posts, rep_id); 
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testParseRepresentative() throws EntityNotFoundException{
        // Query query = new Query(REP_ENTITY_TYPE); 
        // PreparedQuery results = ds.prepare(query);
        Entity rep_entity = Parse.queryForRepresentative(rep_id);
        Representative actual = Parse.parseRepresentative(rep_entity); 
        System.out.println(donald_trump); 
        System.out.flush(); 
        System.out.println(actual); 
        System.out.flush(); 
        Assert.assertTrue(actual.equals(donald_trump));
    }
}
