package org.mercycorps.translationcards.txcmaker.servlet;

import com.google.appengine.repackaged.com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ImportServlet extends HttpServlet {

    class Response {
        boolean success;
        String message;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Response response = new Response();
        response.success = true;
        response.message = "This endpoint is stubbed to always return as successful.";
        String json = new Gson().toJson(response);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }
}
