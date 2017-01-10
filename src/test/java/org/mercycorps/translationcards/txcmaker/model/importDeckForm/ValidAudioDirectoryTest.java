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
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidAudioDirectoryTest {

    private ValidAudioDirectory validAudioDirectory;
    private Drive drive;
    private String audioDirectoryIDString;
    private List<Error> errors;

    @Before
    public void setup() {
        errors = new ArrayList<>();
        drive = mock(Drive.class, RETURNS_DEEP_STUBS);
        audioDirectoryIDString = "directory ID String";
        validAudioDirectory = new ValidAudioDirectory(drive, audioDirectoryIDString);
    }

    @Test
    public void verifyFormData_shouldNotRespondWithErrorsWhenTheDirectoryIdIsValid() throws Exception {
        errors.addAll(validAudioDirectory.verify());

        assertThat(errors.isEmpty(), is(true));
    }

    @Test
    public void verifyFormData_shouldAddAnErrorWhenDirectoryIDIsInvalid() throws Exception {
        when(drive.children().list(audioDirectoryIDString).execute())
                .thenThrow(mock(GoogleJsonResponseException.class));

        errors.addAll(validAudioDirectory.verify());

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0), is(ValidAudioDirectory.INVALID_AUDIO_DIRECTORY_ID));
    }

    @Test
    public void verifyFormData_shouldAddAnErrorWhenThereIsADriveError() throws Exception {
        when(drive.children().list(audioDirectoryIDString).execute())
                .thenThrow(new IOException());

        errors.addAll(validAudioDirectory.verify());

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0), is(ValidDocumentId.FATAL_DRIVE_ERROR));
    }

    @Test
    public void verifyFormData_shouldAddAnErrorWhenThereIsAnEmptyID() throws Exception {
        String emptyDirectoryID = "";
        when(drive.children().list(emptyDirectoryID).execute())
                .thenThrow(mock(GoogleJsonResponseException.class));
        validAudioDirectory = new ValidAudioDirectory(drive, emptyDirectoryID);
        errors.addAll(validAudioDirectory.verify());

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0), is(ValidAudioDirectory.NO_AUDIO_DIR_ID));
    }

    @Test
    public void verifyFormData_shouldAddAnErrorWhenThereIsANullID() throws Exception {
        String nullDirectoryID = null;
        when(drive.children().list(nullDirectoryID).execute())
                .thenThrow(mock(GoogleJsonResponseException.class));
        validAudioDirectory = new ValidAudioDirectory(drive, nullDirectoryID);
        errors.addAll(validAudioDirectory.verify());

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0), is(ValidAudioDirectory.NO_AUDIO_DIR_ID));
    }

}