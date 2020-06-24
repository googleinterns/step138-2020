package com.google.sps.data;

import java.util.List;
import java.util.ArrayList;
import com.google.sps.data.Comment;
import com.google.sps.data.Post;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory;

public class Parse{

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

    public static Entity queryForRepresentative(String rep_name) {
        Query query = new Query(REP_ENTITY_TYPE); 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity : results.asIterable()) {
            String name = (String) entity.getProperty(REP_NAME);
            if (name == rep_name) {
                return entity; 
            }
        }
        return null; 
    }

    public static Representative parseRepresentative(Entity entity) throws EntityNotFoundException{
        String name = (String) entity.getProperty(REP_NAME);
        String title = (String) entity.getProperty(REP_TITLE);
        List<Post> posts = parsePosts(entity); 
        long id = entity.getKey().getId();
        return new Representative(name, title, posts, id);   
    }

    public static List<Post> parsePosts(Entity rep_entity) throws EntityNotFoundException{
        List<Long> post_ids = (ArrayList<Long>) rep_entity.getProperty(REP_POSTS); 
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

    public static Post parsePost(Entity post_entity) throws EntityNotFoundException{
        long questionId = (long) post_entity.getProperty(POST_QUESTION);
        Comment question = Parse.parseComment(questionId);
        long answerId = (long) post_entity.getProperty(POST_ANSWER);
        Comment answer = Parse.parseComment(answerId);
        List<Comment> comments = Parse.parseComments(post_entity); 
        long id = post_entity.getKey().getId();
        return new Post(question, answer, comments, id); 
    }

    private static List<Comment> parseComments(Entity post_entity) throws EntityNotFoundException{
        List<Long> comment_ids = (ArrayList<Long>) post_entity.getProperty(POST_REPLIES); 
        List<Comment> comments = new ArrayList<>(); 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        for (long comment_id : comment_ids) {
            Comment comment = parseComment(comment_id); 
            comments.add(comment); 
        }
        return comments; 
    }

    private static Comment parseComment(long comment_id) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key comment_entity_key = KeyFactory.createKey(COMMENT_ENTITY_TYPE, comment_id);
        Entity comment_entity = (Entity) datastore.get(comment_entity_key);
        String name = (String) comment_entity.getProperty(COMMENT_NAME);
        String msg = (String) comment_entity.getProperty(COMMENT_MSG);
        long id = comment_entity.getKey().getId();
        return new Comment(name, msg, id); 
    }

    public static long insertCommentDatastore(String name, String message) {
        Entity comment_entity = new Entity(COMMENT_ENTITY_TYPE); 
        comment_entity.setProperty(COMMENT_MSG, message); 
        comment_entity.setProperty(COMMENT_NAME, name); 
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(comment_entity); 
        return comment_entity.getKey().getId(); 
    }

    public static Entity queryForPost(long post_id) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key post_entity_key = KeyFactory.createKey(POST_ENTITY_TYPE, post_id);
        Entity post_entity = (Entity) datastore.get(post_entity_key); 
        return post_entity; 
    }

    public static long insertPostDatastore(long question) {
        Entity post_entity = new Entity(COMMENT_ENTITY_TYPE); 
        post_entity.setProperty(POST_QUESTION, question); 
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(post_entity); 
        return post_entity.getKey().getId(); 
    }

    public static void updateRepresentativePostList(long rep_id, long post_id) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key rep_entity_key = KeyFactory.createKey(REP_ENTITY_TYPE, rep_id);
        Entity rep_entity = (Entity) datastore.get(rep_entity_key); 
        List<Long> post_ids = (ArrayList<Long>) rep_entity.getProperty(REP_POSTS); 
        post_ids.add(post_id); 
        rep_entity.setProperty(REP_POSTS, post_ids); 
    }
}