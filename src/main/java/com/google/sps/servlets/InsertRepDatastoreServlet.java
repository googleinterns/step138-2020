package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory; 
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import com.google.sps.data.Constants;
import com.google.sps.data.DatastoreManager;
import com.google.sps.data.Post;
import com.google.sps.data.Representative;
import com.google.sps.data.DatastoreManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** 
InsertRepDatastoreServlet currently inserts the representative Donald
Trump into the datastore as a hard code for the MVP.
*/

@WebServlet ("/insert_rep_datastore")
public class InsertRepDatastoreServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger("InsertRepDatastoreServlet");
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String REP_NAME = "repName";
    private static final String TITLE = "title";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
        String username = request.getParameter(USERNAME);
        String password = request.getParameter(PASSWORD);
        String repName = request.getParameter(REP_NAME);
        String title = request.getParameter(TITLE);
        Entity rep;
        try {
            rep = DatastoreManager.queryForRepresentativeUsername(username.trim()); 
        } 
        catch(EntityNotFoundException e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
        if (rep == null){
            DatastoreManager.insertRepresentativeInDatastore(repName, title, username, password);   
        }
        response.setContentType("text/html");
        response.getWriter().println(Boolean.toString(rep != null));
    }
}
