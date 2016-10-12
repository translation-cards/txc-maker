package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.model.*;
import org.mercycorps.translationcards.txcmaker.model.Error;
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

    public List<Error> verify(Drive drive, NewDeck deck, String audioDirectoryId) {
        List<Error> errors = newArrayList();
        List<String> filenamesInAudioDirectory = driveService.getFilenamesInAudioDirectory(drive, audioDirectoryId);
        Map<String, List<NewCard>> audioFileToCards = newHashMap();

        for (Translation language: deck.getTranslations()) {
            for (NewCard card : language.getCards()) {
                List<Error> cardErrors = newArrayList();
                cardErrors.addAll(verifyCardService.verifyRequiredValues(card));

                Error verifyAudioError = verifyCardService.verifyAudioFilename(card, filenamesInAudioDirectory);
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

    private List<Error> verifyDuplicateAudioFile(Map<String, List<NewCard>> audioFileMap) {
        Error error;
        List<Error> duplicateAudioErrors = newArrayList();

        for (List<NewCard> cardsForFile : audioFileMap.values()) {
            if (cardsForFile.size() > 1) {
                for(NewCard card : cardsForFile) {
                    error = new Error(String.format(DUPLICATE_FILE_ERROR_FORMAT, card.getAudio()), true);
                    card.getErrors().add(error);
                    duplicateAudioErrors.add(error);
                }
            }
        }
        return duplicateAudioErrors;
    }

    private void addToAudioFileMap(NewCard card, Map<String, List<NewCard>> audioFileMap) {
        List<NewCard> cardList = audioFileMap.get(card.getAudio());
        if (cardList == null) {
            cardList = newArrayList();
            audioFileMap.put(card.getAudio(), cardList);
        }
        cardList.add(card);
    }
}
