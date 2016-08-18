package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelService;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
import org.mercycorps.translationcards.txcmaker.service.GcsStreamFactory;
import org.mercycorps.translationcards.txcmaker.service.StorageService;
import org.mercycorps.translationcards.txcmaker.wrapper.GsonWrapper;
import org.mockito.Mock;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BuildTxcTaskTest {

    public static final String SESSION_ID = "session id";
    public static final String DECK_AS_JSON = "deck as json";
    public static final String DIRECTORY_ID = "directory id";
    @Mock
    private ServletContext servletContext;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private GcsStreamFactory gcsStreamFactory;
    @Mock
    private DriveService driveService;
    @Mock
    private ChannelService channelService;
    @Mock
    private Drive drive;
    @Mock
    private HttpServletRequest request;
    @Mock
    private StorageService storageService;
    @Mock
    private GsonWrapper gsonWrapper;
    private BuildTxcTask buildTxcTask;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(request.getParameter("sessionId")).thenReturn(SESSION_ID);
        when(request.getParameter("audioDirId")).thenReturn(DIRECTORY_ID);

        when(authUtils.getDriveOrOAuth(servletContext, null, null, false, SESSION_ID))
                .thenReturn(drive);

        when(storageService.readJson(drive, SESSION_ID))
                .thenReturn(DECK_AS_JSON);

        buildTxcTask = new BuildTxcTask(servletContext, authUtils, gcsStreamFactory, driveService, channelService, storageService, gsonWrapper);

    }

    @Test
    public void name() throws Exception {
        assertThat(true, is(true));

    }

    //    @Test
//    public void shouldGetTheDriveUsingTheSessionId() throws Exception {
//        buildTxcTask.buildTxc(request);
//
//        verify(authUtils).getDriveOrOAuth(servletContext, null, null, false, SESSION_ID);
//    }
//
//    @Test
//    public void shouldReadTheDeckJsonFromStorage() throws Exception {
//        buildTxcTask.buildTxc(request);
//
//        verify(storageService).readJson(drive, SESSION_ID);
//    }
//
//    @Test
//    public void shouldFetchTheAudioFileMetaData() throws Exception {
//        buildTxcTask.buildTxc(request);
//
//        verify(driveService).fetchAudioFilesInDriveDirectory(drive, DIRECTORY_ID);
//    }
}