package com.google.sps.data;

import java.util.List;
import java.util.ArrayList;
import javax.servlet.ServletException;
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
        System.out.println("Reached convertrep");
        return new Representative(name, title, posts, id);   
    }

    private static List<Post> convertPostsFromRep(Entity repEntity) throws EntityNotFoundException{
        List<Long> postIds = (ArrayList<Long>) repEntity.getProperty(Constants.REP_POSTS); 
        System.out.println("THe postids: " + postIds);
        List<Post> posts = new ArrayList<>(); 
        if (postIds == null) {
            return posts; 
        }
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        for (long postId : postIds) {
            Key postEntityKey = KeyFactory.createKey(Constants.POST_ENTITY_TYPE, postId);
            Entity postEntity = (Entity) datastore.get(postEntityKey); 
            Post post = convertPost(postEntity); 
            System.out.println("This is the post: " + post);
            posts.add(post); 
        }
        System.out.println("These are the posts: " + posts);
        return posts; 
    }

    private static Post convertPost(Entity postEntity) throws EntityNotFoundException{
        long questionId = (long) (postEntity.getProperty(Constants.POST_QUESTION));
        Comment question = convertComment(questionId);
        System.out.println("This is the question: " + question);
        long answerId = (long) (postEntity.getProperty(Constants.POST_ANSWER));
        System.out.println("Answer id: " + answerId);
        Comment answer = convertComment(answerId);
        List<Comment> comments = convertCommentsFromPost(postEntity); 
        System.out.println("These are the comments: " + comments);
        long id = postEntity.getKey().getId();
        System.out.println("Reached convertPost");
        return new Post(question, answer, comments, id); 
    }

    private static List<Comment> convertCommentsFromPost(Entity postEntity) throws EntityNotFoundException{
        System.out.println("This is the postEntity: " + postEntity);
        List<Long> commentIds = (ArrayList<Long>) postEntity.getProperty(Constants.POST_REPLIES); 
        System.out.println("These are the comment ids: " + commentIds);
        List<Comment> comments = new ArrayList<>(); 
        if (commentIds == null) {
            return comments; 
        }
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        for (long commentId : commentIds) {
            Comment comment = convertComment(commentId); 
            comments.add(comment); 
        }
        System.out.println("Reached convert comments from post");
        return comments; 
    }

    private static Comment convertComment(long commentId) throws EntityNotFoundException {
        if (commentId == -1){
            return null;
        }
        System.out.println("This is the commentId: " + commentId);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key commentEntityKey = KeyFactory.createKey(Constants.COMMENT_ENTITY_TYPE, commentId);
        Entity commentEntity = (Entity) datastore.get(commentEntityKey);
        String name = (String) commentEntity.getProperty(Constants.COMMENT_NAME);
        String msg = (String) commentEntity.getProperty(Constants.COMMENT_MSG);
        long id = commentEntity.getKey().getId();
        System.out.println("Reached convert comment");
        return new Comment(name, msg, id); 
    }
}
