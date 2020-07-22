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
import com.google.sps.servlets.InsertRepDatastoreServlet;
import java.io.StringWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(JUnit4.class)
public class InsertRepDatastoreServletTest{
    private InsertRepDatastoreServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private LocalServiceTestHelper helper;
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new InsertRepDatastoreServlet();
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
    public void repNotInDatastore() throws Exception {
        when(request.getParameter("username")).thenReturn("username");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getParameter("repName")).thenReturn("Donald J. Trump");
        when(request.getParameter("title")).thenReturn("President of the United States");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        Representative rep = DatastoreManager.queryForRepresentativeObjectWithName("Donald J. Trump");
        assertTrue(rep.getName().equals("Donald J. Trump"));
        assertTrue(rep.getTitle().equals("President of the United States"));
        assertTrue(rep.getUsername().equals("username"));
        assertTrue(rep.getPassword().equals("password"));
        assertTrue(stringWriter.toString().contains("false"));
    }
}
