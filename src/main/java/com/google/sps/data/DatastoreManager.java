package com.google.sps.data;

import com.google.sps.data.Comment;
import com.google.sps.data.Constants;
import com.google.sps.data.DatastoreEntityToObjectConverter;
import com.google.sps.data.Post;
import com.google.sps.data.Representative;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory;
import java.util.List;
import java.util.ArrayList;
import javax.annotation.Nullable; 

/**
 * Includes helper functions that allow datastore transactions 
 * Pushing entities into datastore, querying datastore, and modifying entities
 */ 
public class DatastoreManager {
    /**
     * Inserts a comment entity into datastore 
     * @param name nickname of user submitting the comment 
     * @param message 
     * @return ID of entity inserted into datastore
     */ 
    public static long insertCommentInDatastore(String name, String message) {
        Entity commentEntity = new Entity(Constants.COMMENT_ENTITY_TYPE); 
        commentEntity.setProperty(Constants.COMMENT_MSG, message); 
        commentEntity.setProperty(Constants.COMMENT_NAME, name); 
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(commentEntity); 
        return commentEntity.getKey().getId(); 
    }

    /**
     * Inserts a representative entity into datastore 
     * @param name of representative
     * @param title of representative  
     * @return ID of entity inserted into datastore
     */ 
    public static long insertRepresentativeInDatastore(String name, String title) {
        Entity repEntity = new Entity(Constants.REP_ENTITY_TYPE); 
        repEntity.setProperty(Constants.REP_NAME, name); 
        repEntity.setProperty(Constants.REP_TITLE, title); 
        repEntity.setProperty(Constants.REP_POSTS, new ArrayList<>());
        List<Long> postIds = (ArrayList<Long>) repEntity.getProperty(Constants.REP_POSTS); 
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(repEntity); 
        return repEntity.getKey().getId(); 
    }

    /**
     * Inserts a post entity into datastore 
     * @param question ID of comment entity representing question made by the user 
     * @return ID of entity inserted into datastore
     */ 
    public static long insertPostInDatastore(long question) {
        Entity postEntity = new Entity(Constants.POST_ENTITY_TYPE); 
        postEntity.setProperty(Constants.POST_QUESTION, question); 
        postEntity.setProperty(Constants.POST_ANSWER, -1); 
        postEntity.setProperty(Constants.POST_REPLIES, new ArrayList<>()); 
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(postEntity); 
        return postEntity.getKey().getId(); 
    }

    /**
     * Updates a representative entity with a new post entity 
     * @param repId ID of the representative entity 
     * @param postId ID of the post entity 
     */ 
    public static void updateRepresentativePostList(long repId, long postId) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key repEntityKey = KeyFactory.createKey(Constants.REP_ENTITY_TYPE, repId);
        Entity repEntity = (Entity) datastore.get(repEntityKey); 
        List<Long> postIds = (ArrayList<Long>) repEntity.getProperty(Constants.REP_POSTS); 
        if (postIds == null) {
            postIds = new ArrayList<>(); 
        }
        postIds.add(postId); 
        repEntity.setProperty(Constants.REP_POSTS, postIds); 
    }

    /**
     * Updates a post entity with a comment in the replies list 
     * @param postId ID of the post entity 
     * @param commentId ID of the comment being added as a reply 
     */ 
    public static void updatePostWithComment(long postId, long commentId) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity postEntity = DatastoreManager.queryForPost(postId); 
        List<Long> commentIds = (ArrayList<Long>) postEntity.getProperty(Constants.POST_REPLIES); 
        if (commentIds == null) {
            commentIds = new ArrayList<>(); 
        }
        commentIds.add(commentId); 
        postEntity.setProperty(Constants.POST_REPLIES, commentIds); 
    }

    /**
     * Searches datastore for a particular representative and converts the entity 
     * to a Representative instance 
     * @param repName name of the representative to search datastore for 
     * @return the representative object, or null if it was not found in datastore 
     */ 
    public static @Nullable Representative queryForRepresentative(String repName) {
        Query query = new Query(Constants.REP_ENTITY_TYPE); 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        Entity repEntity = null; 
        for (Entity entity : results.asIterable()) {
            String name = (String) entity.getProperty(Constants.REP_NAME);
            if (name.equals(repName)) {
                repEntity = entity; 
            }
        }
        if (repEntity == null) {
            System.out.println("retugning null 1"); 
            return null; 
        }
        else {
            Representative rep = null; 
            try {
                rep = DatastoreEntityToObjectConverter.convertRepresentative(repEntity); 
                return rep; 
            } 
            catch(EntityNotFoundException e) {
                System.out.println("retugning null 2"); 
                return null; 
            }
        }
    }

    /**
     * Searches datastore for a particular post 
     * @param postId ID of the post to search datastore for 
     * @return the post entity found in datastore 
     */ 
    public static Entity queryForPost(long postId) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key postEntityKey = KeyFactory.createKey(Constants.POST_ENTITY_TYPE, postId);
        Entity postEntity = (Entity) datastore.get(postEntityKey); 
        return postEntity; 
    }
}
