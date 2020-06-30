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

/* 
The NewPostServlet class inserts a comment entity and a post entity into datastore 
and updates the list of posts associated with a representative when a user enters a 
question on a representative's feed
*/

@WebServlet ("/new_post")
public class NewPostServlet extends HttpServlet {    

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        String repName = request.getParameter("repName");
        String name = request.getParameter("name");
        String comment = request.getParameter("comment");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity rep = null;
        try {
            rep = DatastoreManager.queryForRepresentativeEntityWithName(repName); 
        } 
        catch(EntityNotFoundException e) {
            System.out.println("Cannot find representative"); 
            e.printStackTrace(); 
            throw new ServletException("Error: " + e.getMessage(), e);
        }
        System.out.println("Rep before: " + rep);
        long repId = rep.getKey().getId();

        //Insert comment and pull commentId
        long commentId = DatastoreManager.insertCommentInDatastore(name, comment);
        System.out.println("This is the comment id: " + commentId);

        //Insert post and pull postId
        long postId = DatastoreManager.insertPostInDatastore(commentId);
        System.out.println("This is the post id: " + postId);

        //Add the post to the representative's post list 
        try {
            DatastoreManager.updateRepresentativePostList(repId, postId);
        } 
        catch(EntityNotFoundException e) {
            System.out.println("Unable to update representative post list"); 
            e.printStackTrace(); 
            throw new ServletException("Error: " + e.getMessage(), e);
        }
        String redirect = "feed.html?name=" + repName;
        response.sendRedirect(redirect);
    }
}
