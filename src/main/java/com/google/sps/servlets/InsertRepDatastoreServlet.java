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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

@WebServlet ("/insert_rep_datastore")
public class InsertRepDatastoreServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String name = "Donald J. Trump";
        String title = "President of the United States";
        Entity rep = null;
        try {
            rep = DatastoreManager.queryForRepresentativeEntityWithName(name); 
        } 
        catch(EntityNotFoundException e) {
            Constants.logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
        if (rep == null){
            DatastoreManager.insertRepresentativeInDatastore(name, title);
        }
    }
}
