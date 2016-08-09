package org.mercycorps.translationcards.txcmaker.api.response;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class CreateDeckResponse {
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private int id = -1;

    public Response build() throws URISyntaxException {
        if(errors.isEmpty()) {
            URI uri = new URI(Integer.toString(id));
            return success(uri);
        } else {
            return failure();
        }
    }

    private Response success(URI uri) {

        return Response
                .created(uri)
                .entity(this)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response failure() {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(this)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public void addError(String error) {
        errors.add(error);
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getErrors() {
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
}
