package org.mercycorps.translationcards.txcmaker.resource;

import org.mercycorps.translationcards.txcmaker.model.Error;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class CreateDeckResponse {
    private List<Error> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private int id = -1;
    private String channelToken;

    public ResponseEntity build() throws URISyntaxException {
        if(errors.isEmpty()) {
            return success();
        } else {
            return failure();
        }
    }

    private ResponseEntity success() {
        return ResponseEntity
                .ok(this);
    }

    private ResponseEntity failure() {
        return ResponseEntity
                .badRequest()
                .body(this);
    }

    public void addError(Error error) {
        errors.add(error);
    }

    public void addErrors(List<Error> errors) {
        this.errors.addAll(errors);
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public int getId() {
        return id;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void setChannelToken(String channelToken) {
        this.channelToken = channelToken;
    }

    public String getChannelToken() {
        return channelToken;
    }
}
