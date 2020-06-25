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

public class QueryDatastore{

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

    public static Entity queryForPost(long post_id) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key post_entity_key = KeyFactory.createKey(POST_ENTITY_TYPE, post_id);
        Entity post_entity = (Entity) datastore.get(post_entity_key); 
        return post_entity; 
    }
}