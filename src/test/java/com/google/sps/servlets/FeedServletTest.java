package com.google.sps.data;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.DatastoreManager;
import com.google.sps.servlets.FeedServlet;
import java.io.*;
import javax.servlet.http.*;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;


public class FeedServletTest{
    private FeedServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new FeedServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        helper.setUp();
        ds = DatastoreServiceFactory.getDatastoreService();
    }

    @Test
    public void testDoGet() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(request.getParameter("repName")).thenReturn("Donald Trump");
        when(response.getWriter()).thenReturn(writer);
        DatastoreManager.insertRepresentativeInDatastore("Donald Trump", "President");

        servlet.doGet(request, response);
    
        verify(request, atLeast(1)).getParameter("repName");
        writer.flush(); 
        assertTrue(stringWriter.toString().contains("name"));
        assertTrue(stringWriter.toString().contains("Donald Trump"));
        assertTrue(stringWriter.toString().contains("title"));
        assertTrue(stringWriter.toString().contains("President"));
        assertTrue(stringWriter.toString().contains("posts"));
        assertTrue(stringWriter.toString().contains("id"));
    }
}
