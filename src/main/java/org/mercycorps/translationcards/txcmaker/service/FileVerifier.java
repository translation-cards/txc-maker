package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mercycorps.translationcards.txcmaker.task.TxcPortingUtility;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class FileVerifier {

    private static final String CSV_EXPORT_TYPE = "text/csv";
    private static final String SRC_HEADER_LANGUAGE = "Language";
    private static final String SRC_HEADER_LABEL = "Label";
    private static final String SRC_HEADER_TRANSLATION_TEXT = "Translation";
    private static final String SRC_HEADER_FILENAME = "Filename";

    List<String> errors;
    List<String> warnings;
    private Map<String, String> audioFileIds;

    public void verify(String audioDirString, String spreadsheetFileString, Drive drive) throws IOException {
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
        audioFileIds  = new HashMap<>();

        String audioDirId = TxcPortingUtility.parseAudioDirId(audioDirString);
        fetchAudioFileIds(drive, audioDirId);

        String spreadsheetFileId = TxcPortingUtility.getSpreadsheetId(spreadsheetFileString);
        checkForErrors(drive, spreadsheetFileId);
    }

    private void checkForErrors(Drive drive, String spreadsheetFileId) throws IOException {
        Drive.Files.Export sheetExport = drive.files().export(spreadsheetFileId, CSV_EXPORT_TYPE);
        Reader reader = new InputStreamReader(sheetExport.executeMediaAsInputStream());
        CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
        Set<String> includedAudioFiles = new HashSet<>();

        try {
            for (CSVRecord row : parser) {
                String filename = row.get(SRC_HEADER_FILENAME);
                if (includedAudioFiles.contains(filename)) {
                    warnings.add(String.format("Used %s multiple times.", filename));
                    continue;
                }
                includedAudioFiles.add(filename);
                if (!audioFileIds.containsKey(filename)) {
                    errors.add(String.format("Unknown file %s.", filename));
                }
            }
        } finally {
            parser.close();
            reader.close();
        }
    }

    private void fetchAudioFileIds(Drive drive, String audioDirId) throws IOException {
        ChildList audioList = drive.children().list(audioDirId).execute();
        for (ChildReference audioRef : audioList.getItems()) {
            File audioFile = drive.files().get(audioRef.getId()).execute();
            audioFileIds.put(audioFile.getOriginalFilename(), audioRef.getId());
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
