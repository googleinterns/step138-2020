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
import com.google.sps.data.DatastoreManager;
import com.google.sps.data.Post;
import com.google.sps.data.Representative;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder; 
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
 * Servlet for fetching a representative object used to create the feed page 
 * for a particular representative 
 */ 
@WebServlet ("/validate_rep")
public class ValidateRepresentativeServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger("ValidateRepresentativeServlet");
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
        String username = request.getParameter(USERNAME);
        String password = request.getParameter(PASSWORD);
        String repName;
        try {
            repName = DatastoreManager.queryForRepresentativeNameWithLogin(
                username.trim(), password); 
        } catch(EntityNotFoundException e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
        String redirect =  (repName != null) ?  "feed.html?name=" + URLEncoder.encode(repName) : "/errors/invalidAuthRep.html";
        response.sendRedirect(redirect);
    }
}
