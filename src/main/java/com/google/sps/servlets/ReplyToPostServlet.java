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
        String repName = request.getParameter("repName");
        long commentId = DatastoreManager.insertCommentInDatastore(nickName, comment); 
        try {
            DatastoreManager.updatePostWithComment(postId, commentId); 
        } 
        catch(EntityNotFoundException e) {
            return; 
        }
        String redirect = "feed.html?name=" + repName;
        response.sendRedirect(redirect);
    }
}
