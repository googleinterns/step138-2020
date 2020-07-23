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
import com.google.sps.servlets.RepAnswerServlet;
import java.io.StringWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(JUnit4.class)
public class RepAnswerServletTest{
    private RepAnswerServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private LocalServiceTestHelper helper;
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new RepAnswerServlet();
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
    public void testRepAnswerPostFound() throws Exception {
        long questionId = DatastoreManager.insertCommentInDatastore("Bob", "How are you doing?");
        long postId = DatastoreManager.insertPostInDatastore(questionId, Arrays.asList("Education"));
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
        assertTrue(answer.getDisplayName().equals("Donald Trump"));
        assertTrue(answer.getComment().equals("I am well"));
    }
}
