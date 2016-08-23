package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.mercycorps.translationcards.txcmaker.model.importDeckForm.DocumentId.CSV_EXPORT_TYPE;

@Service
public class DriveService {

    private static final Logger log = Logger.getLogger(DriveService.class.getName());

    TxcMakerParser txcMakerParser;
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

    public Map<String, String> fetchAllAudioFileMetaData(Drive drive, String directoryId) {
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

    public void fetchAudioFileAndWriteToZip(Drive drive, ZipOutputStream zipOutputStream, Set<String> includedAudioFiles, String audioFileName, String audioFileId) throws IOException {
        if (includedAudioFiles.contains(audioFileName)) {
            return;
        }
        includedAudioFiles.add(audioFileName);
        zipOutputStream.putNextEntry(new ZipEntry(audioFileName));
        drive.files().get(audioFileId).executeMediaAndDownloadTo(zipOutputStream);
    }

    public void downloadAudioFilesAndZipTxc(String sessionId, Drive drive, String deckJson, Map<String, String> audioFiles) {
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
}
