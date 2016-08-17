package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import org.apache.commons.csv.CSVParser;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
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

    ServletContext servletContext;
    private AuthUtils authUtils;
    private DriveService driveService;
    private ChannelService channelService;
    private TxcPortingUtility txcPortingUtility;

    @Autowired
    public VerifyDeckTask(ServletContext servletContext, AuthUtils authUtils, DriveService driveService, ChannelService channelService, TxcPortingUtility txcPortingUtility) {
        this.servletContext = servletContext;
        this.authUtils = authUtils;
        this.driveService = driveService;
        this.channelService = channelService;
        this.txcPortingUtility = txcPortingUtility;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void verifyDeck(HttpServletRequest request) throws ServletException, IOException {
        final Deck deck = assembleDeck(request);

        sendDeckToClient(deck, request.getParameter("sessionId"));
    }

    private Deck assembleDeck(HttpServletRequest request) {
        Deck deck = Deck.initializeDeckWithFormData(request);
        String sessionId = request.getParameter("sessionId");
        Drive drive = getDrive(sessionId);

        CSVParser parser = driveService.fetchParsableCsv(drive, request.getParameter("docId"));

        txcPortingUtility.parseCsvIntoDeck(deck, parser);

        return deck;
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

    private void sendDeckToClient(Deck deck, String sessionId) throws IOException {
        channelService.sendMessage(new ChannelMessage(sessionId, txcPortingUtility.buildTxcJson(deck)));
    }
}
