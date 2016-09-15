package org.mercycorps.translationcards.txcmaker.service;

import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.deck.RequiredString;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Constraint;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.util.StringUtils.isEmpty;

@Service
public class VerifyCardService {
    public final static Error NO_LABEL = new Error("This card has no label", true);
    public final static Error NO_AUDIO = new Error("This card has no audio recording", true);
    public final static Error NO_TEXT = new Error("This card has no text translation", false);
    public final static String FILE_NOT_FOUND_ERROR_FORMAT = "There is no file named '%s' in the Google Drive folder.";
    public final static String DUPLICATE_FILE_ERROR_FORMAT = "Audio file '%s' is associated with more than one card.";


    public List<Error> verifyRequiredValues(Card card) {
        List<Constraint> constraints = Arrays.asList((Constraint)
                new RequiredString(card.card_label, NO_LABEL),
                new RequiredString(card.dest_audio, NO_AUDIO),
                new RequiredString(card.dest_txt, NO_TEXT)
        );

        List<Error> errors = newArrayList();

        for(Constraint constraint : constraints) {
            errors.addAll(constraint.verify());
        }

        return errors;
    }

    public Error verifyAudioFilename(Card card, List<String> audioFilenames) {
        String cardAudioFilename = card.dest_audio;
        if (audioFilenames == null || !isEmpty(cardAudioFilename) && !audioFilenames.contains(cardAudioFilename)) {
            return new Error(String.format(FILE_NOT_FOUND_ERROR_FORMAT, cardAudioFilename), true);
        }
        return null;
    }

    public Error verifyDuplicateAudioFile(Card card, List<String> filesFromCards) {
        if (filesFromCards.contains(card.dest_audio)) {
            Error cardError = new Error(String.format(DUPLICATE_FILE_ERROR_FORMAT, card.dest_audio), false);
            return cardError;
        }
        return null;
    }
}
