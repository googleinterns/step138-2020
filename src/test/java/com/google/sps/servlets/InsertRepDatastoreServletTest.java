package com.google.sps.data;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.DatastoreManager;
import com.google.sps.servlets.InsertRepDatastoreServlet;
import java.io.*;
import javax.servlet.http.*;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;


public class InsertRepDatastoreServletTest{
    private InsertRepDatastoreServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private DatastoreService ds; 

    @Before
    public void setUp() {
        servlet = new InsertRepDatastoreServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        helper.setUp();
        ds = DatastoreServiceFactory.getDatastoreService();
    }

    @Test
    public void repNotInDatastore() throws Exception {
        servlet.doGet(request, response);
        Representative rep = DatastoreManager.queryForRepresentativeObjectWithName("Donald J. Trump");
        
        Assert.assertTrue(rep.getName().equals("Donald J. Trump"));
        Assert.assertTrue(rep.getTitle().equals("President of the United States"));
    }
}
