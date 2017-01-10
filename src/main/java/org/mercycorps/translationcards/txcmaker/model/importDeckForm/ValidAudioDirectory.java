package org.mercycorps.translationcards.txcmaker.model.importDeckForm;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.deck.RequiredString;

import java.io.IOException;
import java.util.List;

public class ValidAudioDirectory implements Constraint {
    static final Error INVALID_AUDIO_DIRECTORY_ID = new Error("Invalid Audio Directory ID", true);
    static final Error NO_AUDIO_DIR_ID = new Error("Audio Directory ID is a required field", true);
    private final RequiredString emptyDirectoryID;

    private Drive drive;
    private String audioDirectoryId;

    public ValidAudioDirectory(Drive drive, String audioDirectoryId) {
        this.drive = drive;
        this.audioDirectoryId = audioDirectoryId;
        this.emptyDirectoryID = new RequiredString(audioDirectoryId, NO_AUDIO_DIR_ID);
    }

    @Override
    public List<Error> verify() {
        List<Error> errors = emptyDirectoryID.verify();

        if (errors.isEmpty()) {
            try {
                drive.children().list(audioDirectoryId).execute();
            } catch (GoogleJsonResponseException e) {
                errors.add(INVALID_AUDIO_DIRECTORY_ID);
            } catch (IOException e) {
                errors.add(ValidDocumentId.FATAL_DRIVE_ERROR);
            }
        }

        return errors;
    }
}
