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
import com.google.sps.data.InsertAndUpdate;
import com.google.sps.data.QueryDatastore;
import com.google.sps.data.Post;
import com.google.sps.data.Representative;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet ("/reply_to_post")
public class ReplyToPostServlet extends HttpServlet { 
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        long postId = Long.parseLong(request.getParameter("postId"));
        String nickName = request.getParameter("name");
        String comment = request.getParameter("reply");

        Entity postEntity = null; 
        try {
            postEntity = QueryDatastore.queryForPost(postId); 
        } 
        catch(EntityNotFoundException e) {
            System.out.println("Unable to query for post in datastore"); 
            System.exit(0);
        }
        long commentId = InsertAndUpdate.insertCommentDatastore(nickName, comment); 
        List<Long> commentIds = (ArrayList<Long>) postEntity.getProperty(Constants.POST_REPLIES);  
        commentIds.add(commentId); 
        postEntity.setProperty(Constants.POST_REPLIES, commentIds); 
    }
}
