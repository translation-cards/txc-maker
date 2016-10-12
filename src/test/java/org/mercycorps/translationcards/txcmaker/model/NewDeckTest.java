package org.mercycorps.translationcards.txcmaker.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewDeckTest {

    private final ArrayList<Translation> translations = new ArrayList<>();
    private final ArrayList<String> destinationLanguageNames = new ArrayList<>();

    @Test
    public void shouldBeValidWithNoTranslations() {
        NewDeck deck = new NewDeck(null, null, null, 0L, false, null, null, translations, destinationLanguageNames);

        assertThat(deck.isValid(), is(true));
    }

    @Test
    public void shouldBeValidWithOneValidTranslation() {
        Translation validTranslation = mock(Translation.class);
        when(validTranslation.isValid()).thenReturn(true);
        translations.add(validTranslation);
        NewDeck deck = new NewDeck(null, null, null, 0L, false, null, null, translations, destinationLanguageNames);

        assertThat(deck.isValid(), is(true));
    }

    @Test
    public void shouldBeInvalidWithOneInvalidTranslation() {
        Translation validTranslation = mock(Translation.class);
        when(validTranslation.isValid()).thenReturn(false);
        translations.add(validTranslation);
        NewDeck deck = new NewDeck(null, null, null, 0L, false, null, null, translations, destinationLanguageNames);

        assertThat(deck.isValid(), is(false));
    }

    @Test
    public void shouldBeInvalidWithOneValidTranslationAndOneInvalidTranslation() {
        Translation translation = mock(Translation.class);
        when(translation.isValid()).thenReturn(true).thenReturn(false);
        translations.add(translation);
        translations.add(translation);
        NewDeck deck = new NewDeck(null, null, null, 0L, false, null, null, translations, destinationLanguageNames);

        assertThat(deck.isValid(), is(false));
    }
}
