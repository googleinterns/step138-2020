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
public final class InsertAndUpdateTest {
    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private DatastoreService ds; 

    @Before
    public void setUp() {
        helper.setUp();
        ds = DatastoreServiceFactory.getDatastoreService();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testInsertCommentDatastore() throws EntityNotFoundException{
        long commentId = InsertAndUpdate.insertCommentDatastore("Anonymous", "Nice dude"); 
        Key commentEntityKey = KeyFactory.createKey(Constants.COMMENT_ENTITY_TYPE, commentId);
        Entity commentEntity = (Entity) ds.get(commentEntityKey);
        String name = (String) (commentEntity.getProperty(Constants.COMMENT_NAME));
        String msg = (String) (commentEntity.getProperty(Constants.COMMENT_MSG));
        Assert.assertTrue(name.equals("Anonymous")); 
        Assert.assertTrue(msg.equals("Nice dude")); 
    }

    @Test
    public void testInsertRepresentativeDatastore() throws EntityNotFoundException{
        long repId = InsertAndUpdate.insertRepresentativeDatastore("Donald Trump", "President of the US"); 
        Key repEntityKey = KeyFactory.createKey(Constants.REP_ENTITY_TYPE, repId);
        Entity repEntity = (Entity) ds.get(repEntityKey);
        String name = (String) (repEntity.getProperty(Constants.REP_NAME));
        String title = (String) (repEntity.getProperty(Constants.REP_TITLE));
        Assert.assertTrue(name.equals("Donald Trump")); 
        Assert.assertTrue(title.equals("President of the US")); 
    }
}
