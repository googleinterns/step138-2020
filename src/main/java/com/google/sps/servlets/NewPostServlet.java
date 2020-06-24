package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.data.Parse;
import com.google.sps.data.Comment;
import com.google.sps.data.Representative;
import com.google.appengine.api.datastore.EntityNotFoundException;


@WebServlet ("/new_post")
public class NewPostServlet extends HttpServlet{    

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String rep_name = request.getParameter("rep_name");
        String name = request.getParameter("name");
        String comment = request.getParameter("comment");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity rep_entity = Parse.queryForRepresentative(rep_name); 
        Representative rep = null; 
        try {
            rep = Parse.parseRepresentative(rep_entity); 
        } 
        catch(EntityNotFoundException e) {
            System.out.println("Unable to parse representative from datastore"); 
            System.exit(0);
        }
        long repId = rep.getID();
        //Insert comment and pull commentId
        long commentId = Parse.insertCommentDatastore(name, comment);

        //Insert post and pull postId
        long postId = Parse.insertPostDatastore(commentId);

        //Add the post to the representative's post list 
        try {
            Parse.updateRepresentativePostList(postId, repId);
        } 
        catch(EntityNotFoundException e) {
            System.out.println("Unable to update representative post list"); 
            System.exit(0);
        }
    }
}
