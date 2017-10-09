package org.mercycorps.translationcards.txcmaker.service;

import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mercycorps.translationcards.txcmaker.transformer.FinalizedLanguageTransformer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FinalizedDeckFactoryTest {

    private FinalizedLanguageTransformer transformer = mock(FinalizedLanguageTransformer.class);
    private FinalizedDeckFactory finalizedDeckFactory = new FinalizedDeckFactory(transformer);

    @Test
    public void shouldUseFinalizedDeckTransformerToCreateFinalizedLanguages() {
        Deck deck = mock(Deck.class);
        finalizedDeckFactory.finalize(deck);

        verify(transformer).transform(deck);
    }
}