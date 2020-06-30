package com.google.sps.servlets;

import com.google.sps.data.Constants;
import com.google.sps.data.InsertAndUpdate;
import com.google.sps.data.QueryDatastore;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

/**
The RepAnswerServlet class inserts a comment entity into datastore and updates
the answer property of the post that the representative is responding to
*/

@WebServlet ("/rep_answer")
public class RepAnswerServlet extends HttpServlet{

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
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
            Constants.logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
        post.setProperty(Constants.POST_ANSWER, commentId);
        response.sendRedirect("feed.html");
    }
}