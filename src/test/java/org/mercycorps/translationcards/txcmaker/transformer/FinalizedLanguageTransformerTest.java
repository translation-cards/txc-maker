package org.mercycorps.translationcards.txcmaker.transformer;

import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;


/**
 * Something to note: since the {@link org.mercycorps.translationcards.txcmaker.transformer.FinalizedLanguageTransformer}
 * needs to reach into each of the {@link org.mercycorps.translationcards.txcmaker.model.NewCard},
 * {@link org.mercycorps.translationcards.txcmaker.model.Translation}, and
 * {@link org.mercycorps.translationcards.txcmaker.model.Language} class this test is closer to an integration test
 * than a true unit test
 */
public class FinalizedLanguageTransformerTest {

    private FinalizedCardTransformer cardTransformer = mock(FinalizedCardTransformer.class);
    private FinalizedLanguageTransformer transformer = new FinalizedLanguageTransformer(cardTransformer);

    private final Language SPANISH = new Language("es", "Spanish");
    private final Language FRENCH = new Language("fr", "French");

    private final NewCard helloInSpanish = new NewCard("Hello", "hola.wav", "Hola", SPANISH);

    @Test
    public void shouldTransformEmptyDeckToEmptyListOfFinalizedLanguages() {
        NewDeck emptyDeck = new NewDeck(null, null, null, 0L, false, null, null, null, new ArrayList<Error>(), new ArrayList<Translation>(), new ArrayList<String>());

        List<FinalizedLanguage> finalizedLanguages = transformer.transform(emptyDeck);

        assertThat(finalizedLanguages.isEmpty(), is(true));
    }

    @Test
    public void aDeckWithOneCardShouldTransformIntoAListOfOneLanguage() {
        Translation translation = new Translation(newArrayList(helloInSpanish));
        NewDeck deck = new NewDeck(null, null, null, 0L, false, null, null, null, new ArrayList<Error>(), newArrayList(translation), new ArrayList<String>());

        List<FinalizedLanguage> languages = transformer.transform(deck);

        assertThat(languages.size(), is(1));
    }

    @Test
    public void shouldUseTheCardTransformerToTransformOneCard() {
        Translation translation = new Translation(newArrayList(helloInSpanish));

        transformer.organizeCardsByLanguage(newArrayList(translation));

        verify(cardTransformer).transform(helloInSpanish);
    }

    @Test
    public void shouldUseTheCardTransformerToTransformMultipleCards() {
        Translation translation = new Translation(newArrayList(helloInSpanish, helloInSpanish, helloInSpanish));

        transformer.organizeCardsByLanguage(newArrayList(translation));

        verify(cardTransformer, times(3)).transform(helloInSpanish);
    }

    @Test
    public void shouldUseTheCardTransformerOnEachTranslation() {
        Translation translationOne = new Translation(newArrayList(helloInSpanish));
        Translation translationTwo = new Translation(newArrayList(helloInSpanish));

        transformer.organizeCardsByLanguage(newArrayList(translationOne, translationTwo));

        verify(cardTransformer, times(2)).transform(helloInSpanish);
    }

    @Test
    public void withOnlyOneLanguageShouldHaveOutputWithOnlyOneLanguage() {
        Translation translation = new Translation(newArrayList(helloInSpanish));

        Map<Language, List<FinalizedCard>> finalizedLanguages = transformer.organizeCardsByLanguage(newArrayList(translation));

        assertThat(finalizedLanguages.keySet().size(), is(1));
        assertThat(finalizedLanguages.keySet().contains(helloInSpanish.getDestinationLanguage()), is(true));
    }

    @Test
    public void shouldPlaceTheOutputFromFinalizedCardTransformerInFinalizedLanguageMap() {
        FinalizedCard arbitraryCard = mock(FinalizedCard.class);
        when(cardTransformer.transform(helloInSpanish)).thenReturn(arbitraryCard);

        Translation translation = new Translation(newArrayList(helloInSpanish));

        Map<Language, List<FinalizedCard>> finalizedLanguages = transformer.organizeCardsByLanguage(newArrayList(translation));

        assertThat(finalizedLanguages.get(SPANISH).get(0), is(arbitraryCard));
    }
    
