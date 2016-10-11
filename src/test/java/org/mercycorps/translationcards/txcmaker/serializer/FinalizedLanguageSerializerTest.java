package org.mercycorps.translationcards.txcmaker.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.FinalizedCard;
import org.mercycorps.translationcards.txcmaker.model.FinalizedLanguage;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FinalizedLanguageSerializerTest {

    private JsonSerializationContext context;
    private FinalizedLanguageSerializer serializer;
    private FinalizedLanguage finalizedLanguage;

    @Before
    public void setUp() throws Exception {
        context = mock(JsonSerializationContext.class);
        serializer = new FinalizedLanguageSerializer();
        finalizedLanguage = new FinalizedLanguage();
    }

    @Test
    public void shouldSerializeIsoCode() {
        finalizedLanguage.iso_code = "Iso Code";

        JsonObject json = (JsonObject) serializer.serialize(finalizedLanguage, null, context);

        assertThat(json.get("iso_code").getAsString(), is("Iso Code"));
    }

    @Test
    public void shouldSerializedCards() {
        ArrayList<FinalizedCard> cards = new ArrayList<FinalizedCard>() {{
            add(new FinalizedCard());
        }};
        finalizedLanguage.cards = cards;

        serializer.serialize(finalizedLanguage, null, context);

        verify(context).serialize(cards);
    }

    @Test
    @Ignore("i think this is just renaming the label field")
    public void shouldSerializeDestinationLanguageName() {
        final String destinationLanguageName = "Arabic";
//        finalizedLanguage.destinationLanguageName = destinationLanguageName;

        JsonObject json = (JsonObject) serializer.serialize(finalizedLanguage, null, context);

        assertThat(json.get("dest_language_name").getAsString(), is(destinationLanguageName));
    }
}
