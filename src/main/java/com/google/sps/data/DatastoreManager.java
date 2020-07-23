package com.google.sps.data;

import com.google.sps.data.Comment;
import com.google.sps.data.Constants;
import com.google.sps.data.DatastoreEntityToObjectConverter;
import com.google.sps.data.Post;
import com.google.sps.data.Reaction;
import com.google.sps.data.Representative;
import com.google.sps.data.ToxicCommentException;
import com.google.sps.data.ToxicityDetector;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory;
import java.lang.System; 
import java.lang.UnsupportedOperationException; 
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Includes helper functions that allow datastore transactions 
 * Pushing entities into datastore, querying datastore, and modifying entities
 */ 
public class DatastoreManager {
    private static final Logger logger = LogManager.getLogger("DatastoreManager");

    /**
     * Inserts a comment entity into datastore if not toxic 
     * @param name nickname of user submitting the comment 
     * @param message 
     * @return ID of entity inserted into datastore
     */ 
    public static long insertCommentInDatastoreIfNonToxic(String name, String message) 
    throws ToxicCommentException {
        if (ToxicityDetector.isCommentToxic(message)) {
            throw new ToxicCommentException("Can't enter toxic comments."); 
        }
        return insertCommentInDatastore(name, message); 
    }

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
    public static long insertRepresentativeInDatastore(
    String name, String title, String username, String password, List<Long> tabIds) {
        Entity repEntity = new Entity(Constants.REP_ENTITY_TYPE); 
        repEntity.setProperty(Constants.REP_NAME, name); 
        repEntity.setProperty(Constants.REP_TITLE, title); 
        repEntity.setProperty(Constants.REP_USERNAME, username);
        repEntity.setProperty(Constants.REP_PASSWORD, password);
        repEntity.setProperty(Constants.REP_POSTS, new ArrayList<>());
        repEntity.setProperty(Constants.REP_INTRO, "");
        repEntity.setProperty(Constants.REP_BLOB_KEY_URL, "");
        repEntity.setProperty(Constants.REP_TABS, tabIds);
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
    public static long insertPostInDatastore(long question, List<String> tabs) {
        Entity postEntity = new Entity(Constants.POST_ENTITY_TYPE); 
        postEntity.setProperty(Constants.POST_QUESTION, question); 
        postEntity.setProperty(Constants.POST_ANSWER, -1); 
        postEntity.setProperty(Constants.POST_REPLIES, new ArrayList<>());
        List<String> reactions = Reaction.allValues();
        for (String reaction : reactions) {
            postEntity.setProperty(reaction, (long) 0);
        }
        postEntity.setProperty(Constants.POST_TABS, tabs);
        postEntity.setProperty(Constants.POST_TIMESTAMP, System.currentTimeMillis());
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.put(postEntity); 
        return postEntity.getKey().getId(); 
    }

    /**
     * Inserts a tab entity into datastore 
     * @param name name of the tab being inserted
     * @param platform description of representative's platform for particular tab
     * @return ID of entity inserted into datastore
     */ 
    public static List<Long> insertTabsInDatastore(List<String> names, List<String> platforms) 
    throws EntityNotFoundException {
        List<Long> tabIds = new ArrayList<>();
        Entity existingTab;
        for (int i = 0 ; i < names.size() ; i++) {
            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            existingTab = DatastoreManager.queryForTabEntityWithName(names.get(i));
            if (existingTab == null) {
                Entity tabEntity = new Entity(Constants.TAB_ENTITY_TYPE); 
                tabEntity.setProperty(Constants.TAB_NAME, names.get(i)); 
                tabEntity.setProperty(Constants.TAB_PLATFORM, platforms.get(i)); 
                ds.put(tabEntity); 
                tabIds.add(tabEntity.getKey().getId());
            }
            else {
                existingTab.setProperty(Constants.TAB_PLATFORM, platforms.get(i));
                ds.put(existingTab);
                tabIds.add(existingTab.getKey().getId());
            }
        }
        return tabIds;
    }

    /**
     * Updates a representative entity with a new post entity 
     * @param repId ID of the representative entity 
     * @param postId ID of the post entity 
     */ 
    public static void updateRepresentativePostList(long repId, long postId) 
    throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key repEntityKey = KeyFactory.createKey(Constants.REP_ENTITY_TYPE, repId);
        Entity repEntity = (Entity) datastore.get(repEntityKey); 
        List<Long> postIds = (ArrayList<Long>) repEntity.getProperty(Constants.REP_POSTS); 
        if (postIds == null) {
            postIds = new ArrayList<>(); 
        }
        postIds.add(postId);
        repEntity.setProperty(Constants.REP_POSTS, postIds); 
        datastore.put(repEntity);
    }

