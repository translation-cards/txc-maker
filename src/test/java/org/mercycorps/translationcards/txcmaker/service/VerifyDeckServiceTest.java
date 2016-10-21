package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.Drive;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mercycorps.translationcards.txcmaker.model.Translation;
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

        Card card = new Card(null, null, null, null);
        Translation translation = new Translation(newArrayList(card));
        Deck deck = new Deck(null, null, null, 0L, false, null, null, null, new ArrayList<Error>(), newArrayList(translation), null);

        VerifyDeckService verifyDeckService = new VerifyDeckService(mockDriveService, mockVerifyCardService);

        List<Error> actualErrors = verifyDeckService.verify(mockDrive, deck, audioDirectoryId);

        assertThat(actualErrors.size(), is(0));
    }

    @Test
    public void testRequiredValuesErrorsAddedToCard() {
        Card requiredValueErrorCard = new Card(null, null, null, null);
        Translation translation = new Translation(newArrayList(requiredValueErrorCard));
        Deck deck = new Deck(null, null, null, 0L, false, null, null, null, new ArrayList<Error>(), newArrayList(translation), null);

        when(mockVerifyCardService.verifyRequiredValues(requiredValueErrorCard)).thenReturn(newArrayList(new Error("requiredValuesError", true)));

        VerifyDeckService verifyDeckService = new VerifyDeckService(mockDriveService, mockVerifyCardService);
        List<Error> actualErrors = verifyDeckService.verify(mockDrive, deck, audioDirectoryId);

        assertThat(actualErrors.size(), is(1));
        assertThat(requiredValueErrorCard.getErrors().size(), is(1));
        assertThat(requiredValueErrorCard.getErrors().get(0).message, is("requiredValuesError"));
    }

    @Test
    public void testMissingAudioFileErrorAddedToCard() {
        Card audioFileErrorCard = new Card(null, null, null, null);
        when(mockVerifyCardService.verifyAudioFilename(eq(audioFileErrorCard), any(List.class))).thenReturn(new Error("audioFilenameError", true));

        Translation translation = new Translation(newArrayList(audioFileErrorCard));
        Deck deck = new Deck(null, null, null, 0L, false, null, null, null, new ArrayList<Error>(), newArrayList(translation), null);

        VerifyDeckService verifyDeckService = new VerifyDeckService(mockDriveService, mockVerifyCardService);
        List<Error> actualErrors = verifyDeckService.verify(mockDrive, deck, audioDirectoryId);

        assertThat(actualErrors.size(), is(1));

        assertThat(audioFileErrorCard.getErrors().size(), is(1));
        assertThat(audioFileErrorCard.getErrors().get(0).message, is("audioFilenameError"));
    }

    @Test
    public void testDuplicateFileErrorsAreAddedToCards() {
        String sameFilename = "sameFilename.mp3";
        Card duplicateCard1 = new Card(null, sameFilename, null, null);
        Card duplicateCard2 = new Card(null, sameFilename, null, null);
        Translation translation = new Translation(newArrayList(duplicateCard1, duplicateCard2));
        Deck deck = new Deck(null, null, null, 0L, false, null, null, null, new ArrayList<Error>(), newArrayList(translation), null);

        VerifyDeckService verifyDeckService = new VerifyDeckService(mockDriveService, mockVerifyCardService);

        List<Error> actualErrors = verifyDeckService.verify(mockDrive, deck, audioDirectoryId);

        assertThat(actualErrors.size(), is(2));

        assertThat(duplicateCard1.getErrors().size(), is(1));
        assertThat(duplicateCard1.getErrors().get(0).message, is(String.format(VerifyDeckService.DUPLICATE_FILE_ERROR_FORMAT, sameFilename)));

        assertThat(duplicateCard2.getErrors().size(), is(1));
        assertThat(duplicateCard2.getErrors().get(0).message, is(String.format(VerifyDeckService.DUPLICATE_FILE_ERROR_FORMAT, sameFilename)));
    }
}
