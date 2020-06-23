package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Scanner;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import java.net.URISyntaxException;
import java.net.URI;
import org.apache.http.impl.client.HttpClients;
import com.google.gson.Gson;

@WebServlet ("/rep_list")
public class RepListServlet extends HttpServlet{

    private final String API_KEY = "AIzaSyAHQRFZpZBZSOeUI-4pmfoHV4SM0eBSlS4";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String zipcode = request.getParameter("zipcode");
        HttpClient httpclient = HttpClients.createDefault();

        URIBuilder builder = new URIBuilder();
        builder.setScheme("https").setHost("www.googleapis.com/civicinfo/v2/representatives")
        .setParameter("key", API_KEY)
        .setParameter("address", zipcode);
        URI uri = null;
        try{
            uri = builder.build();
        } catch(URISyntaxException e){
            //print some shit
        }
        HttpGet httpget = new HttpGet(uri);

        HttpResponse httpresponse = null;
        String responseString = null;

        try {
            httpresponse = httpclient.execute(httpget);
        } catch (IOException e) {
            //handle this IOException properly in the future
        } catch (Exception e) {
            //handle this IOException properly in the future
        }

        HttpEntity responseEntity = httpresponse.getEntity();
        if(responseEntity != null) {
            responseString = EntityUtils.toString(responseEntity);
        }
        
        System.out.println(responseString);
        String json = new Gson().toJson(responseString);
        response.setContentType("application/json");
        response.getWriter().println(json);


    }
}
