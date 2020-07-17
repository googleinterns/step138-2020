package com.google.sps.data;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
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
import java.lang.UnsupportedOperationException; 
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class DatastoreManagerTest {
    private LocalServiceTestHelper helper;
    private DatastoreService ds; 

    @Before
    public void setUp() {
        helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();
        ds = DatastoreServiceFactory.getDatastoreService();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testInsertCommentInDatastore() 
    throws EntityNotFoundException{
        long commentId = DatastoreManager.insertCommentInDatastore("Anonymous", "Nice dude"); 
        Key commentEntityKey = KeyFactory.createKey(Constants.COMMENT_ENTITY_TYPE, commentId);
        Entity commentEntity = (Entity) ds.get(commentEntityKey);
        
        String name = (String) (commentEntity.getProperty(Constants.COMMENT_NAME));
        String msg = (String) (commentEntity.getProperty(Constants.COMMENT_MSG));

        assertTrue(name.equals("Anonymous")); 
        assertTrue(msg.equals("Nice dude")); 
    }

    @Test
    public void testInsertRepresentativeInDatastore() 
    throws EntityNotFoundException{
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", 
            "President of the US", "username", "password", 
            new ArrayList<Long> (Arrays.asList(Long.valueOf(1)))); 

        Key repEntityKey = KeyFactory.createKey(Constants.REP_ENTITY_TYPE, repId);
        Entity repEntity = (Entity) ds.get(repEntityKey);

        String name = (String) (repEntity.getProperty(Constants.REP_NAME));
        String title = (String) (repEntity.getProperty(Constants.REP_TITLE));
        String username = (String) (repEntity.getProperty(Constants.REP_USERNAME));
        String password = (String) (repEntity.getProperty(Constants.REP_PASSWORD));

        assertTrue(name.equals("Donald Trump")); 
        assertTrue(title.equals("President of the US")); 
        assertTrue(username.equals("username"));
        assertTrue(password.equals("password"));
    }

    @Test 
    public void testInsertPostInDatastore() 
    throws EntityNotFoundException {
        long questionId = DatastoreManager.insertCommentInDatastore("Anonymous", 
            "Why are you president?"); 
        long postId = DatastoreManager.insertPostInDatastore(questionId, "Education"); 
        Key postEntityKey = KeyFactory.createKey(Constants.POST_ENTITY_TYPE, postId); 
        Entity postEntity = (Entity) ds.get(postEntityKey); 

        long questionIdRetrieved = (long) (postEntity.getProperty(Constants.POST_QUESTION)); 
        long answerIdRetrieved = (long) (postEntity.getProperty(Constants.POST_ANSWER)); 
        Key questionEntityKey = KeyFactory.createKey
            (Constants.COMMENT_ENTITY_TYPE, questionIdRetrieved); 
        Entity questionEntity = (Entity) ds.get(questionEntityKey); 
        String name = (String) (questionEntity.getProperty(Constants.COMMENT_NAME));
        String msg = (String) (questionEntity.getProperty(Constants.COMMENT_MSG));

        assertTrue(answerIdRetrieved == -1); 
        assertTrue(name.equals("Anonymous")); 
        assertTrue(msg.equals("Why are you president?")); 
    }

    @Test
    public void testUpdateRepresentativePostList() 
    throws EntityNotFoundException{
        List<Long> tabIds = DatastoreManager.insertTabsInDatastore(
                new ArrayList<String> (Arrays.asList("Other")), 
                new ArrayList<String> (Arrays.asList("")));
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", 
            "President of the US", "username", "password", tabIds);
        long questionId = DatastoreManager.insertCommentInDatastore("Anonymous", 
            "Why are you president?"); 
        long postId = DatastoreManager.insertPostInDatastore(questionId, "Education"); 
        
        DatastoreManager.updateRepresentativePostList(repId, postId); 
        Representative rep = DatastoreManager.queryForRepresentativeObjectWithName("Donald Trump"); 
        List<Post> posts = rep.getPosts(); 
        Post post = posts.get(0); 

        assertTrue(posts.size() == 1); 
        assertTrue(post.getQuestion().getDisplayName().equals("Anonymous")); 
        assertTrue(post.getQuestion().getComment().equals("Why are you president?")); 
    }

    @Test
    public void testUpdatePostWithComment() 
    throws EntityNotFoundException{
        long questionId = DatastoreManager.insertCommentInDatastore("Anonymous", 
            "Why are you president?"); 
        long postId = DatastoreManager.insertPostInDatastore(questionId, "Education"); 
        long commentId = DatastoreManager.insertCommentInDatastore("Anonymous", "Nice dude."); 

        DatastoreManager.updatePostWithComment(postId, commentId);
        Post postRetrived = DatastoreManager.queryForPostObjectWithId(postId);
        List<Comment> replies = postRetrived.getReplies(); 

        assertTrue(replies.size() == 1); 
        assertTrue(replies.get(0).getDisplayName().equals("Anonymous")); 
        assertTrue(replies.get(0).getComment().equals("Nice dude.")); 
    }

    @Test
    public void testUpdatePostWithAnswer() 
    throws EntityNotFoundException {
        long questionId = DatastoreManager.insertCommentInDatastore("Anonymous", 
            "Why are you president?"); 
        long postId = DatastoreManager.insertPostInDatastore(questionId, "Education"); 
        long answerId = DatastoreManager.insertCommentInDatastore("Donald Trump", 
            "Because I want to."); 

        DatastoreManager.updatePostWithAnswer(postId, answerId);
        Post postRetrieved = DatastoreManager.queryForPostObjectWithId(postId);
        Comment answer = postRetrieved.getAnswer(); 

        assertTrue(answer.getDisplayName().equals("Donald Trump")); 
        assertTrue(answer.getComment().equals("Because I want to.")); 
    }

    @Test
    public void testQueryForRepresentativeObjectWithName() 
    throws EntityNotFoundException {
        List<Long> tabIds = DatastoreManager.insertTabsInDatastore(
            Arrays.asList("Other"), Arrays.asList(""));
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", 
            "President of the US", "username", "password", tabIds);
        
        Representative actualRep = DatastoreManager.
            queryForRepresentativeObjectWithName("Donald Trump");

        assertTrue(actualRep.getName().equals("Donald Trump"));
        assertTrue(actualRep.getTitle().equals("President of the US"));
        assertTrue(actualRep.getUsername().equals("username"));
        assertTrue(actualRep.getPassword().equals("password"));
    }

    @Test
    public void testQueryForRepresentativeEntityWithName() 
    throws EntityNotFoundException{
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", 
            "President of the US", "username", "password",
            new ArrayList<Long> (Arrays.asList(Long.valueOf(1))));
    
        Entity repEntity = DatastoreManager.queryForRepresentativeEntityWithName("Donald Trump");
        String name = (String) repEntity.getProperty(Constants.REP_NAME); 
        String title = (String) repEntity.getProperty(Constants.REP_TITLE); 
        
        assertTrue(name.equals("Donald Trump")); 
        assertTrue(title.equals("President of the US")); 
    }

    @Test
    public void testQueryForPostObjectWithId() 
    throws EntityNotFoundException {
        long commentId = DatastoreManager.insertCommentInDatastore("Anonymous", "Nice dude");
        List<Long> commentIds = new ArrayList<>(); 
        commentIds.add(commentId); 
        long commentIdQuestion = DatastoreManager.insertCommentInDatastore("Anonymous", 
            "Why are you in office?");
        long commentIdAnswer = DatastoreManager.insertCommentInDatastore("Donald Trump", 
            "Because I want to be.");
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

        assertTrue(postRetrieved != null);
        assertTrue(question != null);
        assertTrue(answer != null);
        assertTrue(replies.size() == 1); 
        assertTrue(replies.get(0).getComment().equals("Nice dude")); 
        assertTrue(replies.get(0).getDisplayName().equals("Anonymous")); 
        assertTrue(question.getComment().equals("Why are you in office?")); 
        assertTrue(question.getDisplayName().equals("Anonymous")); 
        assertTrue(answer.getComment().equals("Because I want to be.")); 
        assertTrue(answer.getDisplayName().equals("Donald Trump")); 
    }

    @Test
    public void testQueryForPostEntityWithId() 
    throws EntityNotFoundException{
        long commentId = DatastoreManager.insertCommentInDatastore("Anonymous", "Nice dude");
        List<Long> commentIds = new ArrayList<>(); 
        commentIds.add(commentId); 
        long commentIdQuestion = DatastoreManager.insertCommentInDatastore("Anonymous", 
            "Why are you in office?");
        long commentIdAnswer = DatastoreManager.insertCommentInDatastore("Donald Trump", 
            "Because I want to be.");
        Entity postEntity = new Entity(Constants.POST_ENTITY_TYPE); 
        postEntity.setProperty(Constants.POST_QUESTION, commentIdQuestion); 
        postEntity.setProperty(Constants.POST_ANSWER, commentIdAnswer); 
        postEntity.setProperty(Constants.POST_REPLIES, commentIds); 
        ds.put(postEntity); 

        long postId = postEntity.getKey().getId(); 
        Entity postEntityRetrived = DatastoreManager.queryForPostEntityWithId(postId);
        long questionIdActual = (long)(postEntityRetrived.getProperty(Constants.POST_QUESTION));
        long answerIdActual = (long)(postEntityRetrived.getProperty(Constants.POST_ANSWER));

        assertTrue(postEntityRetrived != null);
        assertTrue(postId == postEntityRetrived.getKey().getId()); 
        assertTrue(questionIdActual == commentIdQuestion);
        assertTrue(answerIdActual == commentIdAnswer);
    }

    @Test
    public void testAddReactionToPost() 
    throws EntityNotFoundException{
        long commentIdQuestion = DatastoreManager.insertCommentInDatastore
            ("Anonymous", "Why are you in office?");
        long postId = DatastoreManager.insertPostInDatastore(commentIdQuestion, "education"); 

        DatastoreManager.addReactionToPost(postId, Reaction.THUMBS_UP.getValue()); 
        Post post = DatastoreManager.queryForPostObjectWithId(postId); 
        long reactionCount = post.getReactions().get(Reaction.THUMBS_UP); 

        assertTrue(reactionCount == 1);
    }

    @Test
    public void testRemoveReactionFromPostWithZeroReactions() 
    throws EntityNotFoundException {
        long commentIdQuestion = DatastoreManager.insertCommentInDatastore
            ("Anonymous", "Why are you in office?");
        long postId = DatastoreManager.insertPostInDatastore(commentIdQuestion, "education"); 

        assertThrows(UnsupportedOperationException.class, () -> {
            DatastoreManager.removeReactionFromPost(postId, Reaction.THUMBS_UP.getValue()); 
        });
    }

    @Test
    public void testRemoveReactionFromPostWithOneReaction() 
    throws EntityNotFoundException{
        long commentIdQuestion = DatastoreManager.insertCommentInDatastore
            ("Anonymous", "Why are you in office?");
        long postId = DatastoreManager.insertPostInDatastore(commentIdQuestion, "education"); 
        DatastoreManager.addReactionToPost(postId, Reaction.THUMBS_UP.getValue()); 

        DatastoreManager.removeReactionFromPost(postId, Reaction.THUMBS_UP.getValue()); 
        Post post = DatastoreManager.queryForPostObjectWithId(postId); 
        long reactionCount = post.getReactions().get(Reaction.THUMBS_UP); 

        assertTrue(reactionCount == 0);
    }

    @Test
    public void testqueryForTabListWithRepName() 
    throws EntityNotFoundException {
        List<Long> tabIds = DatastoreManager.insertTabsInDatastore(
            Arrays.asList("Education", "Police"), 
            Arrays.asList("Platform on education", "Platform on police"));
        Tab tab1 = new Tab("Education", "Platform on education", tabIds.get(0));
        Tab tab2 = new Tab("Police", "Platform on police", tabIds.get(1));
        List<Tab> tabList = new ArrayList<Tab> (Arrays.asList(tab1, tab2));
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", 
        "President", "username", "password",
        new ArrayList<Long> (Arrays.asList(Long.valueOf(1))));
        DatastoreManager.updateRepresentativeTabList(repId, tabIds);

        List<Tab> tabListActual = DatastoreManager.queryForTabListWithRepName("Donald Trump");

        assertTrue(tabListActual.equals(tabList));
    }

    @Test
    public void testqueryForTabEntityWithName() throws EntityNotFoundException {
        Entity tabEntity = new Entity(Constants.TAB_ENTITY_TYPE); 
        tabEntity.setProperty(Constants.TAB_NAME, "Education");
        tabEntity.setProperty(Constants.TAB_PLATFORM, "Platform");
        ds.put(tabEntity);

        Entity actualEntity = DatastoreManager.queryForTabEntityWithName("Education");

        assertTrue(tabEntity.equals(actualEntity));
    }

    @Test
    public void testInsertTabsInDatastore() 
    throws EntityNotFoundException{
        List<Long> tabIds = DatastoreManager.insertTabsInDatastore(
            Arrays.asList("Education", "Police"), 
            Arrays.asList("Platform on education", "Platform on police")); 
        List<Long> newTabIds = DatastoreManager.insertTabsInDatastore(
            Arrays.asList("Education"), 
            Arrays.asList("New platform on education")); 
        Key tabEntityKey1 = KeyFactory.createKey(Constants.TAB_ENTITY_TYPE, tabIds.get(0));
        Entity tabEntity1 = (Entity) ds.get(tabEntityKey1);
        Key tabEntityKey2 = KeyFactory.createKey(Constants.TAB_ENTITY_TYPE, tabIds.get(1));
        Entity tabEntity2 = (Entity) ds.get(tabEntityKey2);
        
        String name1 = (String) (tabEntity1.getProperty(Constants.TAB_NAME));
        String platform1 = (String) (tabEntity1.getProperty(Constants.TAB_PLATFORM));
        String name2 = (String) (tabEntity2.getProperty(Constants.TAB_NAME));
        String platform2 = (String) (tabEntity2.getProperty(Constants.TAB_PLATFORM));

        assertTrue(name1.equals("Education")); 
        assertTrue(name2.equals("Police"));         
        assertTrue(platform1.equals("New platform on education")); 
        assertTrue(platform2.equals("Platform on police"));
    }

    @Test
    public void testUpdateRepresentativeTabList() 
    throws EntityNotFoundException{
        List<Long> tabIds = DatastoreManager.insertTabsInDatastore(
            Arrays.asList("Education", "Police"), 
            Arrays.asList("Platform on education", "Platform on police")); 
        Tab tab1 = new Tab("Education", "Platform on education", tabIds.get(0));
        Tab tab2 = new Tab("Police", "Platform on police", tabIds.get(1));
        List<Tab> tabList = new ArrayList<Tab> (Arrays.asList(tab1, tab2));
        long repId = DatastoreManager.insertRepresentativeInDatastore("Donald Trump", 
            "President of the US", "username", "password", tabIds);

        DatastoreManager.updateRepresentativeTabList(repId, tabIds);
        List<Tab> actualTabList = DatastoreManager.queryForTabListWithRepName("Donald Trump");

        assertTrue(tabList.equals(actualTabList));
    }
}
