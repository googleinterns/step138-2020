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
import com.google.sps.data.InsertAndUpdate;
import com.google.sps.data.Parse;
import com.google.sps.data.QueryDatastore;
import com.google.sps.data.Representative;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet ("/new_post")
public class NewPostServlet extends HttpServlet{    

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
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
            System.out.println("Unable to parse representative from datastore"); 
            System.exit(0);
        }
        long repId = rep.getID();
        //Insert comment and pull commentId
        long commentId = InsertAndUpdate.insertCommentDatastore(name, comment);

        //Insert post and pull postId
        long postId = InsertAndUpdate.insertPostDatastore(commentId);

        //Add the post to the representative's post list 
        try {
            InsertAndUpdate.updateRepresentativePostList(postId, repId);
        } 
        catch(EntityNotFoundException e) {
            System.out.println("Unable to update representative post list"); 
            System.exit(0);
        }

        response.sendRedirect("feed.html");
    }
}
