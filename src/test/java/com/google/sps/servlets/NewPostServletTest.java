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
import com.google.sps.servlets.NewPostServlet;
import java.io.StringWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(JUnit4.class)
public class NewPostServletTest{
    private NewPostServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private LocalServiceTestHelper helper;
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new NewPostServlet();
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
    public void newPost() throws Exception {
        when(request.getParameter("repName")).thenReturn("Donald Trump");
        when(request.getParameter("name")).thenReturn("Bob");
        when(request.getParameter("comment")).thenReturn("Why are you president?");
        List<Long> tabIds = DatastoreManager.insertTabsInDatastore(
            Arrays.asList("Other"), Arrays.asList(""));
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", 
        "President", "username", "password", tabIds);

        servlet.doPost(request, response);
    
        Representative rep = DatastoreManager.queryForRepresentativeObjectWithName("Donald Trump");
        List<Post> posts = rep.getPosts();
        assertTrue(posts.get(0).getQuestion().getDisplayName().equals("Bob"));
        assertTrue(posts.get(0).getQuestion().getComment().equals("Why are you president?"));
    }
}
