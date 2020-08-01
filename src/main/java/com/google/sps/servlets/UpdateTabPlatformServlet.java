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
import com.google.sps.data.DatastoreManager;
import com.google.sps.data.Post;
import com.google.sps.data.Representative;
import java.io.IOException;
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
The UpdateTabPlatform Servlet class updates the platform for a tab
*/
@WebServlet ("/update_tab_platform")
public class UpdateTabPlatformServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger("UpdateTabPlatformServlet");
    private static final String TAB_NAME = "tabName";
    private static final String PLATFORM = "platform";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
        String tabName = request.getParameter(TAB_NAME);
        String platform = request.getParameter(PLATFORM);
        Entity tabEntity;
        long tabId;
        try {
            tabEntity = DatastoreManager.queryForTabEntityWithName(tabName);
            tabId = tabEntity.getKey().getId();
            DatastoreManager.updateTabPlatform(tabId, platform);        
        } catch(EntityNotFoundException e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
    }
}
