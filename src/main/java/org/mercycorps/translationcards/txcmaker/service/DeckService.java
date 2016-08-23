package org.mercycorps.translationcards.txcmaker.service;


import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.mercycorps.translationcards.txcmaker.resource.ImportDeckResponse;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Field;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.ImportDeckForm;
import org.mercycorps.translationcards.txcmaker.task.TxcMakerParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeckService {

    private ChannelService channelService;
    private Queue taskQueue;
    private TxcMakerParser txcMakerParser;

    @Autowired
    public DeckService(ChannelService channelService, Queue taskQueue, TxcMakerParser txcMakerParser) {
        this.channelService = channelService;
        this.taskQueue = taskQueue;
        this.txcMakerParser = txcMakerParser;
    }

    public void preProcessForm(ImportDeckForm importDeckForm) {
        String documentId = txcMakerParser.parseDocId(importDeckForm.getDocId());
        importDeckForm.setDocId(documentId);
        String audioDirectoryId = txcMakerParser.parseAudioDirId(importDeckForm.getAudioDirId());
        importDeckForm.setAudioDirId(audioDirectoryId);
    }

    public void verifyFormData(ImportDeckResponse importDeckResponse, List<Field> fields) {
        for(Field field : fields) {
            importDeckResponse.addErrors(field.verify());
        }
    }

    public void kickoffVerifyDeckTask(ImportDeckResponse importDeckResponse, String sessionId, ImportDeckForm importDeckForm) {
        if(importDeckResponse.hasErrors()) {
            importDeckResponse.setId("");
            return;
        }
        importDeckResponse.setId(sessionId);
        String token = channelService.createChannel(sessionId);
        importDeckResponse.setChannelToken(token);
        TaskOptions taskOptions = verifyDeckTaskOptions(sessionId, importDeckForm);
        taskQueue.add(taskOptions);
    }

    private TaskOptions verifyDeckTaskOptions(String sessionId, ImportDeckForm importDeckForm) {
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

    public void kickoffBuildDeckTask(String sessionId) {
        TaskOptions taskOptions = buildDeckTaskOptions(sessionId);
        taskQueue.add(taskOptions);
    }

    private TaskOptions buildDeckTaskOptions(String sessionId) {
        return TaskOptions.Builder
                .withUrl("/tasks/txc-build")
                .header("Content-Type", "text/plain")
                .payload(sessionId);
    }
}
