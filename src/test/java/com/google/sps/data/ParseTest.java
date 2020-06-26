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
import com.google.sps.data.Constants;
import com.google.sps.data.InsertAndUpdate;
import com.google.sps.data.Parse;
import com.google.sps.data.QueryDatastore;
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
    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private DatastoreService ds; 
    private Representative donaldTrump; 

    @Before
    public void setUp() {
        helper.setUp();
        ds = DatastoreServiceFactory.getDatastoreService();

        long commentId = InsertAndUpdate.insertCommentDatastore("Anonymous", "Nice dude"); 
        List<Long> commentIds = new ArrayList<>(); 
        commentIds.add(commentId); 
        long commentIdQuestion = InsertAndUpdate.insertCommentDatastore("Anonymous", "Why are you in office?");
        long commentIdAnswer = InsertAndUpdate.insertCommentDatastore("Donald Trump", "Because I want to be.");

        Entity postEntity = new Entity(Constants.POST_ENTITY_TYPE); 
        postEntity.setProperty(Constants.POST_QUESTION, commentIdQuestion); 
        postEntity.setProperty(Constants.POST_ANSWER, commentIdAnswer); 
        postEntity.setProperty(Constants.POST_REPLIES, commentIds); 
        ds.put(postEntity); 
        long postId = postEntity.getKey().getId(); 
        List<Long> postIds = new ArrayList<>(); 
        postIds.add(postId); 

        Entity repEntity = new Entity(Constants.REP_ENTITY_TYPE); 
        repEntity.setProperty(Constants.REP_NAME, "Donald Trump"); 
        repEntity.setProperty(Constants.REP_TITLE, "President of the US"); 
        repEntity.setProperty(Constants.REP_POSTS, postIds); 
        ds.put(repEntity);
        long repId = repEntity.getKey().getId(); 

        Comment comment = new Comment("Anonymous", "Nice dude", commentId); 
        List<Comment> replies = new ArrayList<>();
        replies.add(comment); 
        Comment commentQuestion = new Comment("Anonymous", "Why are you in office?", commentIdQuestion); 
        Comment commentAnswer = new Comment("Donald Trump", "Because I want to be.", commentIdAnswer); 
        Post post = new Post(commentQuestion, commentAnswer, replies, postId); 
        List<Post> posts = new ArrayList<>();
        posts.add(post); 
        donaldTrump = new Representative("Donald Trump", "President of the US", posts, repId); 
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testParseRepresentative() throws EntityNotFoundException{
        Entity trumpEntity = QueryDatastore.queryForRepresentative("Donald Trump");

        Representative actual = Parse.parseRepresentative(trumpEntity); 
        
        Assert.assertTrue(actual.equals(donaldTrump));
    }
}
