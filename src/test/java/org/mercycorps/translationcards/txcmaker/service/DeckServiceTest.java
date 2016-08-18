package org.mercycorps.translationcards.txcmaker.service;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Field;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.ImportDeckForm;
import org.mercycorps.translationcards.txcmaker.resource.ImportDeckResponse;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DeckServiceTest {

    DeckService deckService;

    List<Field> fields;

    private ImportDeckResponse importDeckResponse;
    Error error;
    private ChannelService channelService;
    private Queue taskQueue;
    private String sessionId;
    private String channelToken;
    private ImportDeckForm importDeckForm;

    @Before
    public void setup() throws IOException{

        fields = new ArrayList<>();
        Field field = mock(Field.class);
        when(field.verify()).thenReturn(Collections.<Error>emptyList());
        fields.add(field);
        channelService = mock(ChannelService.class);
        taskQueue = mock(Queue.class);

        importDeckResponse = new ImportDeckResponse();
        error = new Error("someField", "some message");
        importDeckForm = new ImportDeckForm()
                .setDeckName("deck name")
                .setAudioDirId("audio dir id")
                .setDocId("doc id")
                .setPublisher("publisher");

        deckService = new DeckService(channelService, taskQueue);
        sessionId = "session ID";
        channelToken = "channel token";
    }


    @Test
    public void verifyFormData_shouldAssignAnIdToTheNewDeck() throws Exception {
        deckService.verifyFormData(importDeckResponse, fields);

        assertThat(importDeckResponse.getId(), is(10));
    }

    @Test
    public void verifyFormData_shouldAssignAnInvalidIdWhenThereAreErrors() throws Exception {
        Field failedField = mock(Field.class);
        when(failedField.verify()).thenReturn(Collections.singletonList(error));
        fields.add(failedField);
        deckService.verifyFormData(importDeckResponse, fields);

        assertThat(importDeckResponse.getId(), is(-1));
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
        assertThat(taskOptions.getStringParams().get("docId").get(0), is("doc id"));
        assertThat(taskOptions.getStringParams().get("audioDirId").get(0), is("audio dir id"));
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
}