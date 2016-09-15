package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.Drive;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.Language;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VerifyDeckServiceTest {

    @Mock
    Drive mockDrive;

    @Mock
    DriveService mockDriveService;

    @Mock
    VerifyCardService mockVerifyCardService;

    String audioDirectoryId;


    @Before
    public void setup() {
        initMocks(this);

        audioDirectoryId = "doesNotMatterWhatThisValueIs";
    }


    @Test
    public void testFailedVerifications() {

        when(mockVerifyCardService.verifyRequiredValues(any(Card.class))).thenReturn(newArrayList(new Error("requiredValuesError", true)));
        when(mockVerifyCardService.verifyAudioFilename(any(Card.class), any(List.class))).thenReturn(new Error("audioFilenameError", true));
        when(mockVerifyCardService.verifyDuplicateAudioFile(any(Card.class), any(List.class))).thenReturn(new Error("duplicateFilenameError", true));

        VerifyDeckService verifyDeckService = new VerifyDeckService(mockDriveService, mockVerifyCardService);
        Deck deck = new Deck();
        Language language = new Language();

        Card card1 = new Card();
        language.addCard(card1);

        deck.languages = newArrayList(language);
        List<Error> actualErrors = verifyDeckService.verify(mockDrive, deck, audioDirectoryId);

        assertThat(actualErrors.size(), is(3));
    }

    @Test
    public void testPassingVerifications() {

        when(mockVerifyCardService.verifyRequiredValues(any(Card.class))).thenReturn(new ArrayList<Error>());
        when(mockVerifyCardService.verifyAudioFilename(any(Card.class), any(List.class))).thenReturn(null);
        when(mockVerifyCardService.verifyDuplicateAudioFile(any(Card.class), any(List.class))).thenReturn(null);

        VerifyDeckService verifyDeckService = new VerifyDeckService(mockDriveService, mockVerifyCardService);
        Deck deck = new Deck();
        Language language = new Language();

        Card card1 = new Card();
        language.addCard(card1);

        deck.languages = newArrayList(language);
        List<Error> actualErrors = verifyDeckService.verify(mockDrive, deck, audioDirectoryId);

        assertThat(actualErrors.size(), is(0));
    }

    @Test
    public void testErrorsAreAddedToCards() {
        Card requiredValueErrorCard = new Card();
        Card audioFileErrorCard = new Card();
        Card dupeFileErrorCard = new Card();

        when(mockVerifyCardService.verifyRequiredValues(requiredValueErrorCard)).thenReturn(newArrayList(new Error("requiredValuesError", true)));
        when(mockVerifyCardService.verifyAudioFilename(eq(audioFileErrorCard), any(List.class))).thenReturn(new Error("audioFilenameError", true));
        when(mockVerifyCardService.verifyDuplicateAudioFile(eq(dupeFileErrorCard), any(List.class))).thenReturn(new Error("duplicateFilenameError", true));

        VerifyDeckService verifyDeckService = new VerifyDeckService(mockDriveService, mockVerifyCardService);

        Language language = new Language();
        language.addCard(requiredValueErrorCard);
        language.addCard(audioFileErrorCard);
        language.addCard(dupeFileErrorCard);

        Deck deck = new Deck();
        deck.languages = newArrayList(language);

        verifyDeckService.verify(mockDrive, deck, audioDirectoryId);

        assertThat(requiredValueErrorCard.errors.size(), is(1));
        assertThat(requiredValueErrorCard.errors.get(0).message, is("requiredValuesError"));

        assertThat(audioFileErrorCard.errors.size(), is(1));
        assertThat(audioFileErrorCard.errors.get(0).message, is("audioFilenameError"));

        assertThat(dupeFileErrorCard.errors.size(), is(1));
        assertThat(dupeFileErrorCard.errors.get(0).message, is("duplicateFilenameError"));
    }
}