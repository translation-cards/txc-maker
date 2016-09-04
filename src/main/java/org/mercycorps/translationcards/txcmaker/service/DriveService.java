package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.client.http.InputStreamContent;
import com.google.api.client.util.IOUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.mercycorps.translationcards.txcmaker.model.importDeckForm.ValidDocumentId.CSV_EXPORT_TYPE;

@Service
public class DriveService {

    private static final Logger log = Logger.getLogger(DriveService.class.getName());

    private TxcMakerParser txcMakerParser;
    private GcsStreamFactory gcsStreamFactory;

    @Autowired
    public DriveService(TxcMakerParser txcMakerParser, GcsStreamFactory gcsStreamFactory) {
        this.txcMakerParser = txcMakerParser;
        this.gcsStreamFactory = gcsStreamFactory;
    }

    public CSVParser fetchParsableCsv(Drive drive, String documentId) {
        CSVParser parser = null;
        try {
            Drive.Files.Export sheetExport = drive.files().export(documentId, CSV_EXPORT_TYPE);
            Reader reader = new InputStreamReader(sheetExport.executeMediaAsInputStream());
            parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
        } catch(IOException e) {
            log.info("Fetching CSV file with id '" + documentId + "' failed.");
        }
        return parser;
    }

    public Map<String, String> downloadAllAudioFileMetaData(Drive drive, String directoryId) {
        Map<String, String> audioFileIds = new HashMap<>();
        final ChildList childList = fetchAudioFileReferences(drive, directoryId);
        for (ChildReference audioRef : childList.getItems()) {
            File audioFile = fetchAudioFileMetadata(drive, audioRef);
            if(audioFile != null) {
                audioFileIds.put(audioFile.getOriginalFilename(), audioRef.getId());
            }
        }
        return audioFileIds;
    }

    private ChildList fetchAudioFileReferences(Drive drive, String directoryId) {
        ChildList childList = new ChildList();
        try {
            childList = drive
                    .children()
                    .list(directoryId)
                    .setQ("trashed = false")
                    .execute();
        } catch (IOException e) {
            log.info("Fetching audio files in directory " + directoryId + " failed.");

        }
        return childList;
    }

    private File fetchAudioFileMetadata(Drive drive, ChildReference audioRef) {
        File audioFile = null;
        try {
            audioFile = drive.files().get(audioRef.getId()).execute();
        } catch(IOException e) {
            log.info("Fetching audio file with id '" + audioRef.getId() + "' failed.");
        }
        return audioFile;
    }

    public String pushTxcToDrive(Drive drive, String audioDirId, String targetFilename) {
        File targetFileInfo = new File();
        targetFileInfo.setTitle(targetFilename);
        targetFileInfo.setParents(Collections.singletonList(new ParentReference().setId(audioDirId)));
        InputStream txcContentStream = gcsStreamFactory.getInputStream(targetFilename);
        String downloadUrl = "";
        try {
            File file = drive.files().insert(targetFileInfo, new InputStreamContent(null, txcContentStream)).execute();
            downloadUrl = file.getAlternateLink();
        } catch(IOException e) {
            //do something
        }

        return downloadUrl;
    }

    public void zipTxc(String sessionId, String deckJson, Map<String, String> audioFiles) {
        OutputStream gcsOutput = gcsStreamFactory.getOutputStream(sessionId + "/deck.txc");
        ZipOutputStream zipOutputStream = new ZipOutputStream(gcsOutput);
        Set<String> includedAudioFiles = new HashSet<>();
        try {
            zipOutputStream.putNextEntry(new ZipEntry("card_deck.json"));
            zipOutputStream.write(deckJson.getBytes());
            for (String audioFileName : audioFiles.keySet()) {
                if (!includedAudioFiles.contains(audioFileName)) {
                    includedAudioFiles.add(audioFileName);
                    zipOutputStream.putNextEntry(new ZipEntry(audioFileName));
                    InputStream inputStream = gcsStreamFactory.getInputStream(sessionId + "/" + audioFileName);
                    IOUtils.copy(inputStream, zipOutputStream);
                }
            }
            zipOutputStream.close();
            gcsOutput.close();
        } catch(IOException e) {
            //do something
        }
    }

    public void downloadAudioFiles(Drive drive, Map<String, String> audioFiles, String folderName) {
        Set<String> includedAudioFiles = new HashSet<>();
        for(String audioFileName : audioFiles.keySet()) {
            if (!includedAudioFiles.contains(audioFileName)) {
                includedAudioFiles.add(audioFileName);
                String filePath = folderName + "/" + audioFileName;
                OutputStream gcsOutput = gcsStreamFactory.getOutputStream(filePath);
                String audioFileId = audioFiles.get(audioFileName);
                downloadAndWriteFile(drive, gcsOutput, audioFileId);
                try {
                    gcsOutput.close();
                } catch(IOException e) {
                    //do something
                }
            }
        }
    }

    private void downloadAndWriteFile(Drive drive, OutputStream outputStream, String fileId) {
        try {
            drive.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        } catch(IOException e) {
            System.err.println("DriveService.downloadAndWriteFile failed.");
        }
    }

    public Deck assembleDeck(HttpServletRequest request, String sessionId, String documentId, Drive drive) {
        final Deck deck = Deck.initializeDeckWithFormData(request);
        CSVParser parser = fetchParsableCsv(drive, documentId);
        txcMakerParser.parseCsvIntoDeck(deck, parser);
        deck.id = sessionId;
        return deck;
    }

    public void writeFileToStorage(String content, String fileName) throws IOException {
        OutputStream gcsOutput = gcsStreamFactory.getOutputStream(fileName);
        gcsOutput.write(content.getBytes());
        gcsOutput.close();
    }
}
