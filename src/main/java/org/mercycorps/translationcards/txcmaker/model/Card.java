package org.mercycorps.translationcards.txcmaker.model;

import org.mercycorps.translationcards.txcmaker.model.deck.RequiredString;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Card {

    public final static Error NO_LABEL = new Error("This card has no label", true);
    public final static Error NO_AUDIO = new Error("This card has no audio recording", true);
    public final static Error NO_TEXT = new Error("This card has no text translation", false);

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
                        new RequiredString(card_label, NO_LABEL),
                        new RequiredString(dest_audio, NO_AUDIO),
                        new RequiredString(dest_txt, NO_TEXT)
        );

        errors = new ArrayList<>();

        for(Field field : fieldsToVerify) {
            errors.addAll(field.verify());
        }

        return errors;
    }
}
