package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.sps.data.Constants;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/* 
The RepListServlet class takes in an address from the user and call the Google Civic
Info API to pull the list of representatives relevant for that zipcode and returns
a json formatted objects which contains corresponding offices and officials
*/

@WebServlet ("/rep_list")
public class RepresentativeListServlet extends HttpServlet{
    Dotenv dotenv = Dotenv.load();
    private final String API_KEY = dotenv.get("CIVIC_API_KEY");

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        String zipcode = request.getParameter("zipcode");
        HttpClient httpclient = HttpClients.createDefault();
        URIBuilder builder = new URIBuilder();
        URI uri = null;

        builder.setScheme("https").setHost("www.googleapis.com/civicinfo/v2/representatives")
        .setParameter("key", API_KEY)
        .setParameter("address", zipcode);
        try{
            uri = builder.build();
        } catch(URISyntaxException e){
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
        HttpGet httpget = new HttpGet(uri);
        HttpResponse httpresponse = null;
        String responseString = null;

        try{
            httpresponse = httpclient.execute(httpget);
        } catch (IOException e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }

        HttpEntity responseEntity = httpresponse.getEntity();
        
        if(responseEntity != null) {
            responseString = EntityUtils.toString(responseEntity);
        }
        else{
            logger.debug("Response entity was null.");
        }

        String json = new Gson().toJson(responseString);
        response.setContentType("application/json");
        response.getWriter().println(json);
    }
}
