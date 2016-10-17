package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.client.http.InputStreamContent;
import com.google.api.client.util.IOUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.model.File;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.mercycorps.translationcards.txcmaker.model.NewCard;
import org.mercycorps.translationcards.txcmaker.model.NewDeck;
import org.mercycorps.translationcards.txcmaker.model.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import static com.google.common.collect.Lists.newArrayList;
import static org.mercycorps.translationcards.txcmaker.model.importDeckForm.ValidDocumentId.CSV_EXPORT_TYPE;

@Service
public class DriveService {

    private static final Logger log = Logger.getLogger(DriveService.class.getName());

    private TxcMakerParser txcMakerParser;
    private GcsStreamFactory gcsStreamFactory;
    private Cache cache;

    @Autowired
    public DriveService(TxcMakerParser txcMakerParser, GcsStreamFactory gcsStreamFactory, Cache cache) {
        this.txcMakerParser = txcMakerParser;
        this.gcsStreamFactory = gcsStreamFactory;
        this.cache = cache;
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

    public Map<String, String> downloadAllAudioFileMetaData(Drive drive, String directoryId, NewDeck deck) {
        Map<String, String> audioFileIds = new HashMap<>();
        Set<String> audioFilesInDeck = getAudioFilesInDeck(deck);
        final ChildList childList = downloadAudioFileReferences(drive, directoryId);
        for (ChildReference audioRef : childList.getItems()) {
            File audioFile = downloadAudioFileMetadata(drive, audioRef);
            if(audioFile != null && audioFilesInDeck.contains(audioFile.getOriginalFilename())) {
                audioFileIds.put(audioFile.getOriginalFilename(), audioRef.getId());
            }
        }
        return audioFileIds;
    }

    public Set<String> getAudioFilesInDeck(NewDeck deck) {
        Set<String> audioFilesInDeck = new HashSet<>();
        for(Translation translation : deck.getTranslations()) {
            for(NewCard card : translation.getCards()) {
                audioFilesInDeck.add(card.getAudio());
            }
        }
        return audioFilesInDeck;
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

    public List<String> getFilenamesInAudioDirectory(Drive drive, String audioDirectoryId) {
        try {
            FileList fileList = drive.files().list()
                    .setQ(String.format("('%s' in parents) and (trashed = false)", audioDirectoryId))
                    .setFields("items/title")
                    .execute();

            List<String> filenames = newArrayList();
            for(File file : fileList.getItems()) {
                filenames.add(file.getTitle());
            }
            return filenames;
        } catch (Exception e) {
            log.info("Fetching file names for audio directory " + audioDirectoryId  + " failed.");
        }
        return null;
    }

    private File downloadAudioFileMetadata(Drive drive, ChildReference audioRef) {
        File audioFile = null;
        try {
            audioFile = drive.files().get(audioRef.getId()).execute();
        } catch(IOException e) {
            log.info("Fetching audio file with id '" + audioRef.getId() + "' failed.");
        }
        return audioFile;
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

    public void downloadAudioFiles(Drive drive, Map<String, String> audioFiles, String folderName) {
        Set<String> includedAudioFiles = new HashSet<>();
        for(String audioFileName : audioFiles.keySet()) {
            if (!includedAudioFiles.contains(audioFileName)) {
                includedAudioFiles.add(audioFileName);
                String filePath = folderName + "/" + audioFileName;
                OutputStream gcsOutput = gcsStreamFactory.getOutputStream(filePath);
                String audioFileId = audioFiles.get(audioFileName);
                downloadAndWriteFile(drive, gcsOutput, audioFileId, audioFileName);
                try {
                    gcsOutput.close();
                } catch(IOException e) {
                    //do something
                }
            }
        }
    }

    private void downloadAndWriteFile(Drive drive, OutputStream outputStream, String audioFileId, String audioFileName) {
        try {
            InputStream inputStream;
            if(cache.containsKey(audioFileName)) {
                byte[] file = (byte[]) cache.get(audioFileName);
                inputStream = new ByteArrayInputStream(file);
                IOUtils.copy(inputStream, outputStream);
            } else {
                inputStream = drive.files().get(audioFileId).executeMediaAsInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                IOUtils.copy(inputStream, byteArrayOutputStream);
                byte[] file = byteArrayOutputStream.toByteArray();
                cache.put(audioFileName, file);
                outputStream.write(file);
            }
        } catch(IOException e) {
            System.err.println(e.getStackTrace().toString());
        }
    }

    public NewDeck assembleDeck(HttpServletRequest request, String documentId, Drive drive) {
        CSVParser parser = downloadParsableCsv(drive, documentId);
        return txcMakerParser.parseCsvIntoDeck(parser, request);
    }
}
