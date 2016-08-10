package org.mercycorps.translationcards.txcmaker.model.importDeckForm;

import org.mercycorps.translationcards.txcmaker.model.Error;

import java.util.ArrayList;
import java.util.List;

public class Publisher implements Field {
    public static final String FIELD_NAME = "publisher";
    public static final String REQUIRED_FIELD_MESSAGE = "Publisher is a required field.";
    public static final Error REQUIRED_FIELD = new Error(FIELD_NAME, REQUIRED_FIELD_MESSAGE);
    private String publisherString;

    public Publisher(String publisherString) {
        this.publisherString = publisherString;
    }

    @Override
    public List<Error> verify() {
        List<Error> errors = new ArrayList<>();
        if(publisherString == null) {
            errors.add(REQUIRED_FIELD);
        }
        return errors;
    }
}
