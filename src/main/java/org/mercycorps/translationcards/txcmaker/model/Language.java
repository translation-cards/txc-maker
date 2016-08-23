package org.mercycorps.translationcards.txcmaker.model;

import java.util.ArrayList;
import java.util.List;

public class Language {

    public String iso_code;
    public String language_label;
    public List<Card> cards;

    public Language() {
        iso_code = "";
        language_label = "";
        cards = new ArrayList<>();
    }

    public Language(String isoCode, String language_label) {
        this.iso_code = isoCode;
        this.language_label = language_label;
        cards = new ArrayList<>();
    }

    public Language addCard(Card card) {
        cards.add(card);
        return this;
    }
}
