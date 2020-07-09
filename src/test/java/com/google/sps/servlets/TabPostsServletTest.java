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
import com.google.sps.servlets.TabPostsServlet;
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
public class TabPostsServletTest{
    private TabPostsServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private LocalServiceTestHelper helper;
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new TabPostsServlet();
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
    public void testTabHasPosts() throws Exception {
        List<Long> tabId = DatastoreManager.insertTabsInDatastore(new ArrayList<String> (Arrays.asList("Education")), 
            new ArrayList<String> (Arrays.asList("Platform")));
        when(request.getParameter("tab")).thenReturn("Education");
        when(request.getParameter("repName")).thenReturn("Donald Trump");
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", "President", "username", "password");
        long questionId = DatastoreManager.insertCommentInDatastore("Bob", "Comment");
        long postId = DatastoreManager.insertPostInDatastore(questionId, "Education");
        DatastoreManager.updateRepresentativePostList(repId, postId);


        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);
        
        assertTrue(stringWriter.toString().contains("Bob"));
        assertTrue(stringWriter.toString().contains("Comment"));
        assertTrue(stringWriter.toString().contains("Education"));
    }
}
