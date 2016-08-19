package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.model.DeckMetadata;
import org.mercycorps.translationcards.txcmaker.wrapper.GsonWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}
