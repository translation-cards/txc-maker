package org.mercycorps.translationcards.txcmaker.service;

import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Deck;

import static org.junit.Assert.*;

public class DeckServiceTest {

    DeckService deckService;

    @Before
    public void setup() {
        deckService = new DeckService();
    }

    @Test
    public void shouldGetADeck() throws Exception {
        Deck deck = deckService.get(1);

        assertNotNull(deck);
    }
}