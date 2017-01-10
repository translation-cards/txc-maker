package org.mercycorps.translationcards.txcmaker.model.importDeckForm;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Error;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ValidDocumentIdTest {


    private ValidDocumentId validDocumentId;
    private Drive drive;
    private String documentIdString;
    private List<Error> errors;

    @Before
    public void setup() {
        errors = new ArrayList<>();
        drive = mock(Drive.class, RETURNS_DEEP_STUBS);
        documentIdString = "document ID String";
        validDocumentId = new ValidDocumentId(drive, documentIdString);
    }

    @Test
    public void verifyFormData_shouldNotRespondWithErrorsWhenTheDocumentIdIsValid() throws Exception {
        errors.addAll(validDocumentId.verify());

        assertThat(errors.isEmpty(), is(true));
    }

    @Test
    public void verifyFormData_shouldAddAnErrorWhenDocumentIDIsInvalid() throws Exception {
        when(drive.files().export(documentIdString, ValidDocumentId.CSV_EXPORT_TYPE).executeMediaAsInputStream())
                .thenThrow(mock(GoogleJsonResponseException.class));

        errors.addAll(validDocumentId.verify());

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0), is(ValidDocumentId.INVALID_DOCUMENT_ID));
    }

    @Test
    public void verifyFormData_shouldAddAnErrorWhenThereIsADriveError() throws Exception {
        when(drive.files().export(documentIdString, ValidDocumentId.CSV_EXPORT_TYPE).executeMediaAsInputStream())
                .thenThrow(new IOException());

        errors.addAll(validDocumentId.verify());

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0), is(ValidDocumentId.FATAL_DRIVE_ERROR));
    }

    @Test
    public void verifyFormData_shouldAddAnErrorWhenThereIsAnEmptyID() throws Exception {
        String emptyDocID = "";
        when(drive.files().export(emptyDocID, ValidDocumentId.CSV_EXPORT_TYPE).executeMediaAsInputStream())
                .thenThrow(mock(GoogleJsonResponseException.class));
        validDocumentId = new ValidDocumentId(drive, emptyDocID);
        errors.addAll(validDocumentId.verify());

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0), is(ValidDocumentId.NO_DOC_ID));
    }

    @Test
    public void verifyFormData_shouldAddAnErrorWhenThereIsANullID() throws Exception {
        String nullDocID = null;
        when(drive.files().export(nullDocID, ValidDocumentId.CSV_EXPORT_TYPE).executeMediaAsInputStream())
                .thenThrow(mock(GoogleJsonResponseException.class));
        validDocumentId = new ValidDocumentId(drive, nullDocID);
        errors.addAll(validDocumentId.verify());

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0), is(ValidDocumentId.NO_DOC_ID));
    }
}