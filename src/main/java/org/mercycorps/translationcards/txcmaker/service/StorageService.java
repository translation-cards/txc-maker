package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.client.util.IOUtils;
import org.mercycorps.translationcards.txcmaker.model.NewDeck;
import org.mercycorps.translationcards.txcmaker.model.deck.DeckMetadata;
import org.mercycorps.translationcards.txcmaker.serializer.GsonWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.cache.Cache;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class StorageService {

    private GcsStreamFactory gcsStreamFactory;
    private GsonWrapper gsonWrapper;
    private Cache cache;

    @Autowired
    public StorageService(GcsStreamFactory gcsStreamFactory, GsonWrapper gsonWrapper, Cache cache) {
        this.gcsStreamFactory = gcsStreamFactory;
        this.gsonWrapper = gsonWrapper;
        this.cache = cache;
    }

    public DeckMetadata readDeckMetaData(String fileName) {
        String deckMetadataJson = readUnicodeFile(fileName);

        return gsonWrapper.fromJson(deckMetadataJson, DeckMetadata.class);
    }

    public NewDeck readDeck(String fileName) {
        String deckJson = readUnicodeFile(fileName);

        return gsonWrapper.fromJson(deckJson, NewDeck.class);
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

    public void zipTxc(String sessionId, String deckJson, List<String> audioFiles) {
        OutputStream gcsOutput = gcsStreamFactory.getOutputStream(sessionId + "/deck.txc");
        ZipOutputStream zipOutputStream = new ZipOutputStream(gcsOutput);
        try {
            zipOutputStream.putNextEntry(new ZipEntry("card_deck.json"));
            zipOutputStream.write(deckJson.getBytes("UTF8"));
            for (String audioFileName : audioFiles) {
                zipOutputStream.putNextEntry(new ZipEntry(audioFileName));
                InputStream inputStream;
                if(cache.containsKey(audioFileName)) {
                    byte[] file = (byte[]) cache.get(audioFileName);
                    inputStream = new ByteArrayInputStream(file);
                } else {
                    inputStream = gcsStreamFactory.getInputStream(sessionId + "/" + audioFileName);
                }
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
