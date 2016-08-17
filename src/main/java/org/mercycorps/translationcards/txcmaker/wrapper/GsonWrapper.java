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

    public String toJson(@Nullable Object object) {
        return gson.toJson(object);
    }
}
