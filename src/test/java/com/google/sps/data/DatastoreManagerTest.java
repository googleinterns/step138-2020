package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.Comment;
import com.google.sps.data.Constants;
import com.google.sps.data.DatastoreManager;
import com.google.sps.data.DatastoreEntityToObjectConverter;
import com.google.sps.data.Post;
import com.google.sps.data.Representative;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** */
@RunWith(JUnit4.class)
public final class DatastoreManagerTest {
    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private DatastoreService ds; 

    @Before
    public void setUp() {
        helper.setUp();
        ds = DatastoreServiceFactory.getDatastoreService();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testInsertCommentInDatastore() throws EntityNotFoundException{
        long commentId = DatastoreManager.insertCommentInDatastore("Anonymous", "Nice dude"); 
        Key commentEntityKey = KeyFactory.createKey(Constants.COMMENT_ENTITY_TYPE, commentId);
        Entity commentEntity = (Entity) ds.get(commentEntityKey);
        
        String name = (String) (commentEntity.getProperty(Constants.COMMENT_NAME));
        String msg = (String) (commentEntity.getProperty(Constants.COMMENT_MSG));

        Assert.assertTrue(name.equals("Anonymous")); 
        Assert.assertTrue(msg.equals("Nice dude")); 
    }

    @Test
    public void testInsertRepresentativeInDatastore() throws EntityNotFoundException{
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", "President of the US"); 
        Key repEntityKey = KeyFactory.createKey(Constants.REP_ENTITY_TYPE, repId);
        Entity repEntity = (Entity) ds.get(repEntityKey);

        String name = (String) (repEntity.getProperty(Constants.REP_NAME));
        String title = (String) (repEntity.getProperty(Constants.REP_TITLE));

        Assert.assertTrue(name.equals("Donald Trump")); 
        Assert.assertTrue(title.equals("President of the US")); 
    }

    @Test 
    public void testInsertPostInDatastore() throws EntityNotFoundException {
        long questionId = DatastoreManager.insertCommentInDatastore("Anonymous", "Why are you president?"); 
        long postId = DatastoreManager.insertPostInDatastore(questionId); 
        Key postEntityKey = KeyFactory.createKey(Constants.POST_ENTITY_TYPE, postId); 
        Entity postEntity = (Entity) ds.get(postEntityKey); 

        long questionIdRetrieved = (long) (postEntity.getProperty(Constants.POST_QUESTION)); 
        long answerIdRetrieved = (long) (postEntity.getProperty(Constants.POST_ANSWER)); 
        Key questionEntityKey = KeyFactory.createKey(Constants.COMMENT_ENTITY_TYPE, questionIdRetrieved); 
        Entity questionEntity = (Entity) ds.get(questionEntityKey); 
        String name = (String) (questionEntity.getProperty(Constants.COMMENT_NAME));
        String msg = (String) (questionEntity.getProperty(Constants.COMMENT_MSG));

        Assert.assertTrue(answerIdRetrieved == -1); 
        Assert.assertTrue(name.equals("Anonymous")); 
        Assert.assertTrue(msg.equals("Why are you president?")); 
    }

    @Test
    public void testUpdateRepresentativePostList() throws EntityNotFoundException{
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", "President of the US");
        long questionId = DatastoreManager.insertCommentInDatastore("Anonymous", "Why are you president?"); 
        long postId = DatastoreManager.insertPostInDatastore(questionId); 
        
        DatastoreManager.updateRepresentativePostList(repId, postId); 
        Representative rep = DatastoreManager.queryForRepresentativeObjectWithName("Donald Trump"); 
        List<Post> posts = rep.getPosts(); 
        Post post = posts.get(0); 

        Assert.assertTrue(posts.size() == 1); 
        Assert.assertTrue(post.getQuestion().getDisplayName().equals("Anonymous")); 
        Assert.assertTrue(post.getQuestion().getComment().equals("Why are you president?")); 
    }

    @Test
    public void testUpdatePostWithComment() throws EntityNotFoundException{
        long questionId = DatastoreManager.insertCommentInDatastore("Anonymous", "Why are you president?"); 
        long postId = DatastoreManager.insertPostInDatastore(questionId); 
        long commentId = DatastoreManager.insertCommentInDatastore("Anonymous", "Nice dude."); 

        DatastoreManager.updatePostWithComment(postId, commentId);
        Post postRetrived = DatastoreManager.queryForPostObjectWithId(postId);
        List<Comment> replies = postRetrived.getReplies(); 

        Assert.assertTrue(replies.size() == 1); 
        Assert.assertTrue(replies.get(0).getDisplayName().equals("Anonymous")); 
        Assert.assertTrue(replies.get(0).getComment().equals("Nice dude.")); 
    }

    @Test
    public void testUpdatePostWithAnswer() throws EntityNotFoundException {
        long questionId = DatastoreManager.insertCommentInDatastore("Anonymous", "Why are you president?"); 
        long postId = DatastoreManager.insertPostInDatastore(questionId); 
        long answerId = DatastoreManager.insertCommentInDatastore("Donald Trump", "Because I want to."); 

        DatastoreManager.updatePostWithAnswer(postId, answerId);
        Post postRetrieved = DatastoreManager.queryForPostObjectWithId(postId);
        Comment answer = postRetrieved.getAnswer(); 

        Assert.assertTrue(answer.getDisplayName().equals("Donald Trump")); 
        Assert.assertTrue(answer.getComment().equals("Because I want to.")); 
    }

    @Test
    public void testQueryForRepresentativeObjectWithName() {
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", "President of the US");

        Representative actualRep = DatastoreManager.queryForRepresentativeObjectWithName("Donald Trump");
        Representative expectedRep = new Representative("Donald Trump", "President of the US", new ArrayList<>(), repId);

        Assert.assertTrue(actualRep.equals(expectedRep));
    }

    @Test
    public void testQueryForRepresentativeEntityWithName() throws EntityNotFoundException{
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", "President of the US");

        Entity repEntity = DatastoreManager.queryForRepresentativeEntityWithName("Donald Trump");
        String name = (String) repEntity.getProperty(Constants.REP_NAME); 
        String title = (String) repEntity.getProperty(Constants.REP_TITLE); 
        
        Assert.assertTrue(name.equals("Donald Trump")); 
        Assert.assertTrue(title.equals("President of the US")); 
    }

    @Test
    public void testQueryForPostObjectWithId() throws EntityNotFoundException {
        long commentId = DatastoreManager.insertCommentInDatastore("Anonymous", "Nice dude");
        List<Long> commentIds = new ArrayList<>(); 
        commentIds.add(commentId); 
        long commentIdQuestion = DatastoreManager.insertCommentInDatastore("Anonymous", "Why are you in office?");
        long commentIdAnswer = DatastoreManager.insertCommentInDatastore("Donald Trump", "Because I want to be.");
        Entity postEntity = new Entity(Constants.POST_ENTITY_TYPE); 
        postEntity.setProperty(Constants.POST_QUESTION, commentIdQuestion); 
        postEntity.setProperty(Constants.POST_ANSWER, commentIdAnswer); 
        postEntity.setProperty(Constants.POST_REPLIES, commentIds); 
        ds.put(postEntity); 

        long postId = postEntity.getKey().getId(); 
        Post postRetrieved = DatastoreManager.queryForPostObjectWithId(postId);
        Comment question = postRetrieved.getQuestion(); 
        Comment answer = postRetrieved.getAnswer(); 
        List<Comment> replies = postRetrieved.getReplies(); 

        Assert.assertTrue(postRetrieved != null);
        Assert.assertTrue(question != null);
        Assert.assertTrue(answer != null);
        Assert.assertTrue(replies.size() == 1); 
        Assert.assertTrue(replies.get(0).getComment().equals("Nice dude")); 
        Assert.assertTrue(replies.get(0).getDisplayName().equals("Anonymous")); 
        Assert.assertTrue(question.getComment().equals("Why are you in office?")); 
        Assert.assertTrue(question.getDisplayName().equals("Anonymous")); 
        Assert.assertTrue(answer.getComment().equals("Because I want to be.")); 
        Assert.assertTrue(answer.getDisplayName().equals("Donald Trump")); 
    }

    @Test
    public void testQueryForPostEntityWithId() throws EntityNotFoundException{
        long commentId = DatastoreManager.insertCommentInDatastore("Anonymous", "Nice dude");
        List<Long> commentIds = new ArrayList<>(); 
        commentIds.add(commentId); 
        long commentIdQuestion = DatastoreManager.insertCommentInDatastore("Anonymous", "Why are you in office?");
        long commentIdAnswer = DatastoreManager.insertCommentInDatastore("Donald Trump", "Because I want to be.");
        Entity postEntity = new Entity(Constants.POST_ENTITY_TYPE); 
        postEntity.setProperty(Constants.POST_QUESTION, commentIdQuestion); 
        postEntity.setProperty(Constants.POST_ANSWER, commentIdAnswer); 
        postEntity.setProperty(Constants.POST_REPLIES, commentIds); 
        ds.put(postEntity); 

        long postId = postEntity.getKey().getId(); 
        Entity postEntityRetrived = DatastoreManager.queryForPostEntityWithId(postId);
        long questionIdActual = (long)(postEntityRetrived.getProperty(Constants.POST_QUESTION));
        long answerIdActual = (long)(postEntityRetrived.getProperty(Constants.POST_ANSWER));

        Assert.assertTrue(postEntityRetrived != null);
        Assert.assertTrue(postId == postEntityRetrived.getKey().getId()); 
        Assert.assertTrue(questionIdActual == commentIdQuestion);
        Assert.assertTrue(answerIdActual == commentIdAnswer);
    }
}
