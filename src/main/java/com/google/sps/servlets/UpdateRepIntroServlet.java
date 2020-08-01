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
The UpdateRepIntro Servlet class updates the intro for a representative
*/
@WebServlet ("/update_rep_intro")
public class UpdateRepIntroServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger("UpdateRepIntroServlet");
    private static final String REP_NAME = "repName";
    private static final String INTRO = "intro";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
        String repName = request.getParameter(REP_NAME);
        String intro = request.getParameter(INTRO);
        Entity repEntity;
        long repId;
        try {
            repEntity = DatastoreManager.queryForRepresentativeEntityWithName(repName);
            repId = repEntity.getKey().getId();
            DatastoreManager.updateRepresentativeIntro(repId, intro);        
        } catch(EntityNotFoundException e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
    }
}
