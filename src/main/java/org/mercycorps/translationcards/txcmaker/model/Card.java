package org.mercycorps.translationcards.txcmaker.model;

import org.mercycorps.translationcards.txcmaker.model.deck.RequiredString;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Card {

    public String card_label;
    public String dest_audio;
    public String dest_txt;
    List<Error> errors;

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

    public List<Error> verify() {
        List<Field> fieldsToVerify = Arrays.asList((Field)
                        new RequiredString(card_label, "card_label", "This card has no label"),
                        new RequiredString(dest_audio, "dest_audio", "This card has no audio recording"),
                        new RequiredString(dest_txt, "dest_txt", "This card has no text translation")
        );

        errors = new ArrayList<>();

        for(Field field : fieldsToVerify) {
            errors.addAll(field.verify());
        }

        return errors;
    }
}
