package org.mercycorps.translationcards.txcmaker.service;


import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Constraint;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.ImportDeckForm;
import org.mercycorps.translationcards.txcmaker.response.ImportDeckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class ImportDeckService {

    private TxcMakerParser txcMakerParser;
    private DriveService driveService;

    @Autowired
    public ImportDeckService(TxcMakerParser txcMakerParser, DriveService driveService) {
        this.txcMakerParser = txcMakerParser;
        this.driveService = driveService;
    }

    public void preProcessForm(ImportDeckForm importDeckForm) {
        String documentId = txcMakerParser.parseDocId(importDeckForm.getDocId());
        importDeckForm.setDocId(documentId);
        String audioDirectoryId = txcMakerParser.parseAudioDirId(importDeckForm.getAudioDirId());
        importDeckForm.setAudioDirId(audioDirectoryId);
    }

    private void verifyFormData(ImportDeckResponse importDeckResponse, List<Constraint> constraints) {
        for(Constraint constraint : constraints) {
            importDeckResponse.addErrors(constraint.verify());
        }
    }

    private void verifyDeck(Deck deck, ImportDeckResponse importDeckResponse) {
        if(!deck.isValid()) {
            String errorMessage = "The ISO Code on rows ";

            for (Error error : deck.getParsingErrors()) {
                errorMessage += error.message + ", ";
            }
            errorMessage = errorMessage.substring(0, errorMessage.length() - 2);

            errorMessage += " are invalid. See www.translation-cards.com/iso-codes for a list of supported codes";
            importDeckResponse.addError(new Error(errorMessage, true));
        }
    }

    public void processForm(ImportDeckForm importDeckForm,
                            HttpServletRequest request,
                            ImportDeckResponse importDeckResponse,
                            Drive drive,
                            String sessionId,
                            List<Constraint> fieldsToVerify) {
        verifyFormData(importDeckResponse, fieldsToVerify);
        if(!importDeckResponse.hasErrors()) {
            try {
                final Deck deck = driveService.assembleDeck(request, importDeckForm.getDocId(), sessionId, drive);
                verifyDeck(deck, importDeckResponse);
            } catch(IllegalArgumentException exception) {
                importDeckResponse.addError(new Error(exception.getMessage(), true));
            }
        }
    }
}
