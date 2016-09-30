package org.mercycorps.translationcards.txcmaker.wrapper;

import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Card;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class GsonWrapperTest {
    @Test
    public void shouldProduceJson() throws Exception {
        GsonWrapper gsonWrapper = new GsonWrapper();
        Card card = new Card().setSourcePhrase("Are you hurt?");

        String json = gsonWrapper.toJson(card);

        assertThat(json, is("{\"source_phrase\":\"Are you hurt?\"}"));
    }
}
