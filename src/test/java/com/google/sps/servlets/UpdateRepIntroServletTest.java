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
import com.google.sps.servlets.UpdateRepIntroServlet;
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
public class UpdateRepIntroServletTest{
    private UpdateRepIntroServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private LocalServiceTestHelper helper;
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new UpdateRepIntroServlet();
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
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", 
        "President", "username", "password", new ArrayList());
        DatastoreManager.updateRepresentativeIntro(repId, "Original Intro");
        when(request.getParameter("repName")).thenReturn("Donald Trump");
        when(request.getParameter("intro")).thenReturn("New intro");

        servlet.doGet(request, response);
        Entity repEntity = DatastoreManager.queryForRepresentativeEntityWithName("Donald Trump");

        assertTrue(repEntity.getProperty(Constants.REP_INTRO).equals("New intro"));
    }
}
