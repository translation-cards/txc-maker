package org.mercycorps.translationcards.txcmaker.api.resource;

import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.api.response.CreateDeckResponse;
import org.mercycorps.translationcards.txcmaker.api.response.RetrieveDeckResponse;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Field;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.ImportDeckForm;
import org.mercycorps.translationcards.txcmaker.service.DeckService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static java.lang.Thread.sleep;

@Path("/decks")
public class DecksResource {

    private DeckService deckService = null;
    @Context
    ServletContext servletContext;
    private AuthUtils authUtils;

    private void init() {
        if(deckService == null) {
            deckService = (DeckService) servletContext.getAttribute("deckService");
            authUtils = (AuthUtils) servletContext.getAttribute("authUtils");
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Deck> retrieveAllDecks() {
        init();

        return deckService.retrieveAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response importDeck(ImportDeckForm importDeckForm, @Context HttpServletRequest request) throws URISyntaxException {
        init();
        CreateDeckResponse createDeckResponse = new CreateDeckResponse();
        Drive drive = getDrive(request, createDeckResponse);
        final String sessionId = request.getSession().getId();
        final List<Field> fieldsToVerify = importDeckForm.getFieldsToVerify(drive);
        if(drive != null) {
            deckService.verifyFormData(createDeckResponse, fieldsToVerify);
            deckService.kickoffVerifyDeckTask(createDeckResponse, sessionId);
        }
        return createDeckResponse.build();
    }

    private Drive getDrive(@Context HttpServletRequest request, CreateDeckResponse createDeckResponse) {
        Drive drive = null;
        try {
            drive = authUtils.getDriveOrOAuth(servletContext, request, null, false, request.getSession().getId());
        } catch(IOException e) {
            createDeckResponse.addError(new Error("", "Authorization failed"));
        }
        return drive;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDeck(@PathParam("id") int id) {
        init();

        RetrieveDeckResponse retrieveDeckResponse = new RetrieveDeckResponse();
        deckService.retrieve(id, retrieveDeckResponse);

        try {
            sleep(3000);
        } catch(InterruptedException e) {

        }

        return retrieveDeckResponse.build();
    }

}
