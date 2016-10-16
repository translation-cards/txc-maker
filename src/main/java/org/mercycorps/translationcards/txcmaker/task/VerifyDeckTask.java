package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mercycorps.translationcards.txcmaker.model.deck.DeckMetadata;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
import org.mercycorps.translationcards.txcmaker.service.StorageService;
import org.mercycorps.translationcards.txcmaker.service.VerifyDeckService;
import org.mercycorps.translationcards.txcmaker.wrapper.GsonWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/tasks/txc-verify")
public class VerifyDeckTask {

    private ServletContext servletContext;
    private AuthUtils authUtils;
    private DriveService driveService;
    private ChannelService channelService;
    private GsonWrapper gsonWrapper;
    private StorageService storageService;
    private VerifyDeckService verifyDeckService;

    @Autowired
    public VerifyDeckTask(ServletContext servletContext, AuthUtils authUtils, DriveService driveService, ChannelService channelService,
                          GsonWrapper gsonWrapper, StorageService storageService, VerifyDeckService verifyDeckService) {
        this.servletContext = servletContext;
        this.authUtils = authUtils;
        this.driveService = driveService;
        this.channelService = channelService;
        this.gsonWrapper = gsonWrapper;
        this.storageService = storageService;
        this.verifyDeckService = verifyDeckService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void verifyDeck(HttpServletRequest request) throws ServletException, IOException {
        final String sessionId = request.getParameter("sessionId");
        final String documentId = request.getParameter("docId");
        final String directoryId = request.getParameter("audioDirId");
        final Drive drive = getDrive(sessionId);

        final Deck deck = driveService.assembleDeck(request, sessionId, documentId, drive);
        deck.errors = verifyDeckService.verify(drive, deck, directoryId);

        final String deckJson = gsonWrapper.toJson(deck);
        storageService.writeFileToStorage(deckJson, sessionId + "/deck.json");
        writeDeckMetadataToStorage(sessionId, documentId, directoryId);
        sendDeckToClient(deckJson, sessionId);
    }

    private void writeDeckMetadataToStorage(String sessionId, String documentId, String directoryId) throws IOException {
        final DeckMetadata deckMetadata = new DeckMetadata(documentId, directoryId);
        final String deckMetadataJson = gsonWrapper.toJson(deckMetadata);
        storageService.writeFileToStorage(deckMetadataJson, sessionId + "/metadata.json");
    }

    private Drive getDrive(String sessionId) {
        Drive drive = null;
        try {
            drive = authUtils.getDriveOrOAuth(servletContext, null, null, false, sessionId);
        } catch(IOException e) {
            //do something
        }
        return drive;
    }

    private void sendDeckToClient(String deckJson, String sessionId) {
        channelService.sendMessage(new ChannelMessage(sessionId, deckJson));
    }
}
