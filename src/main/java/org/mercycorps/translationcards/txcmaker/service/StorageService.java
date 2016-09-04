package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.client.util.IOUtils;
import org.mercycorps.translationcards.txcmaker.model.deck.DeckMetadata;
import org.mercycorps.translationcards.txcmaker.wrapper.GsonWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
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
        String deckMetadataJson = readFile(fileName);

        return gsonWrapper.fromJson(deckMetadataJson, DeckMetadata.class);
    }

    public String readFile(String fileName) {
        InputStream inputStream = gcsStreamFactory.getInputStream(fileName);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while(((line = bufferedReader.readLine())) != null) {
                stringBuilder.append(line);
            }
        } catch(IOException e) {
            //do something
        }
        return stringBuilder.toString();
    }

    public void zipTxc(String sessionId, String deckJson, List<String> audioFiles) {
        OutputStream gcsOutput = gcsStreamFactory.getOutputStream(sessionId + "/deck.txc");
        ZipOutputStream zipOutputStream = new ZipOutputStream(gcsOutput);
        try {
            zipOutputStream.putNextEntry(new ZipEntry("card_deck.json"));
            zipOutputStream.write(deckJson.getBytes());
            for (String audioFileName : audioFiles) {
                zipOutputStream.putNextEntry(new ZipEntry(audioFileName));
                InputStream inputStream = gcsStreamFactory.getInputStream(sessionId + "/" + audioFileName);
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
        gcsOutput.write(content.getBytes());
        gcsOutput.close();
    }
}
