package com.google.sps.data;

import java.util.List;
import java.util.ArrayList;
import com.google.sps.data.Comment;
import com.google.sps.data.Post;
import com.google.sps.data.Constants;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory;

public class InsertAndUpdate{
    public static long insertCommentDatastore(String name, String message) {
        Entity commentEntity = new Entity(Constants.COMMENT_ENTITY_TYPE); 
        commentEntity.setProperty(Constants.COMMENT_MSG, message); 
        commentEntity.setProperty(Constants.COMMENT_NAME, name); 
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(commentEntity); 
        return commentEntity.getKey().getId(); 
    }

    public static long insertRepresentativeDatastore(String name, String title) {
        Entity repEntity = new Entity(Constants.REP_ENTITY_TYPE); 
        repEntity.setProperty(Constants.REP_NAME, name); 
        repEntity.setProperty(Constants.REP_TITLE, title); 
        repEntity.setProperty(Constants.REP_POSTS, null);
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(repEntity); 
        return repEntity.getKey().getId(); 
    }

    public static long insertPostDatastore(long question) {
        Entity postEntity = new Entity(Constants.COMMENT_ENTITY_TYPE); 
        postEntity.setProperty(Constants.POST_QUESTION, question); 
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(postEntity); 
        return postEntity.getKey().getId(); 
    }

    public static void updateRepresentativePostList(long repId, long postId) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key repEntityKey = KeyFactory.createKey(Constants.REP_ENTITY_TYPE, repId);
        Entity repEntity = (Entity) datastore.get(repEntityKey); 
        List<Long> postIds = (ArrayList<Long>) rep_entity.getProperty(Constants.REP_POSTS); 
        postIds.add(postId); 
        repEntity.setProperty(Constants.REP_POSTS, postIds); 
    }
}