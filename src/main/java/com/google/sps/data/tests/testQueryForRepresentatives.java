// import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
// import static org.junit.Assert.assertEquals;

// import com.google.appengine.api.datastore.DatastoreService;
// import com.google.appengine.api.datastore.DatastoreServiceFactory;
// import com.google.appengine.api.datastore.Entity;
// import com.google.appengine.api.datastore.EntityNotFoundException;
// import com.google.appengine.api.datastore.PreparedQuery;
// import com.google.appengine.api.datastore.Query;
// import com.google.appengine.api.datastore.Key;
// import com.google.appengine.api.datastore.KeyFactory;
// import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
// import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
// import com.google.sps.data.Comment;
// import com.google.sps.data.Parse;
// import com.google.sps.data.Post;
// import com.google.sps.data.Representative;
// import java.util.ArrayList;
// import java.util.List;
// import org.junit.Assert;
// import org.junit.After;
// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.junit.runners.JUnit4;

// @RunWith(JUnit4.class)
// public class testQueryForRepresentatives {
//     private static final String REP_ENTITY_TYPE = "Representative";
//     private static final String REP_NAME = "Name";
//     private static final String REP_TITLE = "Official Title";
//     private static final String REP_POSTS = "Posts";

//     private static final String POST_ENTITY_TYPE = "Post";
//     private static final String POST_QUESTION = "Question";
//     private static final String POST_ANSWER = "Answer";
//     private static final String POST_REPLIES = "Replies";

//     private static final String COMMENT_ENTITY_TYPE = "Comment";
//     private static final String COMMENT_NAME = "Nick Name";
//     private static final String COMMENT_MSG = "Message";

//     private final LocalServiceTestHelper helper =
//         new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

//     @Before
//     public void setUp() {
//         helper.setUp();
//     }

//     @After
//     public void tearDown() {
//         helper.tearDown();
//     }

//     @Test
//     // Run this test twice to prove we're not leaking any state across tests.
//     private void testQueryForRepresentatives() {
//         DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
//         Entity repEntity = new Entity(REP_ENTITY_TYPE);
//             taskEntity.setProperty(REP_NAME, "Donald Trump");
//             taskEntity.setProperty(REP_TITLE, "President of the United States");
//             taskEntity.setProperty(REP_POSTS, null);
//         ds.put(repEntity);

//         Entity trueEntity = Parse.queryForRepresentative("Donald Trump");
        
//         assertEquals(repEntity, trueEntity);
        
//     }
// }