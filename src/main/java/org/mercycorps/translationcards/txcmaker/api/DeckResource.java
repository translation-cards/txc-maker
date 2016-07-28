package org.mercycorps.translationcards.txcmaker.api;


import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.service.DeckService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/decks")
public class DeckResource {

    private final DeckService deckService;

    public DeckResource() {
        deckService = new DeckService();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Deck retrieveDeck() {
        return deckService.retrieve(1);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createDeck(Deck deck) {
        deckService.create(deck);
    }

}
