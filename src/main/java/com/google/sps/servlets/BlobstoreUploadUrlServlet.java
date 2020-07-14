package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import java.net.URLEncoder; 
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Returns the uploadUrl for an image being uploaded to Blobstore
 */
@WebServlet("/blobstore-upload-url")
public class BlobstoreUploadUrlServlet extends HttpServlet {  
    private static final String REP_NAME = "repName";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String repName = request.getParameter(REP_NAME);
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        String uploadUrl = blobstoreService.createUploadUrl("/rep_upload_image?repName=" 
            + URLEncoder.encode(repName));
        response.setContentType("text/html");
        response.getWriter().println(uploadUrl);
    }
}
