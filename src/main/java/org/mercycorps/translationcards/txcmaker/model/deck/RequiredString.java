package org.mercycorps.translationcards.txcmaker.model.deck;

import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Constraint;

import java.util.ArrayList;
import java.util.List;

public class RequiredString implements Constraint {

    private final String value;
    private final Error error;

    public RequiredString(String value, Error error) {
        this.value = value;
        this.error = error;
    }

    @Override
    public List<Error> verify() {
        List<Error> errors = new ArrayList<>();

        if(value == null || value.equals("")) {
            errors.add(error);
        }

        return errors;
    }
}
