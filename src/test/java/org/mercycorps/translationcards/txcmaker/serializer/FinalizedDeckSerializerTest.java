package org.mercycorps.translationcards.txcmaker.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.FinalizedDeck;
import org.mercycorps.translationcards.txcmaker.model.FinalizedLanguage;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FinalizedDeckSerializerTest {

    private FinalizedDeckSerializer finalizedDeckSerializer;
    private FinalizedDeck finalizedDeck;
    private JsonSerializationContext context;

    @Before
    public void setUp() throws Exception {
        finalizedDeckSerializer = new FinalizedDeckSerializer();
        finalizedDeck = new FinalizedDeck();
        context = mock(JsonSerializationContext.class);
    }

    @Test
    public void shouldSerializeLabel() {
        final String deckLabel = "label";
        finalizedDeck.deck_label = deckLabel;

        JsonObject json = (JsonObject) finalizedDeckSerializer.serialize(finalizedDeck, null, context);

        assertThat(json.get("deck_label").getAsString(), is(deckLabel));
    }
    
    @Test
    public void shouldSerializeId() {
        final String id = "an id";
        finalizedDeck.id = id;

        JsonObject json = (JsonObject) finalizedDeckSerializer.serialize(finalizedDeck, null, context);

        assertThat(json.get("id").getAsString(), is(id));
    }

    @Test
    public void shouldSerializeLanguages() {
        ArrayList<FinalizedLanguage> finalizedLanguages = new ArrayList<>();
        finalizedDeck.languages = finalizedLanguages;

        JsonSerializationContext jsonSerializationContext = context;
        finalizedDeckSerializer.serialize(finalizedDeck, null, jsonSerializationContext);

        verify(jsonSerializationContext).serialize(finalizedLanguages);
    }

    @Test
    public void shouldSerializeLicenseURL() {
        final String licenseURL = "a license url";
        finalizedDeck.license_url= licenseURL;

        JsonObject json = (JsonObject) finalizedDeckSerializer.serialize(finalizedDeck, null, context);

        assertThat(json.get("license-url").getAsString(), is(licenseURL));
    }

    @Test
    public void shouldSerializeLocked() {
        final boolean locked = true;
        finalizedDeck.locked = locked;

        JsonObject json = (JsonObject) finalizedDeckSerializer.serialize(finalizedDeck, null, context);

        assertThat(json.get("locked").getAsBoolean(), is(locked));
    }

    @Test
    public void shouldSerializePublisher() {
        final String publisher = "a publisher";
        finalizedDeck.publisher= publisher;

        JsonObject json = (JsonObject) finalizedDeckSerializer.serialize(finalizedDeck, null, context);

        assertThat(json.get("publisher").getAsString(), is(publisher));
    }

    @Test
    public void shouldSerializeReadme() {
        final String readme = "a readme";
        finalizedDeck.readme = readme;

        JsonObject json = (JsonObject) finalizedDeckSerializer.serialize(finalizedDeck, null, context);

        assertThat(json.get("readme").getAsString(), is(readme));
    }

    @Test
    public void shouldSerializeTimestamp() {
        final long timestamp = 1L;
        finalizedDeck.timestamp = timestamp;

        JsonObject json = (JsonObject) finalizedDeckSerializer.serialize(finalizedDeck, null, context);

        assertThat(json.get("timestamp").getAsLong(), is(timestamp));
    }

    @Test
    public void shouldSerializeSourceLanguage() {
        final String sourceLanguage = "a source language";
        finalizedDeck.source_language = sourceLanguage;

        JsonObject json = (JsonObject) finalizedDeckSerializer.serialize(finalizedDeck, null, context);

        assertThat(json.get("source_language").getAsString(), is(sourceLanguage));
    }

    @Test
    @Ignore("is this different than source_language?")
    public void shouldSerializedSourceLanguageName() {
        final String sourceLanguageName = "a source language";
//        finalizedDeck.sourceLanguageName = sourceLanguageName;

        JsonObject json = (JsonObject) finalizedDeckSerializer.serialize(finalizedDeck, null, context);

        assertThat(json.get("source_language_name").getAsString(), is(sourceLanguageName));
    }
}
