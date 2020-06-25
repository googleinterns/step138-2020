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

public class InsertAndUpdate{

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

    public static long insertCommentDatastore(String name, String message) {
        Entity comment_entity = new Entity(COMMENT_ENTITY_TYPE); 
        comment_entity.setProperty(COMMENT_MSG, message); 
        comment_entity.setProperty(COMMENT_NAME, name); 
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(comment_entity); 
        return comment_entity.getKey().getId(); 
    }

    public static long insertRepresentativeDatastore(String name, String title) {
        Entity rep_entity = new Entity(REP_ENTITY_TYPE); 
        rep_entity.setProperty(REP_NAME, name); 
        rep_entity.setProperty(REP_TITLE, title); 
        rep_entity.setProperty(REP_POSTS, null);
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(rep_entity); 
        return rep_entity.getKey().getId(); 
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