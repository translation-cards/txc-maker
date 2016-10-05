package org.mercycorps.translationcards.txcmaker.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class FinalizedCardSerializer implements JsonSerializer<FinalizedCard> {
    @Override
    public JsonElement serialize(FinalizedCard finalizedCard, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("card_label", finalizedCard.card_label);
        jsonObject.addProperty("dest_audio", finalizedCard.dest_audio);
        jsonObject.addProperty("dest_txt", finalizedCard.dest_txt);
        return jsonObject;
    }
}
