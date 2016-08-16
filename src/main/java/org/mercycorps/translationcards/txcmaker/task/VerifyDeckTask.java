package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import org.apache.commons.csv.CSVParser;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.service.DriveService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class VerifyDeckTask extends HttpServlet {

    ServletContext servletContext;
    private AuthUtils authUtils;
    private DriveService driveService;
    private ChannelService channelService;
    private TxcPortingUtility txcPortingUtility;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        servletContext = config.getServletContext();
        authUtils = (AuthUtils) servletContext.getAttribute("authUtils");
        driveService = (DriveService) servletContext.getAttribute("driveService");
        channelService = (ChannelService) servletContext.getAttribute("channelService");
        txcPortingUtility = (TxcPortingUtility) servletContext.getAttribute("txcPortingUtility");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        final Deck deck = assembleDeck(request);

        sendDeckToClient(deck, request.getParameter("sessionId"));
    }

    private Deck assembleDeck(HttpServletRequest request) {
        Deck deck = Deck.initializeDeckWithFormData(request);
        String sessionId = request.getParameter("sessionId");
        Drive drive = getDrive(sessionId);

        String audioDirId = txcPortingUtility.parseAudioDirId(request.getParameter("audioDirId"));
        Map<String, String> audioFileIds = driveService.fetchAudioFilesInDriveDirectory(drive, audioDirId);
        CSVParser parser = driveService.fetchParsableCsv(drive, request.getParameter("docId"));

        if(audioFileIds.isEmpty() || parser == null) {
            return null;
        }

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
