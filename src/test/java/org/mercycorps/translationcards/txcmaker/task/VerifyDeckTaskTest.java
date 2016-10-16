package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.language.LanguageService;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
import org.mercycorps.translationcards.txcmaker.service.StorageService;
import org.mercycorps.translationcards.txcmaker.service.VerifyDeckService;
import org.mercycorps.translationcards.txcmaker.wrapper.GsonWrapper;
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

public class VerifyDeckTaskTest {

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

    private VerifyDeckTask verifyDeckTask;
    private Map<String, String> audioFileMetaData;
    private Deck deck;


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

        deck = new Deck();
        when(driveService.assembleDeck(request, SESSION_ID, DOC_ID, drive))
                .thenReturn(deck);

        when(gsonWrapper.toJson(any(Object.class)))
                .thenReturn(DECK_AS_JSON)
                .thenReturn(DECK_METADATA_AS_JSON);

        audioFileMetaData = new HashMap<>();
        when(driveService.downloadAllAudioFileMetaData(drive, AUDIO_DIR_ID, deck))
                .thenReturn(audioFileMetaData);

        verifyDeckTask = new VerifyDeckTask(servletContext, authUtils, driveService, channelService, gsonWrapper, storageService, verifyDeckService);
    }

    @Test
    public void shouldGetTheDriveUsingTheSessionId() throws Exception {
        verifyDeckTask.verifyDeck(request);

        verify(authUtils).getDriveOrOAuth(servletContext, null, null, false, SESSION_ID);
    }

    @Test
    public void shouldAssembleTheDeck() throws Exception {
        verifyDeckTask.verifyDeck(request);

        verify(driveService).assembleDeck(request, SESSION_ID, DOC_ID, drive);
    }

    @Test
    public void shouldRespondOverTheCorrectChannel() throws Exception {
        verifyDeckTask.verifyDeck(request);

        ArgumentCaptor<ChannelMessage> channelMessageArgumentCaptor = ArgumentCaptor.forClass(ChannelMessage.class);
        verify(channelService).sendMessage(channelMessageArgumentCaptor.capture());

        ChannelMessage channelMessage = channelMessageArgumentCaptor.getValue();
        assertThat(channelMessage.getClientId(), is(SESSION_ID));
    }

    @Test
    public void shouldRespondWithTheDeckAsJson() throws Exception {
        verifyDeckTask.verifyDeck(request);

        ArgumentCaptor<ChannelMessage> channelMessageArgumentCaptor = ArgumentCaptor.forClass(ChannelMessage.class);
        verify(channelService).sendMessage(channelMessageArgumentCaptor.capture());

        ChannelMessage channelMessage = channelMessageArgumentCaptor.getValue();
        assertThat(channelMessage.getMessage(), is(DECK_AS_JSON));
    }

    @Test
    public void shouldWriteDeckToStorage() throws Exception {
        verifyDeckTask.verifyDeck(request);

        verify(storageService).writeFileToStorage(DECK_AS_JSON, SESSION_ID + "/deck.json");
    }

    @Test
    public void shouldWriteDeckMetadataToStorage() throws Exception {
        verifyDeckTask.verifyDeck(request);

        verify(storageService).writeFileToStorage(DECK_METADATA_AS_JSON, SESSION_ID + "/metadata.json");
    }

    @Test
    public void shouldVerifyUsingService() throws Exception {
        verifyDeckTask.verifyDeck(request);

        verify(verifyDeckService).verify(drive, deck, AUDIO_DIR_ID);
    }

    @Test
    public void shouldAddErrorsToDeck() throws Exception {
        when(verifyDeckService.verify(drive, deck, AUDIO_DIR_ID)).thenReturn(newArrayList(new Error("a deck error", false)));
        verifyDeckTask.verifyDeck(request);
        assertThat(deck.errors.size(), is(1));

        assertThat(deck.errors.get(0).message, is("a deck error"));
    }
}