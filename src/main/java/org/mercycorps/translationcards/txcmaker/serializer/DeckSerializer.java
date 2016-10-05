package org.mercycorps.translationcards.txcmaker.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;

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
        jsonObject.addProperty("license_url", deck.license_url);
        jsonObject.addProperty("numberOfErrors", deck.getNumberOfErrors());
        jsonObject.add("languages", jsonSerializationContext.serialize(deck.languages));
        return  jsonObject;
    }
}
