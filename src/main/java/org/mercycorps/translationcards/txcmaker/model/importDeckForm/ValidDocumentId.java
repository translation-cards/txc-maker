package org.mercycorps.translationcards.txcmaker.model.importDeckForm;


import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.deck.RequiredString;

import java.io.IOException;
import java.util.List;

public class ValidDocumentId implements Constraint {
    public static final String CSV_EXPORT_TYPE = "text/csv";
    private static final String INVALID_DOCUMENT_ID_MESSAGE = "Invalid Document ID";
    private static final String FATAL_DRIVE_ERROR_MESSAGE = "There was an error accessing the provided document. Please try again.";
    static final Error NO_DOC_ID = new Error("Document ID is a required field", true);
    static final Error INVALID_DOCUMENT_ID = new Error(INVALID_DOCUMENT_ID_MESSAGE, true);
    static final Error FATAL_DRIVE_ERROR = new Error(FATAL_DRIVE_ERROR_MESSAGE, true);

    private Drive drive;
    private String documentId;
    private RequiredString emptyIDCheck;

    public ValidDocumentId(Drive drive, String documentId) {
        this.drive = drive;
        this.documentId = documentId;
        this.emptyIDCheck = new RequiredString(documentId, NO_DOC_ID);
    }

    @Override
    public List<Error> verify() {
        List<Error> errors = emptyIDCheck.verify();

        if(errors.isEmpty()) {
            try {
                drive.files().export(documentId, CSV_EXPORT_TYPE).executeMediaAsInputStream().close();
            } catch (GoogleJsonResponseException e) {
                errors.add(INVALID_DOCUMENT_ID);
            } catch (IOException e) {
                errors.add(FATAL_DRIVE_ERROR);
            }
        }

        return errors;
    }
}
