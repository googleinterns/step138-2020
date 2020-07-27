package com.google.sps.data;

import static org.junit.Assert.assertTrue;
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
import com.google.sps.data.DatastoreEntityToObjectConverter;
import com.google.sps.data.DatastoreManager;
import com.google.sps.data.Post;
import com.google.sps.data.Representative;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class DatastoreEntityToObjectConverterTest {
    private LocalServiceTestHelper helper;
    private DatastoreService ds; 
    private Representative donaldTrump; 
    private Post post; 
    private Tab tab;
    private Entity repEntity; 
    private Entity postEntity;
    private Entity tabEntity; 

    @Before
    public void setUp() {
        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();
        ds = DatastoreServiceFactory.getDatastoreService();

        long commentId = DatastoreManager.insertCommentInDatastore("Anonymous", "Nice dude"); 
        List<Long> commentIds = new ArrayList<>(); 
        commentIds.add(commentId); 
        long commentIdQuestion = DatastoreManager.insertCommentInDatastore
            ("Anonymous", "Why are you in office?");
        long commentIdAnswer = DatastoreManager.insertCommentInDatastore
            ("Donald Trump", "Because I want to be.");
            
        this.tabEntity = new Entity(Constants.TAB_ENTITY_TYPE);
        tabEntity.setProperty(Constants.TAB_NAME, "Education");
        tabEntity.setProperty(Constants.TAB_PLATFORM, "Platform");
        ds.put(tabEntity);
        long tabId = tabEntity.getKey().getId();

        this.tab = new Tab("Education", "Platform", tabId);

        this.postEntity = new Entity(Constants.POST_ENTITY_TYPE); 
        postEntity.setProperty(Constants.POST_QUESTION, commentIdQuestion); 
        postEntity.setProperty(Constants.POST_ANSWER, commentIdAnswer); 
        postEntity.setProperty(Constants.POST_REPLIES, commentIds); 
        postEntity.setProperty(Constants.POST_TABS, Arrays.asList("Education"));
        ds.put(postEntity); 
        long postId = postEntity.getKey().getId(); 
        List<Long> postIds = new ArrayList<>(); 
        postIds.add(postId); 

        this.repEntity = new Entity(Constants.REP_ENTITY_TYPE); 
        repEntity.setProperty(Constants.REP_NAME, "Donald Trump"); 
        repEntity.setProperty(Constants.REP_TITLE, "President of the US");
        repEntity.setProperty(Constants.REP_USERNAME, "username");
        repEntity.setProperty(Constants.REP_PASSWORD, "password"); 
        repEntity.setProperty(Constants.REP_POSTS, postIds);
        repEntity.setProperty(Constants.REP_INTRO, "This is my Intro");
        repEntity.setProperty(Constants.REP_BLOB_KEY_URL, "blobKeyUrl");
        repEntity.setProperty(Constants.REP_TABS, new ArrayList<Long>(Arrays.asList(tabId)));
        repEntity.setProperty(Constants.REP_STATUS, true);
        ds.put(repEntity);
        long repId = repEntity.getKey().getId(); 

        Comment comment = new Comment("Anonymous", "Nice dude", commentId); 
        List<Comment> replies = new ArrayList<>();
        replies.add(comment); 
        Comment commentQuestion = new Comment("Anonymous", "Why are you in office?", 
            commentIdQuestion); 
        Comment commentAnswer = new Comment("Donald Trump", "Because I want to be.", 
            commentIdAnswer); 
        Map<Reaction, Long> reactions = new HashMap<Reaction, Long>(); 
        for (Reaction reaction : Reaction.values()) { 
            reactions.put(reaction, (long) 0);
        }
        this.post = new Post(commentQuestion, commentAnswer, replies, Arrays.asList(tab.getTabName()), postId, reactions, 0); 
        List<Post> posts = new ArrayList<>();
        posts.add(post); 
        donaldTrump = new Representative("Donald Trump", "President of the US", "username", 
        "password", posts, "This is my Intro", "blobKeyUrl", new ArrayList<Tab>(Arrays.asList(tab)), repId); 
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testConvertRepresentative() throws EntityNotFoundException{
        Representative actual = DatastoreEntityToObjectConverter.convertRepresentative(repEntity); 
        assertTrue(actual.equals(donaldTrump));
    }

    @Test 
    public void testConvertPost() throws EntityNotFoundException {
        Post actual = DatastoreEntityToObjectConverter.convertPost(postEntity); 
        assertTrue(actual.equals(post)); 
    }

    @Test
    public void testConvertTab() throws EntityNotFoundException {
        Tab actual = DatastoreEntityToObjectConverter.convertTab(tabEntity);

        assertTrue(actual.equals(tab));
    }

    @Test
    public void testconvertTabsFromRep() throws EntityNotFoundException {
        List<Tab> actual = DatastoreEntityToObjectConverter.convertTabsFromRep(repEntity);

        assertTrue(actual.equals(new ArrayList<Tab> (Arrays.asList(tab))));
    }
}
