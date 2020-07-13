package com.google.sps.servlets;

import com.google.sps.data.Constants;
import com.google.sps.data.DatastoreManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.io.IOException;
import java.net.URLEncoder; 
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
The RepAnswerServlet class inserts a comment entity into datastore and updates
the answer property of the post that the representative is responding to
*/
@WebServlet ("/rep_answer")
public class RepAnswerServlet extends HttpServlet{
    private static final Logger logger = LogManager.getLogger("RepAnswerServlet");
    private static final String POST_ID = "postId";
    private static final String REP_NAME = "repName";
    private static final String ANSWER = "answer";

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException{
        long postId = Long.parseLong(request.getParameter(POST_ID));
        String repName = request.getParameter(REP_NAME);
        String answer = request.getParameter(ANSWER);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        long commentId = DatastoreManager.insertCommentInDatastore(repName, answer);
        Entity post;
        try {
            post = DatastoreManager.queryForPostEntityWithId(postId);
        } 
        catch(EntityNotFoundException e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
        post.setProperty(Constants.POST_ANSWER, commentId);
        datastore.put(post);
        String redirect = "feed.html?name=" + URLEncoder.encode(repName);
        response.sendRedirect(redirect);
    }
}
