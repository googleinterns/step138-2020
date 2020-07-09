package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.data.Comment;
import com.google.sps.data.DatastoreManager;
import com.google.sps.data.Representative;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
The NewPostServlet class inserts a comment entity and a post entity into datastore 
and updates the list of posts associated with a representative when a user enters a 
question on a representative's feed
*/
@WebServlet ("/new_post")
public class NewPostServlet extends HttpServlet{    
    private static final Logger logger = LogManager.getLogger("NewPostServlet");
    private static final String REP_NAME = "repName";
    private static final String NAME = "name";
    private static final String COMMENT = "comment";
    private static final String TAB = "tab";
    private static final String FEED_BOOLEAN = "feed";

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException{
        String repName = request.getParameter(REP_NAME);
        String name = request.getParameter(NAME);
        String comment = request.getParameter(COMMENT);
        Boolean feed = Boolean.parseBoolean(request.getParameter(FEED_BOOLEAN));
        String tab = repName.replaceAll("\\s+","") + request.getParameter(TAB);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity repEntity;
        try {
            repEntity = DatastoreManager.queryForRepresentativeEntityWithName(repName);         
        } catch(EntityNotFoundException e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
        long repId = repEntity.getKey().getId();
        long commentId = DatastoreManager.insertCommentInDatastore(name, comment);
        long postId = DatastoreManager.insertPostInDatastore(commentId, tab);
        try {
            DatastoreManager.updateRepresentativePostList(repId, postId);
        } 
        catch(EntityNotFoundException e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
        String redirect = (feed == true) ? "feed.html?name=" + repName : "tab.html?name=" + repName + "&tab=" + tab;
        response.sendRedirect(redirect);
    }
}
