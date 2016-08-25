package org.mercycorps.translationcards.txcmaker.service;


import org.mercycorps.translationcards.txcmaker.response.ImportDeckResponse;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Constraint;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.ImportDeckForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImportDeckFormService {

    private TxcMakerParser txcMakerParser;

    @Autowired
    public ImportDeckFormService(TxcMakerParser txcMakerParser) {
        this.txcMakerParser = txcMakerParser;
    }

    public void preProcessForm(ImportDeckForm importDeckForm) {
        String documentId = txcMakerParser.parseDocId(importDeckForm.getDocId());
        importDeckForm.setDocId(documentId);
        String audioDirectoryId = txcMakerParser.parseAudioDirId(importDeckForm.getAudioDirId());
        importDeckForm.setAudioDirId(audioDirectoryId);
    }

    public void verifyFormData(ImportDeckResponse importDeckResponse, List<Constraint> constraints) {
        for(Constraint constraint : constraints) {
            importDeckResponse.addErrors(constraint.verify());
        }
    }


}
