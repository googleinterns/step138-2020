package com.google.sps.data;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.DatastoreManager;
import com.google.sps.servlets.NewPostServlet;
import java.io.*;
import javax.servlet.http.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;


public class NewPostServletTest{
    private NewPostServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new NewPostServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        helper.setUp();
        ds = DatastoreServiceFactory.getDatastoreService();
    }

    @Test
    public void newPost() throws Exception {
        when(request.getParameter("repName")).thenReturn("Donald Trump");
        when(request.getParameter("name")).thenReturn("Bob");
        when(request.getParameter("comment")).thenReturn("Why are you president?");
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", "President");

        servlet.doPost(request, response);
    
        Representative rep = DatastoreManager.queryForRepresentativeObjectWithName("Donald Trump");
        List<Post> posts = rep.getPosts();
        Assert.assertTrue(posts.get(0).getQuestion().getDisplayName().equals("Bob"));
        Assert.assertTrue(posts.get(0).getQuestion().getComment().equals("Why are you president?"));
    }
}
