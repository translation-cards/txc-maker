package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.client.util.IOUtils;
import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mercycorps.translationcards.txcmaker.model.deck.DeckMetadata;
import org.mercycorps.translationcards.txcmaker.wrapper.GsonWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class StorageService {

    private GcsStreamFactory gcsStreamFactory;
    private GsonWrapper gsonWrapper;

    @Autowired
    public StorageService(GcsStreamFactory gcsStreamFactory, GsonWrapper gsonWrapper) {
        this.gcsStreamFactory = gcsStreamFactory;
        this.gsonWrapper = gsonWrapper;
    }

    public DeckMetadata readDeckMetaData(String fileName) {
        String deckMetadataJson = readUnicodeFile(fileName);

        return gsonWrapper.fromJson(deckMetadataJson, DeckMetadata.class);
    }

    public Deck readDeck(String fileName) {
        String deckJson = readUnicodeFile(fileName);

        return gsonWrapper.fromJson(deckJson, Deck.class);
    }

    public String readUnicodeFile(String fileName) {
        InputStream inputStream = gcsStreamFactory.getInputStream(fileName);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
            String line;
            while(((line = bufferedReader.readLine())) != null) {
                stringBuilder.append(line);
            }
        } catch(IOException e) {
            //do something
        }
        return stringBuilder.toString();
    }

    public void zipTxc(Drive drive, String sessionId, String deckJson, Map<String, String> audioFiles) {
        OutputStream gcsOutput = gcsStreamFactory.getOutputStream(sessionId + "/deck.txc");
        ZipOutputStream zipOutputStream = new ZipOutputStream(gcsOutput);
        try {
            zipOutputStream.putNextEntry(new ZipEntry("card_deck.json"));
            zipOutputStream.write(deckJson.getBytes("UTF8"));
            for (String audioFileName : audioFiles.keySet()) {
                zipOutputStream.putNextEntry(new ZipEntry(audioFileName));
                InputStream inputStream = drive.files().get(audioFiles.get(audioFileName)).executeMediaAsInputStream();
                IOUtils.copy(inputStream, zipOutputStream);
            }
            zipOutputStream.close();
            gcsOutput.close();
        } catch(IOException e) {
            //do something
        }
    }

    public void writeFileToStorage(String content, String fileName) throws IOException {
        OutputStream gcsOutput = gcsStreamFactory.getOutputStream(fileName);
        gcsOutput.write(content.getBytes("UTF8"));
        gcsOutput.close();
    }
}
