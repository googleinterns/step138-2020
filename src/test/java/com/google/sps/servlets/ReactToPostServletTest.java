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
import com.google.sps.servlets.ReactToPostServlet;
import java.io.StringWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
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
public class ReactToPostServletTest{
    private ReactToPostServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private LocalServiceTestHelper helper;
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new ReactToPostServlet();
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
        long questionId = DatastoreManager.insertCommentInDatastore("Bob", "Why are you president?"); 
        Long postId = DatastoreManager.insertPostInDatastore(questionId, Arrays.asList("education")); 
        when(request.getParameter("postId")).thenReturn(postId.toString());
        when(request.getParameter("reaction")).thenReturn(Reaction.THUMBS_UP.toString());
        when(request.getParameter("repName")).thenReturn("Donald Trump");

        servlet.doGet(request, response);
    
        Entity postEntity = DatastoreManager.queryForPostEntityWithId(postId); 
        long reactionCount = (long) postEntity.getProperty(Reaction.THUMBS_UP.getValue()); 
        assertTrue(reactionCount == 1); 
    }
}
