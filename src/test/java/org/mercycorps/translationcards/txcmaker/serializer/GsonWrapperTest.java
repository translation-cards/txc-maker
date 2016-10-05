package org.mercycorps.translationcards.txcmaker.serializer;

import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Card;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class GsonWrapperTest {
    @Test
    public void shouldProdcuceJson() throws Exception {
        GsonWrapper gsonWrapper = new GsonWrapper();
        Card card = new Card().setLabel("Label");

        String json = gsonWrapper.toJson(card);

        assertThat(json, is("{\"card_label\":\"Label\"}"));
    }
}
