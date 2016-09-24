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
    public void testPassingVerifications() {

        when(mockVerifyCardService.verifyRequiredValues(any(Card.class))).thenReturn(new ArrayList<Error>());
        when(mockVerifyCardService.verifyAudioFilename(any(Card.class), any(List.class))).thenReturn(null);

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
    public void testRequiredValuesErrorsAddedToCard() {
        Card requiredValueErrorCard = new Card();
        Language language = new Language();
        language.addCard(requiredValueErrorCard);
        Deck deck = new Deck();
        deck.languages = newArrayList(language);

        when(mockVerifyCardService.verifyRequiredValues(requiredValueErrorCard)).thenReturn(newArrayList(new Error("requiredValuesError", true)));

        VerifyDeckService verifyDeckService = new VerifyDeckService(mockDriveService, mockVerifyCardService);
        List<Error> actualErrors = verifyDeckService.verify(mockDrive, deck, audioDirectoryId);

        assertThat(actualErrors.size(), is(1));
        assertThat(requiredValueErrorCard.errors.size(), is(1));
        assertThat(requiredValueErrorCard.errors.get(0).message, is("requiredValuesError"));
    }

    @Test
    public void testMissingAudioFileErrorAddedToCard() {
        Card audioFileErrorCard = new Card();
        when(mockVerifyCardService.verifyAudioFilename(eq(audioFileErrorCard), any(List.class))).thenReturn(new Error("audioFilenameError", true));

        Language language = new Language();
        language.addCard(audioFileErrorCard);

        Deck deck = new Deck();
        deck.languages = newArrayList(language);

        VerifyDeckService verifyDeckService = new VerifyDeckService(mockDriveService, mockVerifyCardService);
        List<Error> actualErrors = verifyDeckService.verify(mockDrive, deck, audioDirectoryId);

        assertThat(actualErrors.size(), is(1));

        assertThat(audioFileErrorCard.errors.size(), is(1));
        assertThat(audioFileErrorCard.errors.get(0).message, is("audioFilenameError"));
    }

    @Test
    public void testDuplicateFileErrorsAreAddedToCards() {
        String sameFilename = "sameFilename.mp3";
        Card duplicateCard1 = new Card();
        duplicateCard1.dest_audio = sameFilename;
        Card duplicateCard2 = new Card();
        duplicateCard2.dest_audio = sameFilename;

        VerifyDeckService verifyDeckService = new VerifyDeckService(mockDriveService, mockVerifyCardService);

        Language language = new Language();
        language.addCard(duplicateCard1);
        language.addCard(duplicateCard2);

        Deck deck = new Deck();
        deck.languages = newArrayList(language);

        List<Error> actualErrors = verifyDeckService.verify(mockDrive, deck, audioDirectoryId);

        assertThat(actualErrors.size(), is(2));

        assertThat(duplicateCard1.errors.size(), is(1));
        assertThat(duplicateCard1.errors.get(0).message, is(String.format(VerifyDeckService.DUPLICATE_FILE_ERROR_FORMAT, sameFilename)));

        assertThat(duplicateCard2.errors.size(), is(1));
        assertThat(duplicateCard2.errors.get(0).message, is(String.format(VerifyDeckService.DUPLICATE_FILE_ERROR_FORMAT, sameFilename)));
    }
}