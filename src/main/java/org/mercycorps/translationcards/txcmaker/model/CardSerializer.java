package org.mercycorps.translationcards.txcmaker.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class CardSerializer implements JsonSerializer<Card> {
    @Override
    public JsonElement serialize(Card card, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("card_label", card.card_label);
        jsonObject.addProperty("dest_audio", card.dest_audio);
        jsonObject.addProperty("dest_txt", card.dest_txt);
        if(card.errors != null && !card.errors.isEmpty()) {
            jsonObject.add("errors", jsonSerializationContext.serialize(card.errors));
        }
        return jsonObject;
    }
}
