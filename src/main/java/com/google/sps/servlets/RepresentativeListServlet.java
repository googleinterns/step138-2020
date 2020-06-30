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

/** 
The RepresentativeListServlet class takes in an address from the user and call the Google Civic
Info API to pull the list of representatives relevant for that zipcode and returns
a json formatted objects which contains corresponding offices and officials
*/

@WebServlet ("/rep_list")
public class RepresentativeListServlet extends HttpServlet{
    private final String API_KEY;
    private static final Logger logger = LogManager.getLogger("RepresentativeListServlet");
    private static final String ZIPCODE = "zipcode";

    public RepresentativeListServlet() {
        API_KEY = Dotenv.load().get(Constants.CIVIC_API_KEY);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException{
        String zipcode = request.getParameter(ZIPCODE);
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
        HttpGet httpGet = new HttpGet(uri);
        String responseString = null;
        HttpResponse httpResponse = null;
        try{
            httpResponse = httpclient.execute(httpGet);
        } catch (IOException e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }

        HttpEntity responseEntity = httpResponse.getEntity();

        if(responseEntity != null) {
            responseString = EntityUtils.toString(responseEntity);
        }
        else{
            throw new ServletException("Could not get response from Civic Info API");
        }
        String json = new Gson().toJson(responseString);
        response.setContentType("application/json");
        response.getWriter().println(json);
    }
}
