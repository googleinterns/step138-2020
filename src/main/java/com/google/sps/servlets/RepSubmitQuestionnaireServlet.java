package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key; 
import com.google.appengine.api.datastore.KeyFactory; 
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import com.google.sps.data.Constants;
import com.google.sps.data.DatastoreManager;
import com.google.sps.data.Post;
import com.google.sps.data.Representative;
import com.google.sps.data.DatastoreManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** 
RepSubmitQuestionnaireServlet updates the intro and tabs of the 
representative when they submit the questionnaire
*/

@WebServlet ("/rep_submit_questionnaire")
public class RepSubmitQuestionnaireServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger("RepSubmitQuestionnaireServlet");
    private static final String TOPIC_LIST = "topicList";
    private static final String PLATFORM_LIST = "platformList";
    private static final String INTRO = "intro";
    private static final String REP_NAME = "repName";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
        String repName = request.getParameter(REP_NAME);
        String topics = request.getParameter(TOPIC_LIST);
        String intro = request.getParameter(INTRO);

        List<String> topicList = new ArrayList<String>(Arrays.asList(topics.split(",")));
        topicList.replaceAll(s -> repName.replaceAll("\\s+","") + s);
        String platforms = request.getParameter(PLATFORM_LIST);
        List<String> platformList = new ArrayList<String>(Arrays.asList(platforms.split("\\*,")));
        List<Long> tabIds = DatastoreManager.insertTabsInDatastore(topicList, platformList);

        Entity rep;
        long repId;
        try {
            System.out.println("this is the repname: " + repName);
            rep = DatastoreManager.queryForRepresentativeEntityWithName(repName); 
            System.out.println(rep);
            repId = rep.getKey().getId(); 
            DatastoreManager.updateRepresentativeTabList(repId, tabIds);
            DatastoreManager.updateRepresentativeIntro(repId, intro);
        } 
        catch(EntityNotFoundException e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
    }
}
