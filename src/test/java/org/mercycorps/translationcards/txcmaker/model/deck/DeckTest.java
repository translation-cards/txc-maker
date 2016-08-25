package org.mercycorps.translationcards.txcmaker.model.deck;

import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Card;

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
        assertThat(deck.languages.get(0).cards.get(0), is(card));
    }

    @Test
    public void shouldCreateANewLanguageWhenLanguageLabelsDontMatch() throws Exception {
        final Card card1 = new Card();
        final Card card2 = new Card();
        deck.addCard("ar", "Arabic", card1);
        deck.addCard("ar", "ArabicSomethingElse", card2);

        assertThat(deck.languages.size(), is(2));
        assertThat(deck.languages.get(0).iso_code, is("ar"));
        assertThat(deck.languages.get(0).cards.get(0), is(card1));
        assertThat(deck.languages.get(1).iso_code, is("ar"));
        assertThat(deck.languages.get(1).cards.get(0), is(card2));
    }

    @Test
    public void shouldAddACardToAnExistingLanguage() throws Exception {
        final Card card1 = new Card();
        final Card card2 = new Card();
        deck.addCard("ar", "Arabic", card1);
        deck.addCard("ar", "Arabic", card2);

        assertThat(deck.languages.size(), is(1));
        assertThat(deck.languages.get(0).iso_code, is("ar"));
        assertThat(deck.languages.get(0).cards.get(0), is(card1));
        assertThat(deck.languages.get(0).cards.get(1), is(card2));
    }


}