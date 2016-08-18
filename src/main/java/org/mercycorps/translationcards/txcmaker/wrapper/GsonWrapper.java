package org.mercycorps.translationcards.txcmaker.wrapper;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Component
public class GsonWrapper {

    private final Gson gson;

    public GsonWrapper() {
        gson = new Gson();
    }

    public String toJson(Object object) {
        return gson.toJson(object);
    }

    public <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }
}
