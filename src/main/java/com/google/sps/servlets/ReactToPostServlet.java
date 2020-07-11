package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory; 
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import com.google.sps.data.Constants;
import com.google.sps.data.DatastoreManager;
import com.google.sps.data.Post;
import com.google.sps.data.Representative;
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
 * Servlet for reacting to a post in datastore 
 */ 
@WebServlet ("/react_to_post")
public class ReactToPostServlet extends HttpServlet { 
    private static final Logger logger = LogManager.getLogger("ReactToPostServlet");
    private static final String POST_ID = "postId";
    private static final String REACTION = "reaction";
    private static final String REP_NAME = "repName";
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException{
        long postId = Long.parseLong(request.getParameter(POST_ID));
        String repName = request.getParameter(REP_NAME);
        String reaction = request.getParameter(REACTION);
       
        try {
            DatastoreManager.addReactionToPost(postId, reaction); 
        } 
        catch(EntityNotFoundException e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
        String redirect = "feed.html?name=" + repName;
        response.sendRedirect(redirect);
    }
}
