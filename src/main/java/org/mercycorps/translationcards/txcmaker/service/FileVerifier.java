package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.Drive;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mercycorps.translationcards.txcmaker.task.TxcMakerParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class FileVerifier {

    private static final String CSV_EXPORT_TYPE = "text/csv";
    private static final String SRC_HEADER_LANGUAGE = "Language";
    private static final String SRC_HEADER_LABEL = "Label";
    private static final String SRC_HEADER_TRANSLATION_TEXT = "Translation";
    private static final String SRC_HEADER_FILENAME = "Filename";

    private List<String> errors;
    private List<String> warnings;
    private Map<String, String> audioFileIds;
    private TxcMakerParser txcMakerParser;
    private DriveService driveService;

    @Autowired
    public FileVerifier(TxcMakerParser txcMakerParser, DriveService driveService) {
        this.txcMakerParser = txcMakerParser;
        this.driveService = driveService;
    }

    public void verify(String audioDirString, String spreadsheetFileString, Drive drive) throws IOException {
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
        audioFileIds  = new HashMap<>();

        String audioDirId = txcMakerParser.parseAudioDirId(audioDirString);
        audioFileIds = driveService.fetchAudioFilesInDriveDirectory(drive, audioDirId);

        String spreadsheetFileId = txcMakerParser.parseDocId(spreadsheetFileString);
        CSVParser parser = driveService.fetchParsableCsv(drive, spreadsheetFileId);
        checkForErrors(parser);
    }

    private void checkForErrors(CSVParser parser) throws IOException {
        Set<String> includedAudioFiles = new HashSet<>();

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
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
