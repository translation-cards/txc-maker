package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.controller.BuildTxcController;
import org.mercycorps.translationcards.txcmaker.model.FinalizedDeck;
import org.mercycorps.translationcards.txcmaker.model.NewDeck;
import org.mercycorps.translationcards.txcmaker.model.deck.DeckMetadata;
import org.mercycorps.translationcards.txcmaker.response.BuildTxcTaskResponse;
import org.mercycorps.translationcards.txcmaker.response.ResponseFactory;
import org.mercycorps.translationcards.txcmaker.serializer.GsonWrapper;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
import org.mercycorps.translationcards.txcmaker.service.FinalizedDeckFactory;
import org.mercycorps.translationcards.txcmaker.service.StorageService;
import org.mercycorps.translationcards.txcmaker.wrapper.UrlShortenerWrapper;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class BuildTxcControllerTest {

    public static final String SESSION_ID = "session id";
    public static final String DECK_AS_JSON = "deck as json";
    public static final String DIRECTORY_ID = "directory id";
    public static final String DOCUMENT = "document id";
    public static final String DOWNLOAD_URL = "download url";
    public static final String SHORT_URL = "short url";
    private static final String FINALIZED_DECK_JSON = "finalized deck as json";
    @Mock
    private ServletContext servletContext;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private DriveService driveService;
    @Mock
    private ChannelService channelService;
    @Mock
    private Drive drive;
    @Mock
    private StorageService storageService;
    @Mock
    private ResponseFactory responseFactory;
    @Mock
    private GsonWrapper gsonWrapper;
    @Mock
    private UrlShortenerWrapper urlShortenerWrapper;
    @Mock
    private FinalizedDeckFactory finalizedDeckFactory;
    @Mock
    private NewDeck deck;
    private BuildTxcController buildTxcController;
    private Map<String, String> audioFiles = new HashMap<>();
    private BuildTxcTaskResponse response;
//    private NewDeck deck = new NewDeck().setDeckLabel("label");
    private FinalizedDeck finalizedDeck = new FinalizedDeck().setDeck_label("label");

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        DeckMetadata deckMetadata = new DeckMetadata(DOCUMENT, DIRECTORY_ID);
        when(storageService.readDeckMetaData(SESSION_ID + "/metadata.json"))
                .thenReturn(deckMetadata);

        when(storageService.readDeck(SESSION_ID + "/deck.json"))
                .thenReturn(deck);

        when(finalizedDeckFactory.finalize(deck))
                .thenReturn(finalizedDeck);

        when(gsonWrapper.toJson(finalizedDeck))
                .thenReturn(FINALIZED_DECK_JSON);

        when(authUtils.getDriveOrOAuth(servletContext, null, null, false, SESSION_ID))
                .thenReturn(drive);

        audioFiles.put("file name", "file id");
        when(driveService.downloadAllAudioFileMetaData(drive, DIRECTORY_ID, deck))
                .thenReturn(audioFiles);

        when(driveService.pushTxcToDrive(drive, DIRECTORY_ID, SESSION_ID + "/deck.txc", finalizedDeck.deck_label + ".txc"))
                .thenReturn(DOWNLOAD_URL);

        when(urlShortenerWrapper.getShortUrl(DOWNLOAD_URL))
                .thenReturn(SHORT_URL);

        response = new BuildTxcTaskResponse();
        when(responseFactory.newBuildTxcTaskResponse())
                .thenReturn(response);

        when(deck.getDeckLabel()).thenReturn("label");

        buildTxcController = new BuildTxcController(servletContext, authUtils, driveService, channelService, storageService,
                responseFactory, gsonWrapper, urlShortenerWrapper, finalizedDeckFactory);

    }

    @Test
    public void shouldReadDeckMetadataFromStorage() throws Exception {
        buildTxcController.buildTxc(SESSION_ID);

        verify(storageService).readDeckMetaData(SESSION_ID + "/metadata.json");
    }

    @Test
    public void shouldReadDeckJsonFromStorage() throws Exception {
        buildTxcController.buildTxc(SESSION_ID);

        verify(storageService).readDeck(SESSION_ID + "/deck.json");
    }

    @Test
    public void shouldGetTheDriveUsingTheSessionId() throws Exception {
        buildTxcController.buildTxc(SESSION_ID);

        verify(authUtils).getDriveOrOAuth(servletContext, null, null, false, SESSION_ID);
    }

    @Test
    public void shouldFetchTheAudioFileMetaData() throws Exception {
        buildTxcController.buildTxc(SESSION_ID);

        verify(driveService).downloadAllAudioFileMetaData(drive, DIRECTORY_ID, deck);
    }

    @Test
    public void shouldZipTheTxc() throws Exception {
        buildTxcController.buildTxc(SESSION_ID);

        verify(storageService).zipTxc(SESSION_ID, FINALIZED_DECK_JSON, new ArrayList<>(audioFiles.keySet()));
    }

    @Test
    public void shouldPushTxcToDrive() throws Exception {
        buildTxcController.buildTxc(SESSION_ID);

        verify(driveService).pushTxcToDrive(drive, DIRECTORY_ID, SESSION_ID + "/deck.txc", deck.getDeckLabel() + ".txc");
    }

    @Test
    public void shouldGetAShortenedUrl() throws Exception {
        buildTxcController.buildTxc(SESSION_ID);

        verify(urlShortenerWrapper).getShortUrl(DOWNLOAD_URL);
    }

    @Test
    public void shouldInitializeTheResponse() throws Exception {
        buildTxcController.buildTxc(SESSION_ID);

        ArgumentCaptor<BuildTxcTaskResponse> argumentCaptor = ArgumentCaptor.forClass(BuildTxcTaskResponse.class);
        verify(gsonWrapper, times(2)).toJson(argumentCaptor.capture());

        BuildTxcTaskResponse response = argumentCaptor.getAllValues().get(1);
        assertThat(response.getDeck(), is(deck));
        assertThat(response.getDownloadUrl(), is(SHORT_URL));
    }

    @Test
    public void shouldSendDeckAndDownLoadUrlToClient() throws Exception {
        when(gsonWrapper.toJson(response))
                .thenReturn("response string");

        buildTxcController.buildTxc(SESSION_ID);

        ArgumentCaptor<ChannelMessage> argumentCaptor = ArgumentCaptor.forClass(ChannelMessage.class);
        verify(channelService).sendMessage(argumentCaptor.capture());

        ChannelMessage channelMessage = argumentCaptor.getValue();
        assertThat(channelMessage.getClientId(), is(SESSION_ID));
        assertThat(channelMessage.getMessage(), is("response string"));
    }
}