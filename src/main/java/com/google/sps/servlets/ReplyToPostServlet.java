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


@WebServlet ("/reply_to_post")
public class ReplyToPostServlet extends HttpServlet{ 
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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        long post_id = Long.parseLong(request.getParameter("post_id"));
        String nick_name = request.getParameter("nick_name");
        String comment = request.getParameter("comment");
        Entity post_entity = null; 
        try {
            post_entity = Parse.queryForPost(post_id); 
        } 
        catch(EntityNotFoundException e) {
            System.out.println("Unable to query for post in datastore"); 
            System.exit(0);
        }
        long comment_id = Parse.insertCommentDatastore(nick_name, comment); 
        List<Long> comment_ids = (ArrayList<Long>) post_entity.getProperty(POST_REPLIES);  
        comment_ids.add(comment_id); 
        post_entity.setProperty(POST_REPLIES, comment_ids); 
    }
}
