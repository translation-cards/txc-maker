package org.mercycorps.translationcards.txcmaker.service;


import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;


public class DeckService {

    AuthUtils authUtils;

    public DeckService(AuthUtils authUtils) {
        this.authUtils = authUtils;
    }

    public Deck retrieve(int id) {
        return Deck.stub();
    }

    public int create(Deck deck) {
        return 10;
    }
}
