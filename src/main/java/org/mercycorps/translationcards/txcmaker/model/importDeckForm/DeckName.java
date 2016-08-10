package org.mercycorps.translationcards.txcmaker.model.importDeckForm;

import org.mercycorps.translationcards.txcmaker.model.Error;

import java.util.ArrayList;
import java.util.List;

public class DeckName implements Field {
    public static final String FIELD_NAME = "deckName";
    public static final String REQUIRED_FIELD_MESSAGE = "Deck Name is a required field.";
    public static final Error REQUIRED_FIELD = new Error(FIELD_NAME, REQUIRED_FIELD_MESSAGE);
    private String deckNameString;

    public DeckName(String deckNameString) {
        this.deckNameString = deckNameString;
    }

    @Override
    public List<Error> verify() {
        List<Error> errors = new ArrayList<>();
        if(deckNameString == null) {
            errors.add(REQUIRED_FIELD);
        }
        return errors;
    }
}
