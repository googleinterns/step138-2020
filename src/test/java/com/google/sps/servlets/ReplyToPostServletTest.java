package com.google.sps.data;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.DatastoreManager;
import com.google.sps.servlets.ReplyToPostServlet;
import java.io.*;
import javax.servlet.http.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;


public class ReplyToPostServletTest{
    private ReplyToPostServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new ReplyToPostServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        helper.setUp();
        ds = DatastoreServiceFactory.getDatastoreService();
    }

    @Test
    public void testDoPost() throws Exception {
        // add Representative entity to Datastore 
        long questionId = DatastoreManager.insertCommentInDatastore("Bob", "Why are you president?"); 
        Long postId = DatastoreManager.insertPostInDatastore(questionId); 
        when(request.getParameter("postId")).thenReturn(postId.toString());
        when(request.getParameter("name")).thenReturn("Alice");
        when(request.getParameter("reply")).thenReturn("Yeah bro, why are you?");
        when(request.getParameter("repName")).thenReturn("Donald Trump");
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", "President");
        DatastoreManager.updateRepresentativePostList(repId, postId); 
        
        // make Representative object for comparison purposes 
        Comment question = new Comment("Bob", "Why are you president?", questionId); 
        Comment reply = new Comment("Alice", "Yeah bro, why are you?", -1); 
        List<Comment> replies = new ArrayList<>(); 
        replies.add(reply); 
        List<Post> posts = new ArrayList<>(); 
        Post post = new Post(question, null, replies, postId); 
        posts.add(post); 
        Representative expectedRep = new Representative("Donald Trump", "President", posts, repId);

        servlet.doPost(request, response);
    
        Representative actualRep = DatastoreManager.queryForRepresentativeObjectWithName("Donald Trump");
        Assert.assertTrue(actualRep.equals(expectedRep)); 
    }
}
