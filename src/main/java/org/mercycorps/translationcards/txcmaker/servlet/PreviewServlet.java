package org.mercycorps.translationcards.txcmaker.servlet;

import com.google.appengine.repackaged.com.google.gson.Gson;
import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.Deck;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PreviewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Deck deck = createStubbedDeck();
        String json = new Gson().toJson(deck);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);

    }

    private Deck createStubbedDeck() {
        List<String> languages = Arrays.asList("ar", "fa", "ps");
        List<String> phrases = Arrays.asList(
                "Do you understand this language?",
                "Can I talk to you, using this mobile application (App)?",
                "What is your name?",
                "Do you need water?",
                "Do you have a phone?",
                "Do you need medical attention?",
                "Where do you come from?");

        Deck deck = new Deck()
                .setDeckId("1234")
                .setDeckLabel("Default Deck")
                .setPublisher("Women Hack Syria")
                .setLanguage("en")
                .setLocked(false)
                .setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .setTimestamp(System.currentTimeMillis());

        for(String language : languages) {
            for(String phrase : phrases) {
                deck.addCard(language, new Card()
                        .setLabel(phrase)
                        .setTranslationText(language + " translation")
                        .setFilename(phrase + ".mp3"));
            }
        }
        return deck;
    }
}
