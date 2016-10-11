package org.mercycorps.translationcards.txcmaker.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NewDeckTest {

    private NewDeck deck;
    private List<NewCard> cards = new ArrayList<>();

    private final Language ENGLISH = new Language("en", "English");
    private final Language ARABIC = new Language("ar", "Arabic");
    private final Language SPANISH = new Language("es", "Spanish");

    private final NewCard helloInSpanish = new NewCard("Hello", "helloEs.mp3", "Hola", new ArrayList<Error>(), SPANISH);
    private final NewCard goodbyeInArabic = new NewCard("Goodbye", "goodbyeAr.mp3", "وداع", new ArrayList<Error>(), ARABIC);
    private final NewCard helloInArabic = new NewCard("Hello", "helloAr.mp3", "هتاف للترحيب", new ArrayList<Error>(), ARABIC);

    @Before
    public void setUp() {
        deck = new NewDeck(null, null, null, 0L, false, null, null, new ArrayList<NewCard>());
    }

    @Test
    public void shouldCreateOneTranslationFromOneCard() {
        cards = new ArrayList<NewCard>() {{
            add(helloInSpanish);
        }};

        List<Translation> actual = deck.buildTranslationsFromCards(cards);

        assertThat(actual.size(), is(1));
        assertThat(actual.get(0).getSourcePhrase(), is("Hello"));
    }

    @Test
    public void shouldAddTwoSeparateTranslations() {
        cards = new ArrayList<NewCard>() {{
            add(helloInSpanish);
            add(goodbyeInArabic);
        }};

        List<Translation> actual = deck.buildTranslationsFromCards(cards);

        assertThat(actual.size(), is(2));
    }

    @Test
    public void shouldGroupTranslationsByTheirSourcePhrase() {
        cards = new ArrayList<NewCard>() {{
            add(helloInSpanish);
            add(helloInArabic);
        }};

        List<Translation> actual = deck.buildTranslationsFromCards(cards);

        assertThat(actual.size(), is(1));
        assertThat(actual.get(0).getCards().size(), is(2));
    }

    @Test
    public void shouldHaveOnlySpanishInDestinationLanguageNames() {
        cards = new ArrayList<NewCard>() {{
            add(helloInSpanish);
        }};

        List<String> actual = deck.buildDestinationLanguageNames(cards);

        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is("Spanish"));
    }

    @Test
    public void shouldHaveBothArabicAndSpanishInDestinationLanguageNames() {
        cards = new ArrayList<NewCard>() {{
            add(helloInSpanish);
            add(helloInArabic);
        }};

        List<String> actual = deck.buildDestinationLanguageNames(cards);

        assertThat(actual.size(), is(2));
        assertThat(actual.contains("Spanish"), is(true));
        assertThat(actual.contains("Arabic"), is(true));
    }
}
