package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mercycorps.translationcards.txcmaker.model.deck.DeckMetadata;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
import org.mercycorps.translationcards.txcmaker.service.GcsStreamFactory;
import org.mercycorps.translationcards.txcmaker.service.TxcMakerParser;
import org.mercycorps.translationcards.txcmaker.wrapper.GsonWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/tasks/txc-verify")
public class VerifyDeckTask {

    private ServletContext servletContext;
    private AuthUtils authUtils;
    private DriveService driveService;
    private ChannelService channelService;
    private TxcMakerParser txcMakerParser;
    private GcsStreamFactory gcsStreamFactory;
    private GsonWrapper gsonWrapper;

    @Autowired
    public VerifyDeckTask(ServletContext servletContext, AuthUtils authUtils, DriveService driveService, ChannelService channelService, TxcMakerParser txcMakerParser, GcsStreamFactory gcsStreamFactory, GsonWrapper gsonWrapper) {
        this.servletContext = servletContext;
        this.authUtils = authUtils;
        this.driveService = driveService;
        this.channelService = channelService;
        this.txcMakerParser = txcMakerParser;
        this.gcsStreamFactory = gcsStreamFactory;
        this.gsonWrapper = gsonWrapper;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void verifyDeck(HttpServletRequest request) throws ServletException, IOException {
        final String sessionId = request.getParameter("sessionId");
        final String documentId = request.getParameter("docId");
        final String directoryId = request.getParameter("audioDirId");
        final Drive drive = getDrive(sessionId);

        final Deck deck = driveService.assembleDeck(request, sessionId, documentId, drive);
        deck.verify();

        final String deckJson = gsonWrapper.toJson(deck);
        writeFileToStorage(deckJson, sessionId + "-deck.json");
        writeDeckMetadataToStorage(sessionId, documentId, directoryId);
        sendDeckToClient(deckJson, sessionId);
    }

    private void writeDeckMetadataToStorage(String sessionId, String documentId, String directoryId) throws IOException {
        final DeckMetadata deckMetadata = new DeckMetadata(documentId, directoryId);
        final String deckMetadataJson = gsonWrapper.toJson(deckMetadata);
        writeFileToStorage(deckMetadataJson, sessionId + "-metadata.json");
    }

    private void writeFileToStorage(String content, String fileName) throws IOException {
        OutputStream gcsOutput = gcsStreamFactory.getOutputStream(fileName);
        gcsOutput.write(content.getBytes());
        gcsOutput.close();
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
