package org.mercycorps.translationcards.txcmaker.service;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.ImportDeckForm;
import org.mercycorps.translationcards.txcmaker.response.ImportDeckResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TaskServiceTest {

    private String sessionId;
    private String channelToken;
    private ImportDeckResponse importDeckResponse;
    private ImportDeckForm importDeckForm;
    Error error;
    private TaskService taskService;
    @Mock
    private ChannelService channelService;
    @Mock
    private Queue taskQueue;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        sessionId = "session id";
        channelToken = "channel token";
        importDeckResponse = new ImportDeckResponse();
        error = new Error("some message", true);
        importDeckForm = new ImportDeckForm()
                .setDeckName("deck name")
                .setAudioDirId("audio dir id string")
                .setDocId("doc id string")
                .setPublisher("publisher");
        
        taskService = new TaskService(channelService, taskQueue);
    }

    @Test
    public void kickoffVerifyDeckTask_shouldAssignAnIdToTheNewDeck() throws Exception {
        taskService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);

        assertThat(importDeckResponse.getId(), is(sessionId));
    }

    @Test
    public void kickoffVerifyDeckTask_shouldAssignAnInvalidIdWhenThereAreErrors() throws Exception {
        importDeckResponse.addError(error);

        taskService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);

        assertThat(importDeckResponse.getId(), is(""));
    }

    @Test
    public void kickoffVerifyDeckTask_shouldCreateAChannel() throws Exception {
        taskService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);

        verify(channelService).createChannel(sessionId);
    }

    @Test
    public void kickoffVerifyDeckTask_shouldAddTheChannelTokenToTheResponse() throws Exception {
        when(channelService.createChannel(sessionId)).thenReturn(channelToken);
        taskService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);

        assertThat(importDeckResponse.getChannelToken(), is(channelToken));
    }

    @Test
    public void kickoffVerifyDeckTask_shouldAddTheVerifyDeckTaskToTheQueue() throws Exception {
        taskService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);

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

        taskService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);

        verify(channelService, times(0)).createChannel(sessionId);
        assertNull(importDeckResponse.getChannelToken());
        verify(taskQueue, times(0)).add(any(TaskOptions.class));
    }

    @Test
    public void kickoffBuildDeckTask_shouldAddTheBuildDeckTaskToTheQueue() throws Exception {
        taskService.kickoffBuildDeckTask(sessionId);

        ArgumentCaptor<TaskOptions> taskOptionsArgumentCaptor = ArgumentCaptor.forClass(TaskOptions.class);
        verify(taskQueue).add(taskOptionsArgumentCaptor.capture());

        TaskOptions taskOptions = taskOptionsArgumentCaptor.getValue();
        assertThat(taskOptions.getUrl(), is("/tasks/txc-build"));
        assertThat(taskOptions.getHeaders().get("Content-Type").get(0), is("text/plain"));
        assertThat(taskOptions.getPayload(), is(sessionId.getBytes("UTF8")));
    }

}