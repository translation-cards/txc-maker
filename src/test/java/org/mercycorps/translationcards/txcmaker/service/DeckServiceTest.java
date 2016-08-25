package org.mercycorps.translationcards.txcmaker.service;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Field;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.ImportDeckForm;
import org.mercycorps.translationcards.txcmaker.response.ImportDeckResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeckServiceTest {

    DeckService deckService;

    List<Field> fields;

    Error error;
    @Mock
    private ChannelService channelService;
    @Mock
    private Queue taskQueue;
    @Mock
    private TxcMakerParser txcMakerParser;

    private String sessionId;
    private String channelToken;
    private ImportDeckResponse importDeckResponse;
    private ImportDeckForm importDeckForm;

    @Before
    public void setup() throws IOException{
        initMocks(this);

        fields = new ArrayList<>();
        Field field = mock(Field.class);
        when(field.verify()).thenReturn(Collections.<Error>emptyList());
        fields.add(field);

        importDeckResponse = new ImportDeckResponse();
        error = new Error("some message", true);
        importDeckForm = new ImportDeckForm()
                .setDeckName("deck name")
                .setAudioDirId("audio dir id string")
                .setDocId("doc id string")
                .setPublisher("publisher");

        deckService = new DeckService(channelService, taskQueue, txcMakerParser);
        sessionId = "session ID";
        channelToken = "channel token";
    }

    @Test
    public void verifyFormData_shouldAddErrorsToTheResponseWhenThereAreErrors() throws Exception {
        Field failedField = mock(Field.class);
        List<Error> fieldErrors = Collections.singletonList(error);
        when(failedField.verify()).thenReturn(fieldErrors);
        fields.add(failedField);
        deckService.verifyFormData(importDeckResponse, fields);

        assertThat(importDeckResponse.getErrors(), is(fieldErrors));
    }

    @Test
    public void kickoffVerifyDeckTask_shouldAssignAnIdToTheNewDeck() throws Exception {
        deckService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);

        assertThat(importDeckResponse.getId(), is(sessionId));
    }

    @Test
    public void kickoffVerifyDeckTask_shouldAssignAnInvalidIdWhenThereAreErrors() throws Exception {
        importDeckResponse.addError(error);

        deckService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);

        assertThat(importDeckResponse.getId(), is(""));
    }

    @Test
    public void kickoffVerifyDeckTask_shouldCreateAChannel() throws Exception {
        deckService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);

        verify(channelService).createChannel(sessionId);
    }

    @Test
    public void kickoffVerifyDeckTask_shouldAddTheChannelTokenToTheResponse() throws Exception {
        when(channelService.createChannel(sessionId)).thenReturn(channelToken);
        deckService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);

        assertThat(importDeckResponse.getChannelToken(), is(channelToken));
    }

    @Test
    public void kickoffVerifyDeckTask_shouldAddTheVerifyDeckTaskToTheQueue() throws Exception {
        deckService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);

        ArgumentCaptor<TaskOptions> taskOptionsArgumentCaptor = ArgumentCaptor.forClass(TaskOptions.class);
        verify(taskQueue).add(taskOptionsArgumentCaptor.capture());

        TaskOptions taskOptions = taskOptionsArgumentCaptor.getValue();
        assertThat(taskOptions.getUrl(), is("/tasks/txc-verify"));
        assertThat(taskOptions.getStringParams().get("sessionId").get(0), is(sessionId));
        assertThat(taskOptions.getStringParams().get("deckName").get(0), is("deck name"));
        assertThat(taskOptions.getStringParams().get("docId").get(0), is("doc id string"));
        assertThat(taskOptions.getStringParams().get("audioDirId").get(0), is("audio dir id string"));
        assertThat(taskOptions.getStringParams().get("publisher").get(0), is("publisher"));
    }

    @Test
    public void kickoffVerifyDeckTask_shouldDoNothingIfThereAreErrorsPresent() throws Exception {
        importDeckResponse.addError(error);

        deckService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);

        verify(channelService, times(0)).createChannel(sessionId);
        assertNull(importDeckResponse.getChannelToken());
        verify(taskQueue, times(0)).add(any(TaskOptions.class));
    }

    @Test
    public void kickoffBuildDeckTask_shouldAddTheBuildDeckTaskToTheQueue() throws Exception {
        deckService.kickoffBuildDeckTask(sessionId);

        ArgumentCaptor<TaskOptions> taskOptionsArgumentCaptor = ArgumentCaptor.forClass(TaskOptions.class);
        verify(taskQueue).add(taskOptionsArgumentCaptor.capture());

        TaskOptions taskOptions = taskOptionsArgumentCaptor.getValue();
        assertThat(taskOptions.getUrl(), is("/tasks/txc-build"));
        assertThat(taskOptions.getHeaders().get("Content-Type").get(0), is("text/plain"));
        assertThat(taskOptions.getPayload(), is(sessionId.getBytes()));
    }

    @Test
    public void preProcessForm_shouldParseTheDocumentId() throws Exception {
        when(txcMakerParser.parseDocId("doc id string"))
                .thenReturn("doc id");

        deckService.preProcessForm(importDeckForm);

        assertThat(importDeckForm.getDocId(), is("doc id"));
    }

    @Test
    public void preProcessForm_shouldParseTheAudioDirectoryId() throws Exception {
        when(txcMakerParser.parseAudioDirId("audio dir id string"))
                .thenReturn("audio dir id");

        deckService.preProcessForm(importDeckForm);

        assertThat(importDeckForm.getAudioDirId(), is("audio dir id"));

    }
}