package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.*;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mercycorps.translationcards.txcmaker.model.deck.DeckMetadata;
import org.mercycorps.translationcards.txcmaker.response.BuildTxcTaskResponse;
import org.mercycorps.translationcards.txcmaker.response.ResponseFactory;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
import org.mercycorps.translationcards.txcmaker.service.FinalizedDeckFactory;
import org.mercycorps.translationcards.txcmaker.service.StorageService;
import org.mercycorps.translationcards.txcmaker.wrapper.GsonWrapper;
import org.mercycorps.translationcards.txcmaker.wrapper.UrlShortenerWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/tasks/txc-build")
public class BuildTxcTask {

    private ServletContext servletContext;
    private AuthUtils authUtils;
    private DriveService driveService;
    private ChannelService channelService;
    private StorageService storageService;
    private ResponseFactory responseFactory;
    private GsonWrapper gsonWrapper;
    private UrlShortenerWrapper urlShortenerWrapper;
    private FinalizedDeckFactory finalizedDeckFactory;

    @Autowired
    public BuildTxcTask(ServletContext servletContext, AuthUtils authUtils, DriveService driveService, ChannelService channelService, StorageService storageService, ResponseFactory responseFactory, GsonWrapper gsonWrapper, UrlShortenerWrapper urlShortenerWrapper, FinalizedDeckFactory finalizedDeckFactory) {
        this.servletContext = servletContext;
        this.authUtils = authUtils;
        this.driveService = driveService;
        this.channelService = channelService;
        this.storageService = storageService;
        this.responseFactory = responseFactory;
        this.gsonWrapper = gsonWrapper;
        this.urlShortenerWrapper = urlShortenerWrapper;
        this.finalizedDeckFactory = finalizedDeckFactory;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void buildTxc(@RequestBody String sessionId) {
        final DeckMetadata deckMetadata = storageService.readDeckMetaData(sessionId + "/metadata.json");
        final Deck deck = storageService.readDeck(sessionId + "/deck.json");
        final FinalizedDeck finalizedDeck = finalizedDeckFactory.finalize(deck);
        final String finalizedDeckJson = gsonWrapper.toJson(finalizedDeck);
        final Drive drive = getDrive(sessionId);
        final String directoryId = deckMetadata.directoryId;
        final Map<String, String> audioFiles = driveService.downloadAllAudioFileMetaData(drive, directoryId, deck);
        storageService.zipTxc(sessionId, finalizedDeckJson, new ArrayList<>(audioFiles.keySet()));
        final String downloadUrl = driveService.pushTxcToDrive(drive, directoryId, sessionId + "/deck.txc", finalizedDeck.deck_label);
        final String shortUrl = urlShortenerWrapper.getShortUrl(downloadUrl);
        final BuildTxcTaskResponse response = responseFactory.newBuildTxcTaskResponse()
                .setDeck(deck)
                .setDownloadUrl(shortUrl);
        channelService.sendMessage(new ChannelMessage(sessionId, gsonWrapper.toJson(response)));
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

}
