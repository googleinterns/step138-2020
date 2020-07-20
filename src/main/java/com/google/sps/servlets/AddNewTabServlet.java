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
import java.net.URLEncoder; 
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** 
InsertRepDatastoreServlet currently inserts the representative Donald
Trump into the datastore as a hard code for the MVP.
*/

@WebServlet ("/add_new_tab")
public class AddNewTabServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger("AddNewTabServlet");
    private static final String NEW_TAB_NAME = "tabName";
    private static final String PLATFORM = "platform";
    private static final String POSTS = "posts";
    private static final String REP_NAME = "repName";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
        String newTabName = request.getParameter(NEW_TAB_NAME);
        String platform = request.getParameter(PLATFORM);
        String postIdsString = request.getParameter(POSTS);
        String repName = request.getParameter(REP_NAME);
        List<String> postIds = Arrays.asList(postIdsString.split(","));

        List<Long> newTabId;
        Entity repEntity;
        try {
            repEntity = DatastoreManager.queryForRepresentativeEntityWithName(repName);
            Long repId = repEntity.getKey().getId();
            newTabId = DatastoreManager.insertTabsInDatastore(Arrays.asList(repName.replaceAll("\\s+","") + newTabName), Arrays.asList(platform)); 
            DatastoreManager.updateRepresentativeTabList(repId, newTabId);
            for (String postId : postIds) {
                DatastoreManager.updatePostTab(Long.parseLong(postId), repName.replaceAll("\\s+","") + newTabName);
            }
        } 
        catch(EntityNotFoundException e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
    }
}
