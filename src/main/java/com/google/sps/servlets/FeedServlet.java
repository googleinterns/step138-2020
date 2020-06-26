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
import com.google.sps.data.Parse;
import com.google.sps.data.QueryDatastore;
import com.google.sps.data.Post;
import com.google.sps.data.Representative;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet ("/feed")
public class FeedServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String repName = request.getParameter("rep_name");
        Entity repEntity = QueryDatastore.queryForRepresentative(repName); 
        if (repEntity == null) {
            System.out.println("Unable to query representative from datastore"); 
            System.exit(0);
        }

        Representative rep = null; 
        try {
            rep = Parse.parseRepresentative(repEntity); 
        } 
        catch(EntityNotFoundException e) {
            System.out.println("Unable to parse representative from datastore"); 
            System.exit(0);
        }
        response.setContentType("application/json;");
        response.getWriter().println(new Gson().toJson(rep));
    }
}
