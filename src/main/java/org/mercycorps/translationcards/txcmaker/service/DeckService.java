package org.mercycorps.translationcards.txcmaker.service;


import org.mercycorps.translationcards.txcmaker.api.response.CreateDeckResponse;
import org.mercycorps.translationcards.txcmaker.api.response.RetrieveDeckResponse;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Field;

import java.util.Arrays;
import java.util.List;


public class DeckService {


    public void retrieve(int id, RetrieveDeckResponse retrieveDeckResponse) {
        if(id == 10) {
            retrieveDeckResponse.setDeck(Deck.STUBBED_DECK);
        }
    }

    public void verifyFormData(CreateDeckResponse createDeckResponse, List<Field> fields) {
        for(Field field : fields) {
            createDeckResponse.addErrors(field.verify());
        }

        if (createDeckResponse.hasErrors()) {
            createDeckResponse.setId(-1);
        } else {
            createDeckResponse.setId(10);
        }
    }


    public List<Deck> retrieveAll() {
        return Arrays.asList(Deck.STUBBED_DECK);
    }
}
