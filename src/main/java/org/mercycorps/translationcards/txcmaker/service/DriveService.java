package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mercycorps.translationcards.txcmaker.model.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.logging.Logger;

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

    public CSVParser downloadParsableCsv(Drive drive, String documentId) {
        CSVParser parser = null;
        try {
            Drive.Files.Export sheetExport = drive.files().export(documentId, CSV_EXPORT_TYPE);
            Reader reader = new InputStreamReader(sheetExport.executeMediaAsInputStream(), "UTF8");
            parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
        } catch(IOException e) {
            log.info("Fetching CSV file with id '" + documentId + "' failed.");
        }
        return parser;
    }

    public Map<String, String> downloadAllAudioFileMetaData(Drive drive, String directoryId, Deck deck) {
        Map<String, String> audioFileIds = new HashMap<>();
        Set<String> audioFilesInDeck = getAudioFilesInDeck(deck);
        final ChildList childList = downloadAudioFileReferences(drive, directoryId);
        for (ChildReference audioRef : childList.getItems()) {
            File audioFile = getFile(drive, audioRef.getId());
            if(audioFile != null && audioFilesInDeck.contains(audioFile.getOriginalFilename())) {
                audioFileIds.put(audioFile.getOriginalFilename(), audioRef.getId());
            }
        }
        return audioFileIds;
    }

    public Set<String> getAudioFilesInDeck(Deck deck) {
        Set<String> audioFilesInDeck = new HashSet<>();
        for(Translation translation : deck.getTranslations()) {
            for(Card card : translation.getCards()) {
                audioFilesInDeck.add(card.getAudio());
            }
        }
        return audioFilesInDeck;
    }

    public List<File> getFilesInAudioDirectory(Drive drive, String audioDirectoryId) {
        try {
            FileList fileList = drive.files().list()
                    .setQ(String.format("('%s' in parents) and (trashed = false)", audioDirectoryId))
                    .setFields("items/title, items/downloadUrl, items/id")
                    .execute();

            return fileList.getItems();
        } catch (Exception e) {
            log.info("Fetching file names for audio directory " + audioDirectoryId  + " failed.");
        }
        return null;
    }

    public InputStream getFileInputStream(Drive drive, String fileId) {
        InputStream inputStream = null;
        try {
            inputStream = drive.files().get(fileId).executeMediaAsInputStream();
        } catch(IOException e) {
            log.info("Fetching file with id '" + fileId + "' failed.");
        }
        return inputStream;
    }

    public String pushTxcToDrive(Drive drive, String parentDirectory, String txcPath, String txcFilename) {
        File targetFileInfo = new File();
        targetFileInfo.setTitle(txcFilename);
        targetFileInfo.setParents(Collections.singletonList(new ParentReference().setId(parentDirectory)));
        InputStream txcContentStream = gcsStreamFactory.getInputStream(txcPath);
        String downloadUrl = "";
        try {
            File file = drive.files().insert(targetFileInfo, new InputStreamContent(null, txcContentStream)).execute();
            downloadUrl = file.getAlternateLink();
        } catch(IOException e) {
            //do something
        }

        return downloadUrl;
    }

    public Deck assembleDeck(HttpServletRequest request, String documentId, String sessionId, Drive drive) {
        CSVParser parser = downloadParsableCsv(drive, documentId);
        return txcMakerParser.parseCsvIntoDeck(parser, request, sessionId);
    }

    private File getFile(Drive drive, String fileId) {
        File audioFile = null;
        try {
            audioFile = drive.files().get(fileId).execute();
        } catch(IOException e) {
            log.info("Fetching file with id '" + fileId + "' failed.");
        }
        return audioFile;
    }

    private ChildList downloadAudioFileReferences(Drive drive, String directoryId) {
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
}
