package com.google.sps.data;

import java.util.List;
import java.util.ArrayList;
import com.google.sps.data.Comment;
import com.google.sps.data.Constants;
import com.google.sps.data.Post;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory;

/**
 * Converts entities from datastore into objects 
 */  
public class DatastoreEntityToObjectConverter {
    /**
     * Converts a representative entity into a representative object 
     * @param entity of the representative 
     * @return the representative object 
     */  
    protected static Representative convertRepresentative(Entity entity) throws EntityNotFoundException{
        String name = (String) entity.getProperty(Constants.REP_NAME);
        String title = (String) entity.getProperty(Constants.REP_TITLE);
        List<Post> posts = convertPostsFromRep(entity); 
        long id = entity.getKey().getId();
        return new Representative(name, title, posts, id);   
    }

    private static List<Post> convertPostsFromRep(Entity repEntity) throws EntityNotFoundException{
        List<Long> postIds = (ArrayList<Long>) repEntity.getProperty(Constants.REP_POSTS); 
        List<Post> posts = new ArrayList<>(); 
        if (postIds == null) {
            return posts; 
        }
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        for (long postId : postIds) {
            Key postEntityKey = KeyFactory.createKey(Constants.POST_ENTITY_TYPE, postId);
            Entity postEntity = (Entity) datastore.get(postEntityKey); 
            Post post = convertPost(postEntity); 
            posts.add(post); 
        }
        return posts; 
    }

    private static Post convertPost(Entity postEntity) throws EntityNotFoundException{
        long questionId = (long) (postEntity.getProperty(Constants.POST_QUESTION));
        Comment question = convertComment(questionId);
        long answerId = (long) (postEntity.getProperty(Constants.POST_ANSWER));
        Comment answer = convertComment(answerId);
        List<Comment> comments = convertCommentsFromPost(postEntity); 
        long id = postEntity.getKey().getId();
        return new Post(question, answer, comments, id); 
    }

    private static List<Comment> convertCommentsFromPost(Entity postEntity) throws EntityNotFoundException{
        List<Long> commentIds = (ArrayList<Long>) postEntity.getProperty(Constants.POST_REPLIES); 
        List<Comment> comments = new ArrayList<>(); 
        if (commentIds == null) {
            return comments; 
        }
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        for (long commentId : commentIds) {
            Comment comment = convertComment(commentId); 
            comments.add(comment); 
        }
        return comments; 
    }

    private static Comment convertComment(long commentId) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key commentEntityKey = KeyFactory.createKey(Constants.COMMENT_ENTITY_TYPE, commentId);
        Entity commentEntity = (Entity) datastore.get(commentEntityKey);
        String name = (String) commentEntity.getProperty(Constants.COMMENT_NAME);
        String msg = (String) commentEntity.getProperty(Constants.COMMENT_MSG);
        long id = commentEntity.getKey().getId();
        return new Comment(name, msg, id); 
    }
}