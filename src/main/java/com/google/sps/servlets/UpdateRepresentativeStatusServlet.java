package com.google.sps.servlets;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.sps.data.DatastoreManager;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** 
The UpdateRepresentativeStatus Servlet allows for the updating of the representative's status on the web page
at their discretion.
*/
@WebServlet ("/update_representative_status")
public class UpdateRepresentativeStatusServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger("UpdateRepresentativeStatusServlet");
    private static final String REP_NAME = "repName";
    private static final String REP_STATUS = "status";
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
        String repName = request.getParameter(REP_NAME);
        String status = request.getParameter(REP_STATUS);
        Entity representative;
        try {
            representative = DatastoreManager.queryForRepresentativeEntityWithName(repName); 
            long repId = representative.getKey().getId();      
            DatastoreManager.updateRepresentativeStatus(repId, status);  
        } catch(EntityNotFoundException e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
    }
}
