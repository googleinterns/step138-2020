package com.google.sps.data;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.DatastoreManager;
import com.google.sps.servlets.RepAnswerServlet;
import java.io.*;
import javax.servlet.http.*;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;


public class RepAnswerServletTest{
    private RepAnswerServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new RepAnswerServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        helper.setUp();
        ds = DatastoreServiceFactory.getDatastoreService();
    }

    @Test
    public void testRepAnswerPostFound() throws Exception {
        long questionId = DatastoreManager.insertCommentInDatastore("Bob", "How are you doing?");
        long postId = DatastoreManager.insertPostInDatastore(questionId);
        String postID = String.valueOf(postId);
        when(request.getParameter("postId")).thenReturn(postID);
        when(request.getParameter("repName")).thenReturn("Donald Trump");
        when(request.getParameter("answer")).thenReturn("I am well");
    
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);
    
        Post post = DatastoreManager.queryForPostObjectWithId(postId);
        Comment answer = post.getAnswer();
        Assert.assertTrue(answer.getDisplayName().equals("Donald Trump"));
        Assert.assertTrue(answer.getComment().equals("I am well"));
    }
}
