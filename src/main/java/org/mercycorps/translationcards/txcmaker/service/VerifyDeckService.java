package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.*;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;


@Service
public class VerifyDeckService {
    public final static String DUPLICATE_FILE_ERROR_FORMAT = "Audio file '%s' is associated with more than one card.";

    @Autowired
    private DriveService driveService;

    @Autowired
    private VerifyCardService verifyCardService;

    public VerifyDeckService(DriveService driveService, VerifyCardService verifyCardService) {
        this.driveService = driveService;
        this.verifyCardService = verifyCardService;
    }

    // TODO: two separate patterns for adding errors to decks and cards
    public List<Error> verify(Drive drive, Deck deck, String audioDirectoryId) {
        List<Error> errors = newArrayList();
        List<File> filesInAudioDirectory = driveService.getFilesInAudioDirectory(drive, audioDirectoryId);
        Map<String, List<Card>> audioFileToCards = newHashMap();

        for (Translation language: deck.getTranslations()) {
            for (Card card : language.getCards()) {
                List<Error> cardErrors = newArrayList();
                cardErrors.addAll(verifyCardService.verifyRequiredValues(card));

                Error verifyAudioError = verifyCardService.verifyAudioFilename(card, filesInAudioDirectory);
                if (verifyAudioError != null) {
                    cardErrors.add(verifyAudioError);
                }

                addToAudioFileMap(card, audioFileToCards);

                card.getErrors().addAll(cardErrors);
                errors.addAll(cardErrors);
            }
        }

        errors.addAll(verifyDuplicateAudioFile(audioFileToCards));

        return errors;
    }

    private List<Error> verifyDuplicateAudioFile(Map<String, List<Card>> audioFileMap) {
        List<Error> duplicateAudioErrors = newArrayList();

        for (List<Card> cardsForFile : audioFileMap.values()) {
            if (cardsForFile.size() > 1) {
                for(Card card : cardsForFile) {
                    Error error = new Error(String.format(DUPLICATE_FILE_ERROR_FORMAT, card.getAudio()), true);
                    card.getErrors().add(error);
                    duplicateAudioErrors.add(error);
                }
            }
        }
        return duplicateAudioErrors;
    }

    private void addToAudioFileMap(Card card, Map<String, List<Card>> audioFileMap) {
        List<Card> cardList = audioFileMap.get(card.getAudio());
        if (cardList == null) {
            cardList = newArrayList();
            audioFileMap.put(card.getAudio(), cardList);
        }
        cardList.add(card);
    }
}