package org.mercycorps.translationcards.txcmaker.api;

import org.mercycorps.translationcards.txcmaker.model.Deck;

import javax.ws.rs.core.Response;

public class RetrieveDeckResponse {
    private Deck deck = null;

    public Response build() {
        if (deck != null) {
            return success();
        } else {
            return failure();
        }
    }

    private Response failure() {
        return Response
                .status(Response.Status.NOT_FOUND)
                .build();
    }

    private Response success() {
        return Response
                .status(Response.Status.OK)
                .entity(this)
                .build();
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Deck getDeck() {
        return deck;
    }
}
