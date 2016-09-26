package org.mercycorps.translationcards.txcmaker.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;

import java.lang.reflect.Type;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;


public class DeckSerializerTest {

    @Test
    public void testNumberOfErrors() throws Exception {

        JsonSerializationContext mockJsonSerializationContext = mock(JsonSerializationContext.class);
        Type mockType = mock(Type.class);
        Deck deck = new Deck();
        Error error1 = new Error("test error 1", true);
        Error error2 = new Error("test error 2", true);
        deck.errors = newArrayList(error1, error2);
        DeckSerializer serializer = new DeckSerializer();
        JsonObject result = (JsonObject)serializer.serialize(deck, mockType, mockJsonSerializationContext);
        assertThat(result.get("numberOfErrors").getAsInt(), is(2));
    }
}