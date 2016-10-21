package org.mercycorps.translationcards.txcmaker.model;

import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;

import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeckTest {

    private final ArrayList<Translation> translations = new ArrayList<>();
    private final ArrayList<String> destinationLanguageNames = new ArrayList<>();
    private final ArrayList<Error> parsingErrors = new ArrayList<>();

    @Test
    public void shouldBeValidWithNoTranslations() {
        Deck deck = new Deck(null, null, null, 0L, false, null, null, null, parsingErrors, translations, destinationLanguageNames);

        assertThat(deck.isValid(), is(true));
    }

    @Test
    public void shouldBeValidWithOneValidTranslation() {
        Translation validTranslation = mock(Translation.class);
        when(validTranslation.isValid()).thenReturn(true);
        translations.add(validTranslation);
        Deck deck = new Deck(null, null, null, 0L, false, null, null, null, parsingErrors, translations, destinationLanguageNames);

        assertThat(deck.isValid(), is(true));
    }

    @Test
    public void shouldBeInvalidWithOneInvalidTranslation() {
        Translation validTranslation = mock(Translation.class);
        when(validTranslation.isValid()).thenReturn(false);
        translations.add(validTranslation);
        Deck deck = new Deck(null, null, null, 0L, false, null, null, null, parsingErrors, translations, destinationLanguageNames);

        assertThat(deck.isValid(), is(false));
    }

    @Test
    public void shouldBeInvalidWithOneValidTranslationAndOneInvalidTranslation() {
        Translation translation = mock(Translation.class);
        when(translation.isValid()).thenReturn(true).thenReturn(false);
        translations.add(translation);
        translations.add(translation);
        Deck deck = new Deck(null, null, null, 0L, false, null, null, null, parsingErrors, translations, destinationLanguageNames);

        assertThat(deck.isValid(), is(false));
    }
    
    @Test
    public void shouldGetTranslationBasedOnSourcePhrase() {
        Card hello = new Card("Hello", null, null, null);
        Card goodbye = new Card("Goodbye", null, null, null);

        Translation helloTranslation = new Translation(newArrayList(hello));
        Translation goodbyeTranslation = new Translation(newArrayList(goodbye));

        translations.add(helloTranslation);
        translations.add(goodbyeTranslation);

        Deck deck = new Deck(null, null, null, 0L, false, null, null, null, parsingErrors, translations, destinationLanguageNames);

        assertThat(deck.getTranslationForSourcePhrase("Hello"), is(helloTranslation));
    }
}
