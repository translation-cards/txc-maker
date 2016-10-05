package org.mercycorps.translationcards.txcmaker.wrapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.mercycorps.translationcards.txcmaker.model.FinalizedLanguage;

import java.lang.reflect.Type;

public class FinalizedLanguageSerializer implements JsonSerializer<FinalizedLanguage> {
    @Override
    public JsonElement serialize(FinalizedLanguage finalizedLanguage, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("iso_code", finalizedLanguage.iso_code);
        json.add("cards", context.serialize(finalizedLanguage.cards));
        return json;
    }
}
