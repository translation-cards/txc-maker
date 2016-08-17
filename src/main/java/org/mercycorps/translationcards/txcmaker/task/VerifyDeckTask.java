package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import org.apache.commons.csv.CSVParser;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
import org.mercycorps.translationcards.txcmaker.service.GcsStreamFactory;
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

    private static final int BUFFER_SIZE = 1024;

    private ServletContext servletContext;
    private AuthUtils authUtils;
    private DriveService driveService;
    private ChannelService channelService;
    private CsvParsingUtility csvParsingUtility;
    private GcsStreamFactory gcsStreamFactory;
    private GsonWrapper gsonWrapper;

    @Autowired
    public VerifyDeckTask(ServletContext servletContext, AuthUtils authUtils, DriveService driveService, ChannelService channelService, CsvParsingUtility csvParsingUtility, GcsStreamFactory gcsStreamFactory, GsonWrapper gsonWrapper) {
        this.servletContext = servletContext;
        this.authUtils = authUtils;
        this.driveService = driveService;
        this.channelService = channelService;
        this.csvParsingUtility = csvParsingUtility;
        this.gcsStreamFactory = gcsStreamFactory;
        this.gsonWrapper = gsonWrapper;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void verifyDeck(HttpServletRequest request) throws ServletException, IOException {
        final String sessionId = request.getParameter("sessionId");
        final String documentId = request.getParameter("docId");

        final Deck deck = Deck.initializeDeckWithFormData(request);
        final Drive drive = getDrive(sessionId);
        final String deckJson = assembleDeckJson(deck, drive, documentId);

        sendDeckToClient(deckJson, sessionId);
        writeDeckToStorage(deckJson, sessionId);
    }

    private void writeDeckToStorage(String deckJson, String sessionId) throws IOException {
        OutputStream gcsOutput = gcsStreamFactory.getOutputStream(sessionId);
        gcsOutput.write(deckJson.getBytes());
        gcsOutput.close();
    }

    private String assembleDeckJson(Deck deck, Drive drive, String docId) {
        CSVParser parser = driveService.fetchParsableCsv(drive, docId);
        csvParsingUtility.parseCsvIntoDeck(deck, parser);
        return gsonWrapper.toJson(deck);
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

    private void sendDeckToClient(String deckJson, String sessionId) throws IOException {
        channelService.sendMessage(new ChannelMessage(sessionId, deckJson));
    }
}
