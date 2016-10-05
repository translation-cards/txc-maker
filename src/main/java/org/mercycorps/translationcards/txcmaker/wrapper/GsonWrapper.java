package org.mercycorps.translationcards.txcmaker.wrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mercycorps.translationcards.txcmaker.model.*;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.springframework.stereotype.Component;

@Component
public class GsonWrapper {

    private final Gson gson;

    public GsonWrapper() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Card.class, new CardSerializer())
                .registerTypeAdapter(Deck.class, new DeckSerializer())
                .registerTypeAdapter(FinalizedCard.class, new FinalizedCardSerializer())
                .registerTypeAdapter(FinalizedDeck.class, new FinalizedDeckSerializer())
                .registerTypeAdapter(FinalizedLanguage.class, new FinalizedLanguageSerializer())
                .create();
    }

    public String toJson(Object object) {
        return gson.toJson(object);
    }

    public <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }
}
