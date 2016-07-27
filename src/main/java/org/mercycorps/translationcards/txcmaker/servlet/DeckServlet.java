package org.mercycorps.translationcards.txcmaker.servlet;

import com.google.appengine.repackaged.com.google.gson.Gson;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.service.DeckService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeckServlet extends HttpServlet {

    private DeckService deckService;

    @Override
    public void init() throws ServletException {
        super.init();
        deckService = new DeckService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Deck deck = deckService.get(id);
        String json = new Gson().toJson(deck);
        writeJsonResponse(resp, json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        deckService.loadFromRequest(req);
        String json = new Gson().toJson("Deck loaded");
        writeJsonResponse(resp, json);
    }

    private void writeJsonResponse(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }
}
