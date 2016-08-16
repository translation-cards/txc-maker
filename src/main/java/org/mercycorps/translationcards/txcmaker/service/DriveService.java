package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.mercycorps.translationcards.txcmaker.task.TxcPortingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.mercycorps.translationcards.txcmaker.model.importDeckForm.DocumentId.CSV_EXPORT_TYPE;

@Service
public class DriveService {

    private static final Logger log = Logger.getLogger(DriveService.class.getName());

    TxcPortingUtility txcPortingUtility;

    @Autowired
    public DriveService(TxcPortingUtility txcPortingUtility) {
        this.txcPortingUtility = txcPortingUtility;
    }

    public CSVParser fetchParsableCsv(Drive drive, String documentId) {
        String spreadsheetFileId = txcPortingUtility.getSpreadsheetId(documentId);
        CSVParser parser = null;
        try {
            Drive.Files.Export sheetExport = drive.files().export(spreadsheetFileId, CSV_EXPORT_TYPE);
            Reader reader = new InputStreamReader(sheetExport.executeMediaAsInputStream());
            parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
        } catch(IOException e) {
            log.info("Fetching CSV file with id '" + documentId + "' failed.");
        }
        return parser;
    }

    public Map<String, String> fetchAudioFilesInDriveDirectory(Drive drive, String directoryId) {
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
            childList = drive.children().list(directoryId).execute();
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
}
