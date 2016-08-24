package org.mercycorps.translationcards.txcmaker.wrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.CardSerializer;
import org.mercycorps.translationcards.txcmaker.model.DeckSerializer;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.springframework.stereotype.Component;

@Component
public class GsonWrapper {

    private final Gson gson;

    public GsonWrapper() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Card.class, new CardSerializer())
                .registerTypeAdapter(Deck.class, new DeckSerializer())
                .create();
    }

    public String toJson(Object object) {
        return gson.toJson(object);
    }

    public <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }
}
