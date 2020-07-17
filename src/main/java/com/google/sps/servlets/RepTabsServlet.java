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
import com.google.sps.data.Tab;
import com.google.sps.data.DatastoreManager;
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
The RepInDatastore Servlet class checks to see whether or not a particular
representative has made an account by querying for their name in datastore
*/
@WebServlet ("/rep_tabs")
public class RepTabsServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger("RepTabsServlet");
    private static final String REP_NAME = "repName";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
        String repName = request.getParameter(REP_NAME);
        List<Tab> tabList;
        try {
            tabList = DatastoreManager.queryForTabListWithRepName(repName);
        } catch (Exception e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().println(new Gson().toJson(tabList));
    }
}
