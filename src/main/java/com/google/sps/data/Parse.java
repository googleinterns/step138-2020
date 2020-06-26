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

public class Parse{

    public static Representative parseRepresentative(Entity entity) throws EntityNotFoundException{
        String name = (String) entity.getProperty(Constants.REP_NAME);
        String title = (String) entity.getProperty(Constants.REP_TITLE);
        List<Post> posts = parsePosts(entity); 
        long id = entity.getKey().getId();
        return new Representative(name, title, posts, id);   
    }

    public static List<Post> parsePosts(Entity repEntity) throws EntityNotFoundException{
        List<Long> postIds = (ArrayList<Long>) repEntity.getProperty(Constants.REP_POSTS); 
        List<Post> posts = new ArrayList<>(); 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        for (long postId : postIds) {
            Key postEntityKey = KeyFactory.createKey(Constants.POST_ENTITY_TYPE, postId);
            Entity postEntity = (Entity) datastore.get(postEntityKey); 
            Post post = parsePost(postEntity); 
            posts.add(post); 
        }
        return posts; 
    }

    public static Post parsePost(Entity postEntity) throws EntityNotFoundException{
        long questionId = (long) postEntity.getProperty(Constants.POST_QUESTION);
        Comment question = Parse.parseComment(questionId);
        long answerId = (long) postEntity.getProperty(Constants.POST_ANSWER);
        Comment answer = Parse.parseComment(answerId);
        List<Comment> comments = Parse.parseComments(postEntity); 
        long id = postEntity.getKey().getId();
        return new Post(question, answer, comments, id); 
    }

    private static List<Comment> parseComments(Entity postEntity) throws EntityNotFoundException{
        List<Long> commentIds = (ArrayList<Long>) postEntity.getProperty(Constants.POST_REPLIES); 
        List<Comment> comments = new ArrayList<>(); 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        for (long commentId : commentIds) {
            Comment comment = parseComment(commentId); 
            comments.add(comment); 
        }
        return comments; 
    }

    private static Comment parseComment(long commentId) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key commentEntityKey = KeyFactory.createKey(Constants.COMMENT_ENTITY_TYPE, commentId);
        Entity commentEntity = (Entity) datastore.get(commentEntityKey);
        String name = (String) commentEntity.getProperty(Constants.COMMENT_NAME);
        String msg = (String) commentEntity.getProperty(Constants.COMMENT_MSG);
        long id = commentEntity.getKey().getId();
        return new Comment(name, msg, id); 
    }
}
