package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.DeckMetadata;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
import org.mercycorps.translationcards.txcmaker.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Map;

@RestController
@RequestMapping("/tasks/txc-build")
public class BuildTxcTask {

    private ServletContext servletContext;
    private AuthUtils authUtils;
    private DriveService driveService;
    private ChannelService channelService;
    private StorageService storageService;

    @Autowired
    public BuildTxcTask(ServletContext servletContext, AuthUtils authUtils, DriveService driveService, ChannelService channelService, StorageService storageService) {
        this.servletContext = servletContext;
        this.authUtils = authUtils;
        this.driveService = driveService;
        this.channelService = channelService;
        this.storageService = storageService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void buildTxc(@RequestBody String sessionId) {
        final DeckMetadata deckMetadata = storageService.readDeckMetaData(sessionId + "-metadata.json");
        final String deckJson = storageService.readFile(sessionId + "-deck.json");
        final Drive drive = getDrive(sessionId);
        final String directoryId = deckMetadata.directoryId;
        final Map<String, String> audioFiles = driveService.fetchAllAudioFileMetaData(drive, directoryId);
        driveService.downloadAudioFilesAndZipTxc(sessionId, drive, deckJson, audioFiles);
        driveService.pushTxcToDrive(drive, directoryId, sessionId + ".txc");
        channelService.sendMessage(new ChannelMessage(sessionId, deckJson));
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
