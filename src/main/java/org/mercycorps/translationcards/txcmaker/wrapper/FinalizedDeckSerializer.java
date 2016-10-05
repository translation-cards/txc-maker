package org.mercycorps.translationcards.txcmaker.wrapper;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.mercycorps.translationcards.txcmaker.model.FinalizedDeck;

import java.lang.reflect.Type;

public class FinalizedDeckSerializer implements JsonSerializer<FinalizedDeck> {

    @Override
    public JsonElement serialize(FinalizedDeck finalizedDeck, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("source_language", finalizedDeck.source_language);
        json.addProperty("deck_label", finalizedDeck.deck_label);
        json.addProperty("publisher", finalizedDeck.publisher);
        json.addProperty("id", finalizedDeck.id);
        json.addProperty("timestamp", finalizedDeck.timestamp);
        json.addProperty("locked", finalizedDeck.locked);
        json.addProperty("license-url", finalizedDeck.license_url);
        json.addProperty("readme", finalizedDeck.readme);
        json.add("languages", context.serialize(finalizedDeck.languages));
        return json;
    }
}
