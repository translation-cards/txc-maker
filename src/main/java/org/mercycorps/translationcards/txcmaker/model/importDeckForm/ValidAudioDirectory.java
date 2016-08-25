package org.mercycorps.translationcards.txcmaker.model.importDeckForm;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.model.Error;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ValidAudioDirectory implements Constraint {
    public static final Error INVALID_AUDIO_DIRECTORY_ID = new Error("Invalid Audio Directory ID", true);

    private Drive drive;
    private String audioDirectoryId;

    public ValidAudioDirectory(Drive drive, String audioDirectoryId) {
        this.drive = drive;
        this.audioDirectoryId = audioDirectoryId;
    }

    @Override
    public List<Error> verify() {
        List<Error> errors = new ArrayList<>();

        try {
            drive.children().list(audioDirectoryId).execute();
        } catch(GoogleJsonResponseException e) {
            errors.add(INVALID_AUDIO_DIRECTORY_ID);
        } catch(IOException e) {
            errors.add(ValidDocumentId.FATAL_DRIVE_ERROR);
        }
        return errors;
    }
}
