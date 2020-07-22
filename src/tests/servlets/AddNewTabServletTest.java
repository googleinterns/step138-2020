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
import com.google.sps.servlets.AddNewTabServlet;
import java.io.StringWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(JUnit4.class)
public class AddNewTabServletTest{
    private AddNewTabServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private LocalServiceTestHelper helper;
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new AddNewTabServlet();
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
    public void testAddTab() throws Exception {
        long questionId = DatastoreManager.insertCommentInDatastore("Anonymous", 
            "What are you doing about schools?"); 
        long postId = DatastoreManager.insertPostInDatastore(questionId, Arrays.asList("Education"));
        when(request.getParameter("tabName")).thenReturn("Schools");
        when(request.getParameter("platform")).thenReturn("platform");
        when(request.getParameter("posts")).thenReturn(Long.toString(postId));
        when(request.getParameter("repName")).thenReturn("Donald Trump");
        List<Long> tabIds = DatastoreManager.insertTabsInDatastore(Arrays.asList("Education"), Arrays.asList("Platform on education"));
        Long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", "President", 
            "username", "password", tabIds);

        servlet.doGet(request, response);
        Post post = DatastoreManager.queryForPostObjectWithId(postId);
        Representative rep = DatastoreManager.queryForRepresentativeObjectWithName("Donald Trump");
        Entity tab = DatastoreManager.queryForTabEntityWithName("DonaldTrumpSchools");

        assertTrue(post.getTab().equals("DonaldTrumpSchools"));
        assertTrue(rep.getTabs().get(1).getTabName().equals("DonaldTrumpSchools"));
        assertTrue(tab != null);
    }
}
