package org.mercycorps.translationcards.txcmaker.model.deck;

import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.Error;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DeckTest {

    Deck deck;

    @Before
    public void setUp() throws Exception {
        deck =  new Deck();
    }

    @Test
    public void shouldAddACardToANewLanguage() throws Exception {
        final Card card = new Card();
        deck.addCard("ar", "Arabic", card);

        assertThat(deck.languages.size(), is(1));
        assertThat(deck.languages.get(0).iso_code, is("ar"));
    }

    @Test
    public void shouldCreateANewLanguageWhenLanguageLabelsDontMatch() throws Exception {
        final Card card1 = new Card();
        final Card card2 = new Card();
        deck.addCard("ar", "Arabic", card1);
        deck.addCard("ar", "ArabicSomethingElse", card2);

        assertThat(deck.languages.size(), is(2));
        assertThat(deck.languages.get(0).iso_code, is("ar"));
        assertThat(deck.languages.get(1).iso_code, is("ar"));
    }

    @Test
    public void shouldReturnNumberOfErrors() {
        deck.errors = newArrayList(new Error("error message 1", true), new Error("error message 2", true));
        assertThat(deck.getNumberOfErrors(), is(2));
    }
}