    /**
     * Updates a representative entity with a new tab entity 
     * @param repId ID of the representative entity 
     * @param tabId ID of the tab entity 
     */ 
    public static void updateRepresentativeTabList(long repId, List<Long> tabIdList) 
    throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key repEntityKey = KeyFactory.createKey(Constants.REP_ENTITY_TYPE, repId);
        Entity repEntity = (Entity) datastore.get(repEntityKey); 
        List<Long> tabIds = (ArrayList<Long>) repEntity.getProperty(Constants.REP_TABS); 
        if (tabIds == null) {
            tabIds = new ArrayList<>(); 
            tabIds.addAll(tabIdList);
        }
        else {
            List<String> tabNames = DatastoreEntityToObjectConverter.convertNamesFromTabs(tabIds);
            for (Long tabId : tabIdList) {
                Key tabEntityKey = KeyFactory.createKey(Constants.TAB_ENTITY_TYPE, tabId);
                Entity tabEntity = (Entity) datastore.get(tabEntityKey); 
                String name = (String) tabEntity.getProperty(Constants.TAB_NAME);
                if (tabNames.contains(name)) {
                    tabIds.set(tabNames.indexOf(name), tabId);
                }
                else {
                    tabIds.add(tabId);
                }
            }
        }
        repEntity.setProperty(Constants.REP_TABS, tabIds); 
        datastore.put(repEntity);
    }

    /**
     * Updates a representative entity with a new intro
     * @param repId ID of the representative entity 
     * @param intro a brief description of representative
     */ 
    public static void updateRepresentativeIntro(long repId, String intro) 
    throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key repEntityKey = KeyFactory.createKey(Constants.REP_ENTITY_TYPE, repId);
        Entity repEntity = (Entity) datastore.get(repEntityKey); 
        repEntity.setProperty(Constants.REP_INTRO, intro); 
        datastore.put(repEntity);
    }

    /**
     * Updates a representative entity with a new image
     * @param repId ID of the representative entity 
     * @param blobKeyUrl a link to the representative's profile image
     */ 
    public static void updateRepresentativeImage(long repId, String blobKeyUrl) 
    throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key repEntityKey = KeyFactory.createKey(Constants.REP_ENTITY_TYPE, repId);
        Entity repEntity = (Entity) datastore.get(repEntityKey); 
        repEntity.setProperty(Constants.REP_BLOB_KEY_URL, blobKeyUrl); 
        datastore.put(repEntity);
    }

    /**
     * Updates a post entity with a comment in the replies list 
     * @param postId ID of the post entity 
     * @param commentId ID of the comment being added as a reply 
     */ 
    public static void updatePostWithComment(long postId, long commentId) 
    throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity postEntity = DatastoreManager.queryForPostEntityWithId(postId); 
        List<Long> commentIds = (ArrayList<Long>) postEntity.getProperty(Constants.POST_REPLIES); 
        if (commentIds == null) {
            commentIds = new ArrayList<>(); 
        }
        commentIds.add(commentId); 
        postEntity.setProperty(Constants.POST_REPLIES, commentIds); 
        datastore.put(postEntity);
    }

    /**
     * Updates a post entity with a comment in the replies list 
     * @param postId ID of the post entity 
     * @param commentId ID of the comment being added as a reply 
     */ 
    public static void updatePostWithAnswer(long postId, long answerId) 
    throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity postEntity = DatastoreManager.queryForPostEntityWithId(postId); 
        postEntity.setProperty(Constants.POST_ANSWER, answerId); 
        datastore.put(postEntity);
    }

    /**
     * Updates the tab associated with a post entity
     * @param postId ID of the post entity 
     * @param tabName name of the new tab post is associated with
     */ 
    public static void updatePostTab(long postId, String tabName) 
    throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity postEntity = DatastoreManager.queryForPostEntityWithId(postId); 
        postEntity.setProperty(Constants.POST_TABS, Arrays.asList(tabName)); 
        datastore.put(postEntity);
    }

    /**
     * Searches datastore for a particular representative and converts the entity 
     * to a Representative instance 
     * @param repName name of the representative to search datastore for 
     * @return the representative object, or null if it was not found in datastore 
     */ 
    public static Representative queryForRepresentativeObjectWithName(String repName) {
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
            return null; 
        }
        else {
            Representative rep = null; 
            try {
                rep = DatastoreEntityToObjectConverter.convertRepresentative(repEntity); 
                return rep; 
            } 
            catch(EntityNotFoundException e) {
                logger.error(e);
                return null; 
            }
        }
    }

    /**
     * Searches datastore for a particular representative with username 
     * and password and returns the representative's name
     * @param repUsername username of the representative to search datastore for 
     * @param repPassword password of the representative to search datastore for
     * @return the representative's name as string, or null if it was not found in datastore 
     */ 
    public static String queryForRepresentativeNameWithLogin(
    String repUsername, String repPassword) throws EntityNotFoundException {
        Query query = new Query(Constants.REP_ENTITY_TYPE); 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        Entity repEntity = null; 
        for (Entity entity : results.asIterable()) {
            String username = ((String) entity.getProperty(Constants.REP_USERNAME)).trim();
            String password = ((String) entity.getProperty(Constants.REP_PASSWORD)).trim();
            if (username.equals(repUsername) && password.equals(repPassword)) {
                repEntity = entity; 
            }
        }
        return (repEntity != null) ? (String) repEntity.getProperty(Constants.REP_NAME) : null;
    }

    /**
     * Searches datastore to check if username already taken
     * @param repUsername username of the representative to search datastore for 
     * @return the rep entity with a particular username
     */ 
    public static Entity queryForRepresentativeUsername(String repUsername) 
    throws EntityNotFoundException {
        Query query = new Query(Constants.REP_ENTITY_TYPE); 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        Entity repEntity = null; 
        for (Entity entity : results.asIterable()) {
            String username = ((String) entity.getProperty(Constants.REP_USERNAME)).trim();
            if (username.equals(repUsername)) {
                repEntity = entity; 
            }
        }
        return repEntity;
    }

    /**
     * Searches datastore for a particular representative and returns entity
     * @param repName name of the representative to search datastore for 
     * @return the representative entity, or null if it was not found in datastore 
     */ 
    public static Entity queryForRepresentativeEntityWithName(String repName) 
    throws EntityNotFoundException {
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
        return repEntity; 
    }

    /**
     * Searches datastore for a particular representative and returns entity
     * @param repName name of the representative to search datastore for
     * @param tab name of the tab that we want to categorize by 
     * @return the list of posts under a representative that are associated with particular tab 
     */ 
    public static List<Post> queryForPostListWithTab(String repName, String tab) {
        List<Post> postListForTab = new ArrayList<>();
        Representative rep = null;
        rep = queryForRepresentativeObjectWithName(repName);
        if (rep != null) {
            List<Post> postList= rep.getPosts();
            for (Post post : postList) {
                if (post.getTabs().contains(tab)) {
                    postListForTab.add(post);
                }
            }
        }
        return postListForTab; 
    }

    /**
     * Searches datastore for list of tabs corresponding to a particular representative
     * @param repName name of the representative to search datastore for
     * @return the list of tabs under a representative
     */ 
    public static List<Tab> queryForTabListWithRepName(String repName)
    throws EntityNotFoundException {
        List<Tab> tabListForRep = new ArrayList<>();
        Entity repEntity;
        try {
            repEntity = queryForRepresentativeEntityWithName(repName);
        }
        catch(EntityNotFoundException e) {
            logger.error(e);
            return null; 
        }
        return DatastoreEntityToObjectConverter.convertTabsFromRep(repEntity);
    }

    /**
     * Searches datastore for a particular post and returns post object
     * @param postId ID of the post to search datastore for 
     * @return the post object found in datastore 
     */ 
    public static Post queryForPostObjectWithId(long postId) 
    throws EntityNotFoundException {
        Entity postEntity = DatastoreManager.queryForPostEntityWithId(postId); 
        return DatastoreEntityToObjectConverter.convertPost(postEntity);
    }

    /**
     * Searches datastore for a particular post entity
     * @param postId ID of the post to search datastore for 
     * @return the post entity found in datastore 
     */ 
    public static Entity queryForPostEntityWithId(long postId) 
    throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key postEntityKey = KeyFactory.createKey(Constants.POST_ENTITY_TYPE, postId);
        Entity postEntity = (Entity) datastore.get(postEntityKey); 
        return postEntity; 
    }

    /**
     * Updates a post entity with a reaction
     * @param postId ID of the post entity 
     * @param reaction String of the reaction enum 
     */ 
    public static void addReactionToPost(long postId, String reaction) 
    throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity postEntity = DatastoreManager.queryForPostEntityWithId(postId); 
        long reactionCount = 0; 
        if (postEntity.getProperty(reaction) != null) {
            reactionCount = (long) postEntity.getProperty(reaction); 
        }
        postEntity.setProperty(reaction, reactionCount + 1); 
        datastore.put(postEntity);
    }

    /**
     * Removes a reaction from a post entity 
     * @param postId ID of the post entity 
     * @param reaction String of the reaction enum 
     */ 
    public static void removeReactionFromPost(long postId, String reaction) 
    throws EntityNotFoundException, UnsupportedOperationException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity postEntity = DatastoreManager.queryForPostEntityWithId(postId); 
        Long reactionCount = (long) postEntity.getProperty(reaction); 
        if (reactionCount == null || reactionCount == 0) {
            throw new UnsupportedOperationException("No such reactions in post entity"); 
        }
        reactionCount -= 1;
        postEntity.setProperty(reaction, reactionCount); 
        datastore.put(postEntity);
    }

    /*
     * Searches datastore for a particular tab entity
     * @param tabName name of the tab to search datastore for 
     * @return the tab entity found in datastore 
     */ 
    public static Entity queryForTabEntityWithName(String tabName) 
    throws EntityNotFoundException {
        Query query = new Query(Constants.TAB_ENTITY_TYPE); 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        Entity tabEntity = null; 
        for (Entity entity : results.asIterable()) {
            String name = (String) entity.getProperty(Constants.TAB_NAME);
            if (name.equals(tabName)) {
                tabEntity = entity; 
            }
        }
        return tabEntity; 
    }
}
