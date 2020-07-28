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
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.DatastoreManager;
import com.google.sps.servlets.GetRepresentativeObjectServlet;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(JUnit4.class)
public class GetRepresentativeObjectServletTest{
    private GetRepresentativeObjectServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private LocalServiceTestHelper helper;
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new GetRepresentativeObjectServlet();
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
    public void testDoGet() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(request.getParameter("repName")).thenReturn("Donald Trump");
        when(response.getWriter()).thenReturn(writer);
        List<Long> tabIds = DatastoreManager.insertTabsInDatastore(
            Arrays.asList("Other"), Arrays.asList(""));
        DatastoreManager.insertRepresentativeInDatastore("Donald Trump", 
        "President", "username", "password", tabIds);

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
