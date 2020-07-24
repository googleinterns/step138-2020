package com.google.sps.data;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import com.google.sps.data.Comment;
import com.google.sps.data.Constants;
import com.google.sps.data.Post;
import com.google.sps.data.Reaction;
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
public final class DatastoreEntityToObjectConverter {  
    /**
     * Converts a representative entity into a Representative object 
     * @param entity of the representative 
     * @throws EntityNotFoundException
     * @return the Representative object 
     */  
    protected static Representative convertRepresentative(Entity entity) 
    throws EntityNotFoundException {
        String name = (String) entity.getProperty(Constants.REP_NAME);
        String title = (String) entity.getProperty(Constants.REP_TITLE);
        String username = (String) entity.getProperty(Constants.REP_USERNAME);
        String password = (String) entity.getProperty(Constants.REP_PASSWORD);
        List<Post> posts = convertPostsFromRep(entity); 
        String intro = (String) entity.getProperty(Constants.REP_INTRO);
        String blobKeyUrl = (String) entity.getProperty(Constants.REP_BLOB_KEY_URL);
        List<Tab> tabs = convertTabsFromRep(entity);
        long id = entity.getKey().getId();
        return new Representative(
            name, title, username, password, posts, intro, blobKeyUrl, tabs, id);   
    }
    
    /**
     * Converts a post entity into a Post object 
     * @param postEntity entity of the post 
     * @throws EntityNotFoundException
     * @return the Post object 
     */ 
    protected static Post convertPost(Entity postEntity) 
    throws EntityNotFoundException {
        long questionId = (long) (postEntity.getProperty(Constants.POST_QUESTION));
        Comment question = convertComment(questionId);
        long answerId = (long) postEntity.getProperty(Constants.POST_ANSWER);
        long timestamp = (long) postEntity.getProperty(Constants.POST_TIMESTAMP);
        Comment answer = convertComment(answerId);
        List<Comment> comments = convertCommentsFromPost(postEntity); 
        List<String> tabs = (List<String>) postEntity.getProperty(Constants.POST_TABS);
        long id = postEntity.getKey().getId();
        List<String> reactionStrings = Reaction.allValues(); 
        Map<Reaction, Long> reactions = new HashMap<Reaction, Long>(); 
        for (String reactionString : reactionStrings) {
            long reactionCount = 0; 
            if (postEntity.getProperty(reactionString) != null) {
                reactionCount = (long) (postEntity.getProperty(reactionString));
            }
            reactions.put(Reaction.fromString(reactionString), reactionCount); 
        }
        return new Post(question, answer, comments, tabs, id, reactions, timestamp); 
    }

    /**
     * Converts a tab entity into a Tab object 
     * @param tabEntity entity of the tab 
     * @throws EntityNotFoundException
     * @return the Tab object 
     */ 
    static Tab convertTab(Entity tabEntity) 
    throws EntityNotFoundException {
        String tabName = (String) (tabEntity.getProperty(Constants.TAB_NAME));
        String platform = (String) (tabEntity.getProperty(Constants.TAB_PLATFORM));
        long id = tabEntity.getKey().getId();
        return new Tab(tabName, platform, id); 
    }

    /**
     * Converts a tab entity into a Tab object 
     * @param tabIds list of tab ids 
     * @return list of tab names corresponding to ids
     */ 
    static List<String> convertNamesFromTabs(List<Long> tabIds) 
    throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<String> tabNames = new ArrayList<String>();
        for (long tabId : tabIds) {
            Key tabEntityKey = KeyFactory.createKey(Constants.TAB_ENTITY_TYPE, tabId);
            Entity tabEntity = (Entity) datastore.get(tabEntityKey); 
            String tabName = (String) tabEntity.getProperty(Constants.TAB_NAME);
            tabNames.add(tabName);
        }
        return tabNames;
    }

    /**
     * Converts a list of tab entities into tab objects pulled from a rep 
     * @param repEntity entity of the rep
     * @throws EntityNotFoundException
     * @return List of tab objects
     */ 
    protected static List<Tab> convertTabsFromRep(Entity repEntity) 
    throws EntityNotFoundException {
        List<Long> tabIds = (ArrayList<Long>) repEntity.getProperty(Constants.REP_TABS); 
        List<Tab> tabs = new ArrayList<>(); 
        if (tabIds == null) {
            return tabs; 
        }
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        for (long tabId : tabIds) {
            Key tabEntityKey = KeyFactory.createKey(Constants.TAB_ENTITY_TYPE, tabId);
            Entity tabEntity = (Entity) datastore.get(tabEntityKey); 
            Tab tab = convertTab(tabEntity); 
            tabs.add(tab); 
        }
        return new ArrayList<Tab>(tabs); 
    }

    private static List<Post> convertPostsFromRep(Entity repEntity) 
    throws EntityNotFoundException {
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
        return new ArrayList<Post>(posts); 
    }

    private static List<Comment> convertCommentsFromPost(Entity postEntity) 
    throws EntityNotFoundException {
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

    private static Comment convertComment(long commentId) 
    throws EntityNotFoundException {
        if (commentId == -1) {
            return null;
        }
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key commentEntityKey = KeyFactory.createKey(Constants.COMMENT_ENTITY_TYPE, commentId);
        Entity commentEntity = (Entity) datastore.get(commentEntityKey);
        String name = (String) commentEntity.getProperty(Constants.COMMENT_NAME);
        String msg = (String) commentEntity.getProperty(Constants.COMMENT_MSG);
        long id = commentEntity.getKey().getId();
        return new Comment(name, msg, id); 
    }
}
