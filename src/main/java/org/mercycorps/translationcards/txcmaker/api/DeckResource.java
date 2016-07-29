package org.mercycorps.translationcards.txcmaker.api;


import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.service.DeckService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/decks")
public class DeckResource {

    private DeckService deckService;

    @Context
    ServletContext servletContext;

    private void init() {
        if(deckService == null) {
            AuthUtils authUtils = (AuthUtils) servletContext.getAttribute("authUtils");
            deckService = new DeckService(authUtils);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Deck retrieveDeck() {
        init();
        return deckService.retrieve(1);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createDeck(Deck deck) {
        init();
        deckService.create(deck);
    }

}
