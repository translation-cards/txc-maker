package org.mercycorps.translationcards.txcmaker.task;


import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.codehaus.jackson.map.ObjectMapper;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Card;
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

    private static final String SRC_HEADER_LANGUAGE = "Language";
    private static final String SRC_HEADER_LABEL = "Label";
    private static final String SRC_HEADER_TRANSLATION_TEXT = "Translation";
    private static final String SRC_HEADER_FILENAME = "Filename";


    ServletContext servletContext;
    private AuthUtils authUtils;
    private DriveService driveService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        servletContext = config.getServletContext();
        authUtils = (AuthUtils) servletContext.getAttribute("authUtils");
        driveService = (DriveService) servletContext.getAttribute("driveService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        final Deck deck = assembleDeck(request);

        sendDeckToClient(deck, request.getParameter("sessionId"));
    }

    private Deck assembleDeck(HttpServletRequest request) {
        Deck deck = initializeDeckWithFormData(request);
        String sessionId = request.getParameter("sessionId");
        Drive drive = getDrive(sessionId);

        String audioDirId = TxcPortingUtility.parseAudioDirId(request.getParameter("audioDirId"));
        Map<String, String> audioFileIds = driveService.fetchAudioFilesInDriveDirectory(drive, audioDirId);
        CSVParser parser = driveService.fetchParsableCsv(drive, request.getParameter("docId"));

        if(audioFileIds.isEmpty() || parser == null) {
            return null;
        }

        for(CSVRecord row : parser) {
            String languageIso = row.get(SRC_HEADER_LANGUAGE);
            String audioFileName = row.get(SRC_HEADER_FILENAME);
            Card card = new Card()
                    .setLabel(row.get(SRC_HEADER_LABEL))
                    .setFilename(audioFileName)
                    .setTranslationText(row.get(SRC_HEADER_TRANSLATION_TEXT));
            deck.addCard(languageIso, "language_label", card);
        }

        return deck;
    }




    private Drive getDrive(String sessionId) {
        Drive drive = null;
        try {
            drive = authUtils.getDriveOrOAuth(servletContext, null, null, false, sessionId);
        } catch(IOException e) {

        }
        return drive;
    }

    private Deck initializeDeckWithFormData(HttpServletRequest req) {
        return new Deck()
                .setDeckLabel(req.getParameter("deckName"))
                .setPublisher(req.getParameter("publisher"))
                .setDeckId(req.getParameter("deckId"))
                .setTimestamp(System.currentTimeMillis())
                .setLicenseUrl(req.getParameter("licenseUrl"));
    }

    private void sendDeckToClient(Deck deck, String sessionId) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        String channelKey = sessionId;
        channelService.sendMessage(new ChannelMessage(channelKey, objectMapper.writeValueAsString(deck)));
    }
}
