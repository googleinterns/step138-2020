package com.google.sps.data;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.sps.data.DatastoreManager;
import com.google.sps.servlets.RepInDatastoreServlet;
import java.io.*;
import javax.servlet.http.*;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;

public class RepInDatastoreServletTest{
    private RepInDatastoreServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new RepInDatastoreServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        helper.setUp();
        ds = DatastoreServiceFactory.getDatastoreService();
    }

    @Test
    public void testRepInDatastore() throws Exception {
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", "President of the U.S.");
        when(request.getParameter("repName")).thenReturn("Donald Trump");
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        // Verify repName was called...
        verify(request, atLeast(1)).getParameter("repName"); 
        assertTrue(stringWriter.toString().contains("true"));
    }

    @Test
    public void testRepNotInDatastore() throws Exception {
        when(request.getParameter("repName")).thenReturn("Mike Pence");
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        verify(request, atLeast(1)).getParameter("repName"); 
        assertTrue(stringWriter.toString().contains("false"));
    }
}
