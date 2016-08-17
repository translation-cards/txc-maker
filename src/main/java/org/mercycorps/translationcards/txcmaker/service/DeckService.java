package org.mercycorps.translationcards.txcmaker.service;


import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.mercycorps.translationcards.txcmaker.resource.CreateDeckResponse;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Field;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.ImportDeckForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeckService {

    private ChannelService channelService;
    private Queue taskQueue;

    @Autowired
    public DeckService(ChannelService channelService, Queue taskQueue) {
        this.channelService = channelService;
        this.taskQueue = taskQueue;
    }

    public void verifyFormData(CreateDeckResponse createDeckResponse, List<Field> fields) {
        for(Field field : fields) {
            createDeckResponse.addErrors(field.verify());
        }

        if (createDeckResponse.hasErrors()) {
            createDeckResponse.setId(-1);
        } else {
            createDeckResponse.setId(10);
        }
    }

    public void kickoffVerifyDeckTask(CreateDeckResponse createDeckResponse, String sessionId, ImportDeckForm importDeckForm) {
        if(createDeckResponse.hasErrors()) {
            return;
        }
        String token = channelService.createChannel(sessionId);
        createDeckResponse.setChannelToken(token);
        TaskOptions taskOptions = buildTaskOptions(sessionId, importDeckForm);
        taskQueue.add(taskOptions);
    }

    private TaskOptions buildTaskOptions(String sessionId, ImportDeckForm importDeckForm) {
        return TaskOptions.Builder
                .withUrl("/tasks/txc-verify")
                .param("sessionId", sessionId)
                .param("deckName", importDeckForm.getDeckName())
                .param("publisher", importDeckForm.getPublisher())
                .param("docId", importDeckForm.getDocId())
                .param("audioDirId", importDeckForm.getAudioDirId())
                .param("deckId", "deck id")
                .param("licenseUrl", "license url");
    }
}
