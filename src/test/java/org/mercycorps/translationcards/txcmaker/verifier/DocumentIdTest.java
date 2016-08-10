package org.mercycorps.translationcards.txcmaker.verifier;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.DocumentId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DocumentIdTest {


    DocumentId documentId;
    private Drive drive;
    private String documentIdString;
    private List<Error> errors;

    @Before
    public void setup() {
        errors = new ArrayList<>();
        drive = mock(Drive.class, RETURNS_DEEP_STUBS);
        documentIdString = "document ID String";
        documentId = new DocumentId(drive, documentIdString);
    }

    @Test
    public void verifyFormData_shouldNotRespondWithErrorsWhenTheDocumentIdIsValid() throws Exception {
        errors.addAll(documentId.verify());

        assertThat(errors.isEmpty(), is(true));
    }

    @Test
    public void verifyFormData_shouldAddAnErrorWhenDocumentIDIsInvalid() throws Exception {
        when(drive.files().export(documentIdString, DocumentId.CSV_EXPORT_TYPE).executeMediaAsInputStream())
                .thenThrow(mock(GoogleJsonResponseException.class));

        errors.addAll(documentId.verify());

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0), is(DocumentId.INVALID_DOCUMENT_ID));
    }

    @Test
    public void verifyFormData_shouldAddAnErrorWhenThereIsADriveError() throws Exception {
        when(drive.files().export(documentIdString, DocumentId.CSV_EXPORT_TYPE).executeMediaAsInputStream())
                .thenThrow(new IOException());

        errors.addAll(documentId.verify());

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0), is(DocumentId.FATAL_DRIVE_ERROR));
    }
}