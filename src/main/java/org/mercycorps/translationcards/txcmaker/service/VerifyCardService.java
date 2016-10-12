package org.mercycorps.translationcards.txcmaker.service;

import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.NewCard;
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


    public List<Error> verifyRequiredValues(NewCard card) {
        List<Constraint> constraints = Arrays.asList((Constraint)
                new RequiredString(card.getSourcePhrase(), NO_LABEL),
                new RequiredString(card.getAudio(), NO_AUDIO),
                new RequiredString(card.getDestinationPhrase(), NO_TEXT)
        );

        List<Error> errors = newArrayList();

        for(Constraint constraint : constraints) {
            errors.addAll(constraint.verify());
        }

        return errors;
    }

    public Error verifyAudioFilename(NewCard card, List<String> audioFilenames) {
        String cardAudioFilename = card.getAudio();
        if (audioFilenames == null || !isEmpty(cardAudioFilename) && !audioFilenames.contains(cardAudioFilename)) {
            return new Error(String.format(FILE_NOT_FOUND_ERROR_FORMAT, cardAudioFilename), true);
        }
        return null;
    }
}
