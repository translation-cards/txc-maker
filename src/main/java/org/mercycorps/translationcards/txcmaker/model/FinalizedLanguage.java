package org.mercycorps.translationcards.txcmaker.model;

import java.util.ArrayList;
import java.util.List;

public class FinalizedLanguage {
    
    public String iso_code;
    public List<FinalizedCard> cards;
    
    public FinalizedLanguage() {
        cards = new ArrayList<>();
    }

    public FinalizedLanguage setIso_code(String iso_code) {
        this.iso_code = iso_code;
        return this;
    }

    public FinalizedLanguage setCards(List<FinalizedCard> cards) {
        this.cards = cards;
        return this;
    }
}
