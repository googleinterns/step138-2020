package com.google.sps.data;

import com.google.sps.data.Comment;
import com.google.sps.data.Constants;
import com.google.sps.data.Post;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.List;

public class QueryDatastore{
    public static Entity queryForRepresentative(String repName) {
        Query query = new Query(Constants.REP_ENTITY_TYPE); 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity : results.asIterable()) {
            String name = (String) entity.getProperty(Constants.REP_NAME);
            if (name == repName) {
                return entity; 
            }
        }
        return null; 
    }

    public static Entity queryForPost(long postId) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key postEntityKey = KeyFactory.createKey(Constants.POST_ENTITY_TYPE, postId);
        Entity postEntity = (Entity) datastore.get(postEntityKey); 
        return postEntity; 
    }
}
