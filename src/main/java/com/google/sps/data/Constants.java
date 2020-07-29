package com.google.sps.data;

public final class Constants {
    private static final double EPSILON = 0.00001;
    public static final long[] REACTION_DELTA_BUCKETS = 
        new long[]{25, 50, 75, 100}; 
    public static final double[] REACTION_BUCKET_BOOSTS = 
        new double[]{0.0, 0.05, 0.1, 0.15, 0.2}; 

    public static final String REP_ENTITY_TYPE = "Representative";
    public static final String REP_NAME = "Name";
    public static final String REP_TITLE = "Official Title";
    public static final String REP_USERNAME = "Username";
    public static final String REP_PASSWORD = "Password";
    public static final String REP_POSTS = "Posts";
    public static final String REP_INTRO = "Intro";
    public static final String REP_BLOB_KEY_URL = "BlobKeyUrl";
    public static final String REP_TABS = "Tabs";

    public static final String PERSPECTIVE_API_KEY = "PERSPECTIVE_API_KEY";
    public static final String POST_ENTITY_TYPE = "Post";
    public static final String POST_QUESTION = "Question";
    public static final String POST_ANSWER = "Answer";
    public static final String POST_REPLIES = "Replies";
    public static final String POST_TABS = "Tabs";
    public static final String POST_TIMESTAMP = "Timestamp";

    public static final String TAB_ENTITY_TYPE = "Tab";
    public static final String TAB_NAME = "Name";
    public static final String TAB_PLATFORM = "Platform";

    public static final String COMMENT_ENTITY_TYPE = "Comment";
    public static final String COMMENT_NAME = "Nick Name";
    public static final String COMMENT_MSG = "Message";
    public static final double COMMENT_TOXICITY_THRESHOLD = 0.7;
    public static final String CIVIC_API_KEY = "CIVIC_API_KEY";
    public static final String CIVIC_API_ENDPOINT = 
        "www.googleapis.com/civicinfo/v2/representatives";
    public static final String DEFAULT_POLITICIAN_IMAGE_URL = 
        "/images/defaultProfilePicture.png";

    private Constants() {}
}
