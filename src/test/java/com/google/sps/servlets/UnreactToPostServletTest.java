package com.google.sps.data;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.DatastoreManager;
import com.google.sps.servlets.UnreactToPostServlet;
import java.io.StringWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(JUnit4.class)
public class UnreactToPostServletTest{
    private UnreactToPostServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private LocalServiceTestHelper helper;
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new UnreactToPostServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();
        ds = DatastoreServiceFactory.getDatastoreService();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testDoPost() throws Exception {
        // add Representative entity to Datastore 
        long questionId = DatastoreManager.insertCommentInDatastore("Bob", "Why are you president?"); 
        Long postId = DatastoreManager.insertPostInDatastore(questionId); 
        when(request.getParameter("postId")).thenReturn(postId.toString());
        when(request.getParameter("reaction")).thenReturn(Reaction.THUMBS_UP.toString());
        when(request.getParameter("repName")).thenReturn("Donald Trump");
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", "President");
        DatastoreManager.updateRepresentativePostList(repId, postId); 
        DatastoreManager.addReactionToPost(postId, Reaction.THUMBS_UP.toString()); 
        DatastoreManager.addReactionToPost(postId, Reaction.THUMBS_UP.toString()); 
        
        // make Representative object for comparison purposes 
        Comment question = new Comment("Bob", "Why are you president?", questionId); 
        List<Comment> replies = new ArrayList<>(); 
        List<Post> posts = new ArrayList<>(); 
        Map<Reaction, Long> reactions = new HashMap<Reaction, Long>(); 
        reactions.put(Reaction.THUMBS_UP, (long) 1);
        Post post = new Post(question, null, replies, postId, reactions); 
        posts.add(post); 
        Representative expectedRep = new Representative("Donald Trump", "President", posts, repId);

        servlet.doGet(request, response);
    
        Representative actualRep = DatastoreManager.queryForRepresentativeObjectWithName("Donald Trump");
        assertTrue(actualRep.equals(expectedRep)); 
    }
}
