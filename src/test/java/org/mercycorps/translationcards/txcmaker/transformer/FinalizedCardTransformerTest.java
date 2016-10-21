package org.mercycorps.translationcards.txcmaker.transformer;

import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.FinalizedCard;
import org.mercycorps.translationcards.txcmaker.model.NewCard;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class FinalizedCardTransformerTest {

    private FinalizedCardTransformer transformer = new FinalizedCardTransformer();

    @Test
    public void shouldTransformNewCardsToFinalizedCards() {
        NewCard helloInSpanish = new NewCard("Hello", "hola.wav", "Hola", null);

        FinalizedCard actual = transformer.transform(helloInSpanish);

        assertThat(actual.card_label, is("Hello"));
        assertThat(actual.dest_audio, is("hola.wav"));
        assertThat(actual.dest_txt, is("Hola"));
    }
}