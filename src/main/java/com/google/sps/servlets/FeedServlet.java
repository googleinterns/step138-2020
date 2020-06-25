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
import com.google.sps.data.Parse;
import com.google.sps.data.Post;
import com.google.sps.data.Representative;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet ("/feed")
public class FeedServlet extends HttpServlet{
    private static final String REP_ENTITY_TYPE = "Representative";
    private static final String REP_NAME = "Name";
    private static final String REP_TITLE = "Official Title";
    private static final String REP_POSTS = "Posts";

    private static final String POST_ENTITY_TYPE = "Post";
    private static final String POST_QUESTION = "Question";
    private static final String POST_ANSWER = "Answer";
    private static final String POST_REPLIES = "Replies";

    private static final String COMMENT_ENTITY_TYPE = "Comment";
    private static final String COMMENT_NAME = "Nick Name";
    private static final String COMMENT_MSG = "Message";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long rep_id = Long.parseLong(request.getParameter("rep_id"));
        Entity rep_entity = null; 
        try {
            rep_entity = Parse.queryForRepresentative(rep_id); 
        } 
        catch(EntityNotFoundException e) {
            System.out.println("Unable to query representative from datastore"); 
            System.exit(0);
        }
        Representative rep = null; 
        try {
            rep = Parse.parseRepresentative(rep_entity); 
        } 
        catch(EntityNotFoundException e) {
            System.out.println("Unable to parse representative from datastore"); 
            System.exit(0);
        }
        response.setContentType("application/json;");
        response.getWriter().println(new Gson().toJson(rep));
    }
}
