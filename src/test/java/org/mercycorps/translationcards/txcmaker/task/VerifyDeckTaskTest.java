package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
import org.mercycorps.translationcards.txcmaker.service.LanguageService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
    private static final String AUDIO_DIR_URL = "audio dir url";
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
    private TxcPortingUtility txcPortingUtility;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Drive drive;

    private VerifyDeckTask verifyDeckTask;


    @Before
    public void setup() throws ServletException, IOException {
        initMocks(this);

        // initialize the data passed in through the request

        when(request.getParameter("sessionId")).thenReturn(SESSION_ID);
        when(request.getParameter("audioDirId")).thenReturn(AUDIO_DIR_URL);
        when(request.getParameter("docId")).thenReturn(DOC_ID);

        // initialize the stubbed and mocked values needed to make it through the method

        when(txcPortingUtility.parseAudioDirId(AUDIO_DIR_URL)).thenReturn(AUDIO_DIR_ID);

        Map<String, String> audioFileIds = new HashMap<>();
        audioFileIds.put("stub", "value");
        when(driveService.fetchAudioFilesInDriveDirectory(drive, AUDIO_DIR_ID)).thenReturn(audioFileIds);

        CSVParser parser = new CSVParser(new StringReader("Sure wish I could mock this"), CSVFormat.DEFAULT);
        when(driveService.fetchParsableCsv(drive, DOC_ID)).thenReturn(parser);

        when(authUtils.getDriveOrOAuth(servletContext, null, null, false, SESSION_ID)).thenReturn(drive);

        when(txcPortingUtility.buildTxcJson(any(Deck.class))).thenReturn(DECK_AS_JSON);

        // initialize the task with its dependencies

        when(servletContext.getAttribute("authUtils")).thenReturn(authUtils);
        when(servletContext.getAttribute("driveService")).thenReturn(driveService);
        when(servletContext.getAttribute("servletContext")).thenReturn(servletContext);
        when(servletContext.getAttribute("channelService")).thenReturn(channelService);
        when(servletContext.getAttribute("txcPortingUtility")).thenReturn(txcPortingUtility);
        verifyDeckTask = new VerifyDeckTask();
        verifyDeckTask.init(new StubServletContext(servletContext));
    }

    @Test
    public void shouldGetTheDriveUsingTheSessionId() throws Exception {
        verifyDeckTask.doPost(request, null);

        verify(authUtils).getDriveOrOAuth(servletContext, null, null, false, SESSION_ID);
    }

    @Test
    public void shouldParseTheAudioDirectoryIdFromTheURL() throws Exception {
        verifyDeckTask.doPost(request, null);

        verify(txcPortingUtility).parseAudioDirId(AUDIO_DIR_URL);
    }

    @Test
    public void shouldFetchAudioFiles() throws Exception {
        verifyDeckTask.doPost(request, null);

        verify(driveService).fetchAudioFilesInDriveDirectory(drive, AUDIO_DIR_ID);
    }

    @Test
    public void shouldFetchCsv() throws Exception {
        verifyDeckTask.doPost(request, null);

        verify(driveService).fetchParsableCsv(drive, DOC_ID);
    }

    @Test
    public void shouldParseTheCsvDataIntoTheDeck() throws Exception {
        verifyDeckTask.doPost(request, null);

        verify(txcPortingUtility).parseCsvIntoDeck(any(Deck.class), any(CSVParser.class));
    }

    @Test
    public void shouldRespondOverTheCorrectChannel() throws Exception {
        verifyDeckTask.doPost(request, null);

        ArgumentCaptor<ChannelMessage> channelMessageArgumentCaptor = ArgumentCaptor.forClass(ChannelMessage.class);
        verify(channelService).sendMessage(channelMessageArgumentCaptor.capture());

        ChannelMessage channelMessage = channelMessageArgumentCaptor.getValue();
        assertThat(channelMessage.getClientId(), is(SESSION_ID));
    }

    @Test
    public void shouldRespondWithTheDeckAsJson() throws Exception {
        verifyDeckTask.doPost(request, null);

        ArgumentCaptor<ChannelMessage> channelMessageArgumentCaptor = ArgumentCaptor.forClass(ChannelMessage.class);
        verify(channelService).sendMessage(channelMessageArgumentCaptor.capture());

        ChannelMessage channelMessage = channelMessageArgumentCaptor.getValue();
        assertThat(channelMessage.getMessage(), is(DECK_AS_JSON));
    }

    private class StubServletContext implements ServletConfig {
        ServletContext servletContext;

        StubServletContext(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        @Override
        public String getServletName() {
            return "Stubbed Servlet Config";
        }

        @Override
        public ServletContext getServletContext() {
            return this.servletContext;
        }

        @Override
        public String getInitParameter(String s) {
            return null;
        }

        @Override
        public Enumeration getInitParameterNames() {
            return null;
        }
    }

}