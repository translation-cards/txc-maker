package org.mercycorps.translationcards.txcmaker.model.deck;

import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Field;

import java.util.ArrayList;
import java.util.List;

public class RequiredString implements Field {

    private String value;
    private String key;
    private String message;
    private final boolean fatal;

    public RequiredString(String value, String key, String message, boolean fatal) {
        this.value = value;
        this.key = key;
        this.message = message;
        this.fatal = fatal;
    }

    @Override
    public List<Error> verify() {
        List<Error> errors = new ArrayList<>();

        if(value == null || value.equals("")) {
            errors.add(new Error(key, message, fatal));
        }

        return errors;
    }
}
