package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet ("/new_comment")
public class NewCommentServlet extends HttpServlet{    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        long post_id = (long) request.getParameter("post_id");
        String nick_name = request.getParameter("nick_name");
        String comment = request.getParameter("comment");
        
    }
}