    @Test
    public void aDeckWithOneLanguageAndTwoCardsShouldResultInAFinalizedLanguageWithTwoMatchingCards() {
        Translation translation = new Translation(newArrayList(helloInSpanish, helloInSpanish));

        Map<Language, List<FinalizedCard>> finalizedLanguages = transformer.organizeCardsByLanguage(newArrayList(translation));

        assertThat(finalizedLanguages.get(SPANISH).size(), is(2));
    }

    @Test
    public void shouldCreateAListOfOneFinalizedLanguageWithOneCard() {
        Map<Language, List<FinalizedCard>> cardsByLanguage = new HashMap<>();
        FinalizedCard card = new FinalizedCard();
        cardsByLanguage.put(SPANISH, newArrayList(card));

        List<FinalizedLanguage> finalizedLanguages = transformer.finalizeLanguages(cardsByLanguage);

        assertThat(finalizedLanguages.size(), is(1));
    }

    @Test
    public void shouldCreateAFinalizedLanguageFromOneEntryInTheMap() {
        Map<Language, List<FinalizedCard>> cardsByLanguage = new HashMap<>();
        FinalizedCard card = new FinalizedCard();
        cardsByLanguage.put(SPANISH, newArrayList(card));

        List<FinalizedLanguage> finalizedLanguages = transformer.finalizeLanguages(cardsByLanguage);

        assertThat(finalizedLanguages.get(0).iso_code, is(SPANISH.iso_code));
        assertThat(finalizedLanguages.get(0).cards.size(), is(1));
        assertThat(finalizedLanguages.get(0).cards.get(0), is(card));
    }

    @Test
    public void shouldCreateTwoFinalizedLanguagesWithTwoEntriesInMap() {
        Map<Language, List<FinalizedCard>> cardsByLanguage = new HashMap<>();
        FinalizedCard spanishHello = new FinalizedCard().setCard_label("Hello").setDest_txt("Hola");
        FinalizedCard frenchHello = new FinalizedCard().setCard_label("Hello").setDest_txt("Bonjour");
        cardsByLanguage.put(SPANISH, newArrayList(spanishHello));
        cardsByLanguage.put(FRENCH, newArrayList(frenchHello));

        List<FinalizedLanguage> finalizedLanguages = transformer.finalizeLanguages(cardsByLanguage);

        assertThat(finalizedLanguages.size(), is(2));
    }

    @Test
    public void shouldHaveBothSpanishAndFrenchInListOfLanguages() {
        Map<Language, List<FinalizedCard>> cardsByLanguage = new HashMap<>();
        FinalizedCard spanishHello = new FinalizedCard().setCard_label("Hello").setDest_txt("Hola");
        FinalizedCard frenchHello = new FinalizedCard().setCard_label("Hello").setDest_txt("Bonjour");
        cardsByLanguage.put(SPANISH, newArrayList(spanishHello));
        cardsByLanguage.put(FRENCH, newArrayList(frenchHello));

        List<FinalizedLanguage> finalizedLanguages = transformer.finalizeLanguages(cardsByLanguage);

        boolean bothFrenchAndSpanishRepresented = areBothFrenchAndSpanishRepresented(finalizedLanguages);
        assertThat(bothFrenchAndSpanishRepresented, is(true));
    }

    private boolean areBothFrenchAndSpanishRepresented(List<FinalizedLanguage> finalizedLanguages) {
        FinalizedLanguage firstItem = finalizedLanguages.get(0);
        FinalizedLanguage secondItem = finalizedLanguages.get(1);
        boolean bothItemsAreNotNull = firstItem.iso_code != null && secondItem.iso_code != null;
        boolean bothItemsAreNotEqual = !firstItem.iso_code.equals(secondItem.iso_code);
        return bothItemsAreNotNull
                && bothItemsAreNotEqual
                && itemIsEitherSpanishOrFrench(firstItem)
                && itemIsEitherSpanishOrFrench(secondItem);
    }

    private boolean itemIsEitherSpanishOrFrench(FinalizedLanguage language) {
        return language.iso_code.equals(SPANISH.iso_code) || language.iso_code.equals(FRENCH.iso_code);
    }
}
