package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.DeckMetadata;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
import org.mercycorps.translationcards.txcmaker.service.StorageService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import javax.servlet.ServletContext;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BuildTxcTaskTest {

    public static final String SESSION_ID = "session id";
    public static final String DECK_AS_JSON = "deck as json";
    public static final String DIRECTORY_ID = "directory id";
    public static final String DOCUMENT = "document id";
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
    private BuildTxcTask buildTxcTask;
    private Map<String, String> audioFiles = new HashMap<>();
    ;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        DeckMetadata deckMetadata = new DeckMetadata(DOCUMENT, DIRECTORY_ID);
        when(storageService.readDeckMetaData(SESSION_ID + "-metadata.json"))
                .thenReturn(deckMetadata);

        when(storageService.readFile(SESSION_ID + "-deck.json"))
                .thenReturn(DECK_AS_JSON);

        when(authUtils.getDriveOrOAuth(servletContext, null, null, false, SESSION_ID))
                .thenReturn(drive);

        audioFiles.put("file name", "file id");
        when(driveService.fetchAllAudioFileMetaData(drive, DIRECTORY_ID))
                .thenReturn(audioFiles);

        buildTxcTask = new BuildTxcTask(servletContext, authUtils, driveService, channelService, storageService);

    }

    @Test
    public void shouldReadDeckMetadataFromStorage() throws Exception {
        buildTxcTask.buildTxc(SESSION_ID);

        verify(storageService).readDeckMetaData(SESSION_ID + "-metadata.json");
    }

    @Test
    public void shouldReadDeckJsonFromStorage() throws Exception {
        buildTxcTask.buildTxc(SESSION_ID);

        verify(storageService).readFile(SESSION_ID + "-deck.json");
    }

    @Test
    public void shouldGetTheDriveUsingTheSessionId() throws Exception {
        buildTxcTask.buildTxc(SESSION_ID);

        verify(authUtils).getDriveOrOAuth(servletContext, null, null, false, SESSION_ID);
    }

    @Test
    public void shouldFetchTheAudioFileMetaData() throws Exception {
        buildTxcTask.buildTxc(SESSION_ID);

        verify(driveService).fetchAllAudioFileMetaData(drive, DIRECTORY_ID);
    }

    @Test
    public void shouldZipTheTxc() throws Exception {
        buildTxcTask.buildTxc(SESSION_ID);

        verify(driveService).downloadAudioFilesAndZipTxc(SESSION_ID, drive, DECK_AS_JSON, audioFiles);
    }

    @Test
    public void shouldPushTxcToDrive() throws Exception {
        buildTxcTask.buildTxc(SESSION_ID);

        verify(driveService).pushTxcToDrive(drive, DIRECTORY_ID, SESSION_ID + ".txc");
    }

    @Test
    public void shouldSendDeckToClient() throws Exception {
        buildTxcTask.buildTxc(SESSION_ID);

        ArgumentCaptor<ChannelMessage> argumentCaptor = ArgumentCaptor.forClass(ChannelMessage.class);
        verify(channelService).sendMessage(argumentCaptor.capture());

        ChannelMessage channelMessage = argumentCaptor.getValue();
        assertThat(channelMessage.getClientId(), is(SESSION_ID));
        assertThat(channelMessage.getMessage(), is(DECK_AS_JSON));
    }
}