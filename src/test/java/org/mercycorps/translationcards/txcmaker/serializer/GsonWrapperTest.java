package org.mercycorps.translationcards.txcmaker.serializer;

import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.FinalizedCard;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class GsonWrapperTest {
    @Test
    public void shouldProduceJson() throws Exception {
        GsonWrapper gsonWrapper = new GsonWrapper();
        FinalizedCard finalizedCard = new FinalizedCard()
                .setDest_txt("dest_txt")
                .setCard_label("card_label")
                .setDest_audio("dest_audio");

        String json = gsonWrapper.toJson(finalizedCard);

        assertThat(json, is("{\"card_label\":\"card_label\",\"dest_audio\":\"dest_audio\",\"dest_txt\":\"dest_txt\"}"));
    }
}
