package org.mercycorps.translationcards.txcmaker.service;


import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.mercycorps.translationcards.txcmaker.api.response.CreateDeckResponse;
import org.mercycorps.translationcards.txcmaker.api.response.RetrieveDeckResponse;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Field;

import java.util.Arrays;
import java.util.List;


public class DeckService {

    private ChannelService channelService;
    private Queue taskQueue;

    public DeckService(ChannelService channelService, Queue taskQueue) {
        this.channelService = channelService;
        this.taskQueue = taskQueue;
    }

    public void retrieve(int id, RetrieveDeckResponse retrieveDeckResponse) {
        if(id == 10) {
            retrieveDeckResponse.setDeck(Deck.STUBBED_DECK);
        }
    }

    public List<Deck> retrieveAll() {
        return Arrays.asList(Deck.STUBBED_DECK);
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

    public void kickoffVerifyDeckTask(CreateDeckResponse createDeckResponse, String sessionId) {
        if(createDeckResponse.hasErrors()) {
            return;
        }
        String token = channelService.createChannel(sessionId);
        createDeckResponse.setChannelToken(token);
        TaskOptions taskOptions = TaskOptions.Builder
                .withUrl("/tasks/txc-verify")
                .param("sessionId", sessionId);
        taskQueue.add(taskOptions);
    }
}
