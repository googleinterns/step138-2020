package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet("/rep_upload_image")
public class RepUploadImageServlet extends HttpServlet{
    private static final Logger logger = LogManager.getLogger("RepUploadImageServlet");
    private static final String IMAGE_UPLOAD = "imageUpload";
    private static final String REP_NAME = "repName";
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException{
        String blobKeyUrl = getBlobKeyUrl(request, IMAGE_UPLOAD);
        String repName = request.getParameter(REP_NAME);
        Entity rep;
        long repId;
        try {
            rep = DatastoreManager.queryForRepresentativeEntityWithName(repName); 
            repId = rep.getKey().getId(); 
            DatastoreManager.updateRepresentativeImage(repId, blobKeyUrl);
        } catch(EntityNotFoundException e) {
            logger.error(e);
            throw new ServletException("Error: " + e.getMessage(), e);
        }
        response.sendRedirect("/loginRep.html");
    }

    private String getBlobKeyUrl(HttpServletRequest request, String formInputElementName) {
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
        List<BlobKey> blobKeys = blobs.get(formInputElementName);

        if (blobKeys == null || blobKeys.isEmpty()) {
            return("/images/defaultProfilePicture.png");
        } else {
            return ("/serve_blob?blobKey=" + blobKeys.get(0).getKeyString());
        }
    }
}
