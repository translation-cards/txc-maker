package org.mercycorps.translationcards.txcmaker.api.resource;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.mercycorps.translationcards.txcmaker.api.response.CreateDeckResponse;
import org.mercycorps.translationcards.txcmaker.api.response.RetrieveDeckResponse;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.model.Error;
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
            authUtils = (AuthUtils) servletContext.getAttribute("authUtils");
            deckService = new DeckService();
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
        if(drive != null) {
            deckService.verifyFormData(createDeckResponse, importDeckForm.getFieldsToVerify(drive));
            String token = getTokenForNewChannel(request.getSession().getId());
            createDeckResponse.setChannelToken(token);

            Queue queue = QueueFactory.getQueue("queue-txc-building");
            TaskOptions taskOptions = TaskOptions.Builder.withUrl("/tasks/txc-verify")
                    .param("sessionId", request.getSession().getId());
            queue.add(taskOptions);
        }
        return createDeckResponse.build();
    }

    private String getTokenForNewChannel(String sessionId) {
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        return channelService.createChannel(sessionId);
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
