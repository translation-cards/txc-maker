package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.controller.VerifyDeckController;
import org.mercycorps.translationcards.txcmaker.language.LanguageService;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.NewDeck;
import org.mercycorps.translationcards.txcmaker.serializer.GsonWrapper;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
import org.mercycorps.translationcards.txcmaker.service.StorageService;
import org.mercycorps.translationcards.txcmaker.service.VerifyDeckService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VerifyDeckControllerTest {

    private static final String SESSION_ID = "session id";
    private static final String AUDIO_DIR_ID = "audio dir id";
    private static final String DOC_ID = "document id";
    public static final String DECK_AS_JSON = "deck as JSON";
    public static final String DECK_METADATA_AS_JSON = "deck metadata as json";

    @Mock
    private AuthUtils authUtils;
    @Mock
    private DriveService driveService;
    @Mock
    private LanguageService languageService;
    @Mock
    private ServletContext servletContext;
    @Mock
    private ChannelService channelService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Drive drive;
    @Mock
    private OutputStream outputStream;
    @Mock
    private GsonWrapper gsonWrapper;
    @Mock
    private StorageService storageService;
    @Mock
    private VerifyDeckService verifyDeckService;

    private VerifyDeckController verifyDeckController;
    private Map<String, String> audioFileMetaData;
    private NewDeck deck;

    @Before
    public void setup() throws ServletException, IOException {
        initMocks(this);

        when(request.getParameter("sessionId"))
                .thenReturn(SESSION_ID);
        when(request.getParameter("audioDirId"))
                .thenReturn(AUDIO_DIR_ID);
        when(request.getParameter("docId"))
                .thenReturn(DOC_ID);

        CSVParser parser = new CSVParser(new StringReader("Sure wish I could mock this"), CSVFormat.DEFAULT);
        when(driveService.downloadParsableCsv(drive, DOC_ID))
                .thenReturn(parser);

        when(authUtils.getDriveOrOAuth(servletContext, null, null, false, SESSION_ID))
                .thenReturn(drive);

        deck = new NewDeck(null, null, null, 0L, false, null, null, null, null, null, null);
        when(driveService.assembleDeck(request, DOC_ID, drive))
                .thenReturn(deck);

        when(gsonWrapper.toJson(any(Object.class)))
                .thenReturn(DECK_AS_JSON)
                .thenReturn(DECK_METADATA_AS_JSON);

        audioFileMetaData = new HashMap<>();
        when(driveService.downloadAllAudioFileMetaData(drive, AUDIO_DIR_ID, deck))
                .thenReturn(audioFileMetaData);

        verifyDeckController = new VerifyDeckController(servletContext, authUtils, driveService, channelService, gsonWrapper, storageService, verifyDeckService);
    }

    @Test
    public void shouldGetTheDriveUsingTheSessionId() throws Exception {
        verifyDeckController.verifyDeck(request);

        verify(authUtils).getDriveOrOAuth(servletContext, null, null, false, SESSION_ID);
    }

    @Test
    public void shouldAssembleTheDeck() throws Exception {
        verifyDeckController.verifyDeck(request);

        verify(driveService).assembleDeck(request, DOC_ID, drive);
    }

    @Test
    public void shouldRespondOverTheCorrectChannel() throws Exception {
        verifyDeckController.verifyDeck(request);

        ArgumentCaptor<ChannelMessage> channelMessageArgumentCaptor = ArgumentCaptor.forClass(ChannelMessage.class);
        verify(channelService).sendMessage(channelMessageArgumentCaptor.capture());

        ChannelMessage channelMessage = channelMessageArgumentCaptor.getValue();
        assertThat(channelMessage.getClientId(), is(SESSION_ID));
    }

    @Test
    public void shouldRespondWithTheDeckAsJson() throws Exception {
        verifyDeckController.verifyDeck(request);

        ArgumentCaptor<ChannelMessage> channelMessageArgumentCaptor = ArgumentCaptor.forClass(ChannelMessage.class);
        verify(channelService).sendMessage(channelMessageArgumentCaptor.capture());

        ChannelMessage channelMessage = channelMessageArgumentCaptor.getValue();
        assertThat(channelMessage.getMessage(), is(DECK_AS_JSON));
    }

    @Test
    public void shouldWriteDeckToStorage() throws Exception {
        verifyDeckController.verifyDeck(request);

        verify(storageService).writeFileToStorage(DECK_AS_JSON, SESSION_ID + "/deck.json");
    }

    @Test
    public void shouldWriteDeckMetadataToStorage() throws Exception {
        verifyDeckController.verifyDeck(request);

        verify(storageService).writeFileToStorage(DECK_METADATA_AS_JSON, SESSION_ID + "/metadata.json");
    }

    @Test
    public void shouldDownloadAllAudioFileMetaData() throws Exception {
        verifyDeckController.verifyDeck(request);

        verify(driveService).downloadAllAudioFileMetaData(drive, AUDIO_DIR_ID, deck);
    }

    @Test
    public void shouldDownloadAudioFiles() throws Exception {
        verifyDeckController.verifyDeck(request);

        verify(driveService).downloadAudioFiles(drive, audioFileMetaData, SESSION_ID);
    }

    @Test
    public void shouldVerifyUsingService() throws Exception {
        verifyDeckController.verifyDeck(request);

        verify(verifyDeckService).verify(drive, deck, AUDIO_DIR_ID);
    }

    @Test
    public void shouldAddErrorsToDeck() throws Exception {
        when(verifyDeckService.verify(drive, deck, AUDIO_DIR_ID)).thenReturn(newArrayList(new Error("a deck error", false)));

        verifyDeckController.verifyDeck(request);

        assertThat(deck.getParsingErrors().size(), is(1));
        assertThat(deck.getParsingErrors().get(0).message, is("a deck error"));
    }
}
