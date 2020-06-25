package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.InsertAndUpdate;
import com.google.sps.data.QueryDatastore;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory;

@WebServlet ("/rep_answer")
public class RepAnswerServlet extends HttpServlet{
    private static final String POST_ENTITY_TYPE = "Post";
    private static final String POST_QUESTION = "Question";
    private static final String POST_ANSWER = "Answer";
    private static final String POST_REPLIES = "Replies";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        long postId = Long.parseLong(request.getParameter("postId"));
        String repName = request.getParameter("repName");
        String answer = request.getParameter("answer");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        long commentId = InsertAndUpdate.insertCommentDatastore(repName, answer);
        Entity post = null;
        try {
            post = QueryDatastore.queryForPost(postId);
        } 
        catch(EntityNotFoundException e) {
            System.out.println("Unable to query for post"); 
            System.exit(0);
        }
        post.setProperty(POST_ANSWER, commentId);
    }
}