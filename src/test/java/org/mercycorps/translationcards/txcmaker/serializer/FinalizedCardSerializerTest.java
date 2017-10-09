package org.mercycorps.translationcards.txcmaker.serializer;

import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.FinalizedCard;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class FinalizedCardSerializerTest {
    private FinalizedCard finalizedCard;
    private FinalizedCardSerializer finalizedCardSerializer;

    @Before
    public void setUp() {
        finalizedCardSerializer = new FinalizedCardSerializer();
        finalizedCard = new FinalizedCard();
    }

    @Test
    public void shouldIncludeACardLabelField() {
        final String cardLabel = "Are you cold?";
        finalizedCard.card_label = cardLabel;

        JsonObject actual = (JsonObject) finalizedCardSerializer.serialize(finalizedCard, null, null);

        assertThat(actual.get("card_label").getAsString(), is(cardLabel));
    }

    @Test
    public void shouldIncludeADestinationAudioField() {
        final String destinationAudio = "audio.mp3";
        finalizedCard.dest_audio = destinationAudio;

        JsonObject actual = (JsonObject) finalizedCardSerializer.serialize(finalizedCard, null, null);

        assertThat(actual.get("dest_audio").getAsString(), is(destinationAudio));
    }

    @Test
    public void shouldIncludeADestinationTextField() {
        final String destinationText = "Destination text";
        finalizedCard.dest_txt = destinationText;

        JsonObject actual = (JsonObject) finalizedCardSerializer.serialize(finalizedCard, null, null);

        assertThat(actual.get("dest_txt").getAsString(), is(destinationText));
    }
}