package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory; 
import com.google.gson.Gson;
import com.google.sps.data.Comment;
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
        String rep_name = request.getParameter("rep_name");
        Query query = new Query(REP_ENTITY_TYPE); 

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        Entity rep_entity = queryForRepresentative(results, rep_name); 
        Representative rep = parseRepresentative(rep_entity); 

        response.setContentType("application/json;");
        response.getWriter().println(new Gson().toJson(rep));
    }

    private Entity queryForRepresentative(PreparedQuery results, String rep_name) {
        for (Entity entity : results.asIterable()) {
            String name = (String) entity.getProperty(REP_NAME);
            if (name == rep_name) {
                return entity; 
            }
        }
        return null; 
    }

    private Representative parseRepresentative(Entity entity) {
        String name = (String) entity.getProperty(REP_NAME);
        String title = (String) entity.getProperty(REP_TITLE);
        List<Post> posts = parsePosts(entity); 
        long id = entity.getKey().getId();
        return new Representative(name, title, posts, id);   
    }

    private List<Post> parsePosts(Entity rep_entity) {
        ArrayList<long> post_ids = (ArrayList<long>) rep_entity.getProperty(REP_POSTS); 
        List<Post> posts = new ArrayList<>(); 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        for (long post_id : post_ids) {
            Key post_entity_key = KeyFactory.createKey(POST_ENTITY_TYPE, post_id);
            Entity post_entity = (Entity) datastore.get(post_entity_key); 
            Post post = parsePost(post_entity); 
            posts.add(post); 
        }
        return posts; 
    }

    private Post parsePost(Entity post_entity) {
        String question = (String) post_entity.getProperty(POST_QUESTION);
        String answer = (String) post_entity.getProperty(POST_ANSWER);
        List<Comment> comments = parseComments(post_entity); 
        long id = post_entity.getKey().getId();
        return new Post(question, answer, comments, id); 
    }

    private List<Comment> parseComments(Entity post_entity) {
        ArrayList<long> comment_ids = (ArrayList<long>) post_entity.getProperty(POST_REPLIES); 
        List<Comment> comments = new ArrayList<>(); 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        for (long comment_id : comment_ids) {
            Key comment_entity_key = KeyFactory.createKey(COMMENT_ENTITY_TYPE, comment_id);
            Entity comment_entity = (Entity) datastore.get(comment_entity_key); 
            Comment comment = parseComment(comment_entity); 
            comments.add(comment); 
        }
        return comments; 
    }

    private Comment parseComment(Entity comment_entity) {
        String name = (String) comment_entity.getProperty(COMMENT_NAME);
        String msg = (String) comment_entity.getProperty(COMMENT_MSG);
        long id = comment_entity.getKey().getId();
        return new Comment(name, msg, id); 
    }
}
