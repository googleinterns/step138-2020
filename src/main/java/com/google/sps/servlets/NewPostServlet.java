package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.data.Comment;
import com.google.sps.data.Constants;
import com.google.sps.data.InsertAndUpdate;
import com.google.sps.data.Parse;
import com.google.sps.data.QueryDatastore;
import com.google.sps.data.Representative;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

/**
The NewPostServlet class inserts a comment entity and a post entity into datastore 
and updates the list of posts associated with a representative when a user enters a 
question on a representative's feed
*/

@WebServlet ("/new_post")
public class NewPostServlet extends HttpServlet{    

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        String repName = request.getParameter("repName");
        String name = request.getParameter("name");
        String comment = request.getParameter("comment");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity repEntity = QueryDatastore.queryForRepresentative(repName); 
        Representative rep = null; 
        try {
            rep = Parse.parseRepresentative(repEntity); 
        } 
        catch(EntityNotFoundException e) {
            Constants.logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
            
        }
        long repId = rep.getID();
        long commentId = InsertAndUpdate.insertCommentDatastore(name, comment);
        long postId = InsertAndUpdate.insertPostDatastore(commentId);
        try {
            InsertAndUpdate.updateRepresentativePostList(postId, repId);
        } 
        catch(EntityNotFoundException e) {
            Constants.logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
        
        response.sendRedirect("feed.html");
    }
}
