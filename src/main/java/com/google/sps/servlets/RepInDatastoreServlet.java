package com.google.sps.servlets;

import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.QueryDatastore;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

/* 
The RepInDatastore Servlet class checks to see whether or not a particular
representative has made an account by querying for their name in datastore
*/

@WebServlet ("/rep_in_datastore")
public class RepInDatastoreServlet extends HttpServlet{

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        String repName = request.getParameter("repName");
        Entity representative = QueryDatastore.queryForRepresentative(repName);
        response.setContentType("text/html");
        response.getWriter().println(representative != null);
    }
}
