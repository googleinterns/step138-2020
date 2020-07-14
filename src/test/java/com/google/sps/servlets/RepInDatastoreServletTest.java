package com.google.sps.data;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.sps.data.DatastoreManager;
import com.google.sps.servlets.RepInDatastoreServlet;
import java.io.StringWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(JUnit4.class)
public class RepInDatastoreServletTest{
    private RepInDatastoreServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private LocalServiceTestHelper helper;
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new RepInDatastoreServlet();
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
    public void testRepInDatastore() throws Exception {
        List<Long> tabIds = DatastoreManager.insertTabsInDatastore(
                new ArrayList<String> (Arrays.asList("Other")), 
                new ArrayList<String> (Arrays.asList("")));
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", 
        "President of the U.S.", "username", "password", tabIds);
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
