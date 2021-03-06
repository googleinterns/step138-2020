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

/**
 * Servlet for fetching a representative object used to create the feed page 
 * for a particular representative 
 */ 
@WebServlet ("/get_rep_object")
public class GetRepresentativeObjectServlet extends HttpServlet {
    private static final String REP_NAME = "repName";
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
        String repName = request.getParameter(REP_NAME);
        Representative rep = DatastoreManager.queryForRepresentativeObjectWithName(repName); 
        if (rep == null) {
            throw new ServletException("Rep was not found in the datastore"); 
        }
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().println(new Gson().toJson(rep));
    }
}
