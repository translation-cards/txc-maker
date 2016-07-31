package org.mercycorps.translationcards.txcmaker.service;


import org.mercycorps.translationcards.txcmaker.api.response.CreateDeckResponse;
import org.mercycorps.translationcards.txcmaker.api.response.RetrieveDeckResponse;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.model.ImportDeckForm;

import java.util.Arrays;
import java.util.List;


public class DeckService {

    AuthUtils authUtils;

    public DeckService(AuthUtils authUtils) {
        this.authUtils = authUtils;
    }

    public void retrieve(int id, RetrieveDeckResponse retrieveDeckResponse) {
        if(id == 10) {
            retrieveDeckResponse.setDeck(Deck.stub());
        }
    }

    public void create(ImportDeckForm form, CreateDeckResponse createDeckResponse) {
        Deck deck = form.getDeck();
        String audioDirectoryId = form.getAudioDirectoryId();
        String documentId = form.getDocumentId();

        if ("deck with errors".equals(deck.deck_label)) {
            addFakeErrors(createDeckResponse);
        } else {
            createDeckResponse.setId(10);
        }
    }

    private void addFakeErrors(CreateDeckResponse createDeckResponse) {
        createDeckResponse.setId(-1);
        createDeckResponse.addError("Error 1");
        createDeckResponse.addWarning("Warning 1");
        createDeckResponse.addWarning("Warning 2");
    }

    public List<Deck> retrieveAll() {
        return Arrays.asList(Deck.stub());
    }
}
