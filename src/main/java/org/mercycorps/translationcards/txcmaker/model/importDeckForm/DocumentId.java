package org.mercycorps.translationcards.txcmaker.model.importDeckForm;


import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.model.Error;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DocumentId implements Field {
    public static final String CSV_EXPORT_TYPE = "text/csv";
    public static final String FIELD_NAME = "docId";
    public static final String INVALID_DOCUMENT_ID_MESSAGE = "Invalid Document ID";
    public static final String FATAL_DRIVE_ERROR_MESSAGE = "There was an error accessing the provided document. Please try again.";
    public static final Error INVALID_DOCUMENT_ID = new Error(FIELD_NAME, INVALID_DOCUMENT_ID_MESSAGE);
    public static final Error FATAL_DRIVE_ERROR = new Error("", FATAL_DRIVE_ERROR_MESSAGE);

    private Drive drive;
    private String documentIdString;

    public DocumentId(Drive drive, String documentIdString) {
        this.drive = drive;
        this.documentIdString = documentIdString;
    }

    @Override
    public List<Error> verify() {
        List<Error> errors = new ArrayList<>();
        try {
            InputStream inputStream = drive.files().export(documentIdString, CSV_EXPORT_TYPE).executeMediaAsInputStream();
            inputStream.close();
        } catch(GoogleJsonResponseException e) {
            errors.add(INVALID_DOCUMENT_ID);
        } catch(IOException e) {
            errors.add(FATAL_DRIVE_ERROR);
        }
        return errors;
    }
}
