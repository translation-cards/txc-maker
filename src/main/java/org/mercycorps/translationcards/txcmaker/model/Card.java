package org.mercycorps.translationcards.txcmaker.model;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Card {

    public String card_label;
    public String dest_audio;
    public String dest_txt;
    public List<Error> errors;

    public Card() {
        errors = newArrayList();
    }

    public Card setLabel(String label) {
        this.card_label = label;
        return this;
    }

    public Card setFilename(String filename) {
        this.dest_audio = filename;
        return this;
    }

    public Card setTranslationText(String translationText) {
        this.dest_txt = translationText;
        return this;
    }
}
