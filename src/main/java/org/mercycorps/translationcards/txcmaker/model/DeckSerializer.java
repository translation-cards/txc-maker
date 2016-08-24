package org.mercycorps.translationcards.txcmaker.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class DeckSerializer implements JsonSerializer<Deck> {
    @Override
    public JsonElement serialize(Deck deck, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deck_label", deck.deck_label);
        jsonObject.addProperty("publisher", deck.publisher);
        jsonObject.addProperty("iso_code", deck.iso_code);
        jsonObject.addProperty("language_label", deck.language_label);
        jsonObject.addProperty("id", deck.id);
        jsonObject.addProperty("timestamp", deck.timestamp);
        jsonObject.addProperty("locked", deck.locked);
        jsonObject.add("languages", jsonSerializationContext.serialize(deck.languages));
        if(!deck.errors.isEmpty()) {
            jsonObject.add("errors", jsonSerializationContext.serialize(deck.errors));
        }
        return  jsonObject;
    }
}
