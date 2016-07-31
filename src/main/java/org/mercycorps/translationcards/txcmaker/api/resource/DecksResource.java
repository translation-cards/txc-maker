package org.mercycorps.translationcards.txcmaker.api.resource;

import org.mercycorps.translationcards.txcmaker.api.response.CreateDeckResponse;
import org.mercycorps.translationcards.txcmaker.api.response.RetrieveDeckResponse;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.model.ImportDeckForm;
import org.mercycorps.translationcards.txcmaker.service.DeckService;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.List;

@Path("/decks")
public class DecksResource {

    private DeckService deckService = null;

    @Context
    ServletContext servletContext;

    private void init() {
        if(deckService == null) {
            AuthUtils authUtils = (AuthUtils) servletContext.getAttribute("authUtils");
            deckService = new DeckService(authUtils);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Deck> retrieveAllDecks() {
        init();

        return deckService.retrieveAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response importDeck(MultivaluedMap<String,String> formInput) throws URISyntaxException {
        init();

        CreateDeckResponse createDeckResponse = new CreateDeckResponse();
        deckService.create(new ImportDeckForm(formInput), createDeckResponse);

        return createDeckResponse.build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDeck(@PathParam("id") int id) {
        init();

        RetrieveDeckResponse retrieveDeckResponse = new RetrieveDeckResponse();
        deckService.retrieve(id, retrieveDeckResponse);

        return retrieveDeckResponse.build();
    }

}
