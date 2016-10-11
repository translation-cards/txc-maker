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
    private NewCard helloInSpanish;
    private NewCard helloInArabic;
    private Translation translation;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        helloInSpanish = new NewCard("Hello", "helloEs.wav", "Hola", new ArrayList<Error>(), SPANISH);
        helloInArabic  = new NewCard("Hello", "helloAr.wav", "هتاف للترحيب", new ArrayList<Error>(), ARABIC);
        List<NewCard> cards = new ArrayList<NewCard>();
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
        Translation emptyTranslation = new Translation(new ArrayList<NewCard>());
        assertThat(emptyTranslation.getSourcePhrase(), is(""));
    }

    @Test
    public void shouldBeValidWhenAtLeastOneCardHasAudio() {
        NewCard cardWithAudio = new NewCard("Hello", "hola.wav", "Hola", new ArrayList<Error>(), SPANISH);
        NewCard cardWithoutAudio = new NewCard("Hello", null, "هتاف للترحيب", new ArrayList<Error>(), ARABIC);
        List<NewCard> cards = new ArrayList<>();
        cards.add(cardWithAudio);
        cards.add(cardWithoutAudio);
        translation = new Translation(cards);

        assertThat(translation.isValid(), is(true));
    }

    @Test
    public void shouldBeInvalidIfNoCardsHaveAudioFile() {
        Translation invalidTranslation = new Translation(new ArrayList<NewCard>());

        assertThat(invalidTranslation.isValid(), is(false));
    }
}