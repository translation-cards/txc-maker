package org.mercycorps.translationcards.txcmaker.service;

import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.deck.DeckMetadata;
import org.mercycorps.translationcards.txcmaker.wrapper.GsonWrapper;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StorageServiceTest {

    public static final String JSON_FILE_STRING = "{\"documentId\":\"document id\",\"directoryId\":\"directory id\"}";
    public static final String FILE_NAME = "file name";
    private StorageService storageService;
    @Mock
    private GcsStreamFactory gcsStreamFactory;
    @Mock
    private GsonWrapper gsonWrapper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        InputStream inputStream = new ByteArrayInputStream(JSON_FILE_STRING.getBytes("UTF8"));
        when(gcsStreamFactory.getInputStream(FILE_NAME))
                .thenReturn(inputStream);

        storageService = new StorageService(gcsStreamFactory, gsonWrapper);
    }

    @Test
    public void shouldReadAFileFromGcsStorage() throws Exception {
        String fileString = storageService.readFile(FILE_NAME);

        assertThat(fileString, is(JSON_FILE_STRING));
    }

    @Test
    public void shouldParseAFileIntoDeckMetaData() throws Exception {
        DeckMetadata expectedDeckMetadata = new DeckMetadata("document id", "directory id");
        when(gsonWrapper.fromJson(JSON_FILE_STRING, DeckMetadata.class))
                .thenReturn(expectedDeckMetadata);

        DeckMetadata actualDeckMetaData = storageService.readDeckMetaData(FILE_NAME);

        assertThat(actualDeckMetaData, is(expectedDeckMetadata));
    }
}