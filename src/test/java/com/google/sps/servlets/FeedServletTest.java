package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
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
public final class FeedServletTest {
  private static final String REP_ENTITY_TYPE = "Representative";
  private static final String REP_NAME = "Name";
  private static final String REP_TITLE = "Official Title";
  private static final String REP_POSTS = "Posts";

  private static final String POST_ENTITY_TYPE = "Post";
  private static final String POST_QUESTION = "Question";
  private static final String POST_ANSWER = "Answer";
  private static final String POST_REPLIES = "Replies";

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private final FeedServlet servlet;
  private final DatastoreService ds; 
  private final Representative donald_trump; 

  @Before
  public void setUp() {
    servlet = new FeedServlet();
    helper.setUp();
    ds = DatastoreServiceFactory.getDatastoreService();

    Entity post_entity = new Entity(POST_ENTITY_TYPE); 
    post_entity.setProperty(POST_QUESTION, "Why are you in office?"); 
    post_entity.setProperty(POST_ANSWER, "Because I want to be."); 
    post_entity.setProperty(POST_REPLIES, ListValue.of("Nice dude", "ok")); 
    Entity rep_entity = new Entity(REP_ENTITY_TYPE); 
    rep_entity.setProperty(REP_NAME, "Donald Trump"); 
    rep_entity.setProperty(REP_TITLE, "President of the US"); 
    rep_entity.setProperty(REP_POSTS, post_entity); 
    ds.put(rep_entity);

    List<Comment> replies = new ArrayList<>();
    replies.add("Nice dude"); 
    replies.add("ok"); 
    Post post = new Post("Why are you in office?", "Because I want to be.", replies); 
    List<Post> posts = new ArrayList<>();
    posts.add(post); 
    donald_trump = new Representative("Donald Trump", "President of the US", posts); 
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testParseRepresentative() {
    Query query = new Query(REP_ENTITY_TYPE); 
    PreparedQuery results = ds.prepare(query);
    Entity rep_entity = servlet.queryForRepresentative(results, "Donald Trump");
    Representative actual = servlet.parseRepresentative(rep_entity); 
    Assert.assertTrue(actual.equal(donald_trump));
  }
}
