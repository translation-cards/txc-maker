package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.DeckMetadata;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
import org.mercycorps.translationcards.txcmaker.service.GcsStreamFactory;
import org.mercycorps.translationcards.txcmaker.service.StorageService;
import org.mercycorps.translationcards.txcmaker.wrapper.GsonWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/tasks/txc-build")
public class BuildTxcTask {

    private ServletContext servletContext;
    private AuthUtils authUtils;
    private GcsStreamFactory gcsStreamFactory;
    private DriveService driveService;
    private ChannelService channelService;
    private StorageService storageService;

    @Autowired
    public BuildTxcTask(ServletContext servletContext, AuthUtils authUtils, GcsStreamFactory gcsStreamFactory, DriveService driveService, ChannelService channelService, StorageService storageService) {
        this.servletContext = servletContext;
        this.authUtils = authUtils;
        this.gcsStreamFactory = gcsStreamFactory;
        this.driveService = driveService;
        this.channelService = channelService;
        this.storageService = storageService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void buildTxc(@RequestBody String sessionId) {
        final DeckMetadata deckMetadata = storageService.readDeckMetaData(sessionId + "-metadata.json");
        final String directoryId = deckMetadata.directoryId;
        final Drive drive = getDrive(sessionId);
        final String deckJson = storageService.readFile(sessionId + "-deck.json");
        Map<String, String> audioFiles = driveService.fetchAudioFilesInDriveDirectory(drive, directoryId);

        zipTxc(sessionId, drive, deckJson, audioFiles);
        pushTxcToDrive(drive, directoryId, sessionId + ".txc");
        sendDeckToClient(deckJson, sessionId);
    }

    private void pushTxcToDrive(Drive drive, String audioDirId, String targetFilename) {
        File targetFileInfo = new File();
        targetFileInfo.setTitle(targetFilename);
        targetFileInfo.setParents(Collections.singletonList(new ParentReference().setId(audioDirId)));
        InputStream txcContentStream = gcsStreamFactory.getInputStream(targetFilename);
        try {
            drive.files().insert(targetFileInfo, new InputStreamContent(null, txcContentStream)).execute();
        } catch(IOException e) {
            //do something
        }
    }

    private void zipTxc(String sessionId, Drive drive, String deckJson, Map<String, String> audioFiles) {
        OutputStream gcsOutput = gcsStreamFactory.getOutputStream(sessionId + ".txc");
        ZipOutputStream zipOutputStream = new ZipOutputStream(gcsOutput);
        Set<String> includedAudioFiles = new HashSet<>();
        try {
            zipOutputStream.putNextEntry(new ZipEntry("card_deck.json"));
            zipOutputStream.write(deckJson.getBytes());
            for (String audioFileName : audioFiles.keySet()) {
                fetchAudioFileAndWriteToZip(drive, zipOutputStream, includedAudioFiles, audioFileName, audioFiles.get(audioFileName));
            }
            zipOutputStream.close();
            gcsOutput.close();
        } catch(IOException e) {
            //do something
        }
    }

    private void fetchAudioFileAndWriteToZip(Drive drive, ZipOutputStream zipOutputStream, Set<String> includedAudioFiles, String audioFileName, String audioFileId) throws IOException {
        if (includedAudioFiles.contains(audioFileName)) {
            return;
        }
        includedAudioFiles.add(audioFileName);
        zipOutputStream.putNextEntry(new ZipEntry(audioFileName));
        drive.files().get(audioFileId).executeMediaAndDownloadTo(zipOutputStream);
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
