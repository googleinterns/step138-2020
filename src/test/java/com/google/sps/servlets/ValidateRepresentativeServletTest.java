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
import com.google.sps.servlets.ValidateRepresentativeServlet;
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
import org.mockito.ArgumentCaptor;  
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(JUnit4.class)
public class ValidateRepresentativeServletTest{
    private ValidateRepresentativeServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private LocalServiceTestHelper helper;
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new ValidateRepresentativeServlet();
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
    public void testLoginInformationCorrect() throws Exception {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", 
        "President of the U.S.", "username", "password",
        new ArrayList<Long> (Arrays.asList(Long.valueOf(1))));
        when(request.getParameter("username")).thenReturn("username");
        when(request.getParameter("password")).thenReturn("password");

        servlet.doPost(request, response);

        verify(response).sendRedirect(captor.capture());
        assertTrue(("feed.html?name=Donald+Trump").equals(captor.getValue()));    
    }

    @Test
    public void testLoginInformationIncorrect() throws Exception {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        List<Long> tabIds = DatastoreManager.insertTabsInDatastore(
                new ArrayList<String> (Arrays.asList("Other")), 
                new ArrayList<String> (Arrays.asList("")));
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", 
        "President of the U.S.", "username", "password", tabIds);
        when(request.getParameter("username")).thenReturn("user");
        when(request.getParameter("password")).thenReturn("password");

        servlet.doPost(request, response);

        verify(response).sendRedirect(captor.capture());
        assertTrue(("/errors/invalidAuthRep.html").equals(captor.getValue()));
    }
}
