package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.Language;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

import static com.google.common.collect.Lists.newArrayList;


@Service
public class VerifyDeckService {

    @Autowired
    private DriveService driveService;

    @Autowired
    private VerifyCardService verifyCardService;

    public VerifyDeckService(DriveService driveService, VerifyCardService verifyCardService) {
        this.driveService = driveService;
        this.verifyCardService = verifyCardService;
    }

    public List<Error> verify(Drive drive, Deck deck, String audioDirectoryId) {
        List<Error> errors = newArrayList();
        List<String> filenamesInAudioDirectory = driveService.getFilenamesInAudioDirectory(drive, audioDirectoryId);
        List<String> filesFromCards = newArrayList();

        for (Language language: deck.languages) {
            for (Card card : language.cards) {
                List<Error> cardErrors = newArrayList();
                cardErrors.addAll(verifyCardService.verifyRequiredValues(card));

                Error verifyAudioError = verifyCardService.verifyAudioFilename(card, filenamesInAudioDirectory);
                if (verifyAudioError != null) {
                    cardErrors.add(verifyAudioError);
                }

                Error verifyDupeError = verifyCardService.verifyDuplicateAudioFile(card, filesFromCards);
                if (verifyDupeError != null) {
                    cardErrors.add(verifyDupeError);
                }
                filesFromCards.add(card.dest_audio);

                card.errors.addAll(cardErrors);
                errors.addAll(cardErrors);
            }
        }

        return errors;
    }
}
