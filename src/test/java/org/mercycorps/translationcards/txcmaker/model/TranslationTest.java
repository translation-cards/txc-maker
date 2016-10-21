package org.mercycorps.translationcards.txcmaker.model;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TranslationTest {

    private final Language SPANISH = new Language("es", "Spanish");
    private final Language ARABIC = new Language("ar", "Arabic");
    private Card helloInSpanish;
    private Card helloInArabic;
    private Translation translation;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        helloInSpanish = new Card("Hello", "helloEs.wav", "Hola", SPANISH);
        helloInArabic  = new Card("Hello", "helloAr.wav", "هتاف للترحيب", ARABIC);
        List<Card> cards = new ArrayList<Card>();
        cards.add(helloInSpanish);
        cards.add(helloInArabic);

        translation = new Translation(cards);
    }

    @Test
    public void shouldFindCardThatTranslatesIntoSpanish() {
        assertThat(translation.containsCardForLanguage("Spanish"), is(true));
    }

    @Test
    public void shouldNotFindCardThatTranslatesIntoLatin() {
        assertThat(translation.containsCardForLanguage("Latin"), is(false));
    }

    @Test
    public void shouldGetCardForSpanish() {
        assertThat(translation.getCardForLanguage("Spanish"), is(helloInSpanish));
    }

    @Test
    public void shouldGetCardForArabic() {
        assertThat(translation.getCardForLanguage("Arabic"), is(helloInArabic));
    }

    @Test
    public void shouldThrowARuntimeExceptionWhenACardCannotBeFound() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Cannot find translation for Hello in Latin");
        translation.getCardForLanguage("Latin");
    }

    @Test
    public void shouldDetermineSourcePhraseFromTheCards() {
        assertThat(translation.getSourcePhrase(), is("Hello"));
    }

    @Test
    public void sourcePhraseShouldBeEmptyWhenCardsAreEmpty() {
        Translation emptyTranslation = new Translation(new ArrayList<Card>());
        assertThat(emptyTranslation.getSourcePhrase(), is(""));
    }

    @Test
    public void shouldBeValidWhenAtLeastOneCardHasAudio() {
        Card cardWithAudio = new Card("Hello", "hola.wav", "Hola", SPANISH);
        Card cardWithoutAudio = new Card("Hello", null, "هتاف للترحيب", ARABIC);
        List<Card> cards = new ArrayList<>();
        cards.add(cardWithAudio);
        cards.add(cardWithoutAudio);
        translation = new Translation(cards);

        assertThat(translation.isValid(), is(true));
    }

    @Test
    public void shouldBeInvalidIfNoCardsHaveAudioFile() {
        Translation invalidTranslation = new Translation(new ArrayList<Card>());

        assertThat(invalidTranslation.isValid(), is(false));
    }
}