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
 * Servlet for fetching a list of posts corresponding with a particular tab
 */ 
@WebServlet ("/tab_posts")
public class TabPostsServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger("TabPostsServlet");
    private static final String REP_NAME = "repName";
    private static final String TAB = "tab";
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
        String repName = request.getParameter(REP_NAME);
        String tab = request.getParameter(TAB);
        List<Post> postList = DatastoreManager.queryForPostListWithTab(repName, tab);
        response.setContentType("application/json;");
        response.getWriter().println(new Gson().toJson(postList));
    }
}
