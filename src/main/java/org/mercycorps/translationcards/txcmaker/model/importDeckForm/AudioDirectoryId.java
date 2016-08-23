package org.mercycorps.translationcards.txcmaker.model.importDeckForm;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.model.Error;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioDirectoryId implements Field {
    public static final String FIELD_NAME = "audioDirId";
    public static final String INVALID_AUDIO_DIRECTORY_ID_MESSAGE = "Invalid Audio Directory ID";
    public static final Error INVALID_AUDIO_DIRECTORY_ID = new Error(FIELD_NAME, INVALID_AUDIO_DIRECTORY_ID_MESSAGE);
    public static final String REQUIRED_FIELD_MESSAGE = "Audio Directory ID is a required field.";
    public static final Error REQUIRED_FIELD = new Error(FIELD_NAME, REQUIRED_FIELD_MESSAGE);

    private Drive drive;
    private String audioDirectoryId;

    public AudioDirectoryId(Drive drive, String audioDirectoryId) {
        this.drive = drive;
        this.audioDirectoryId = audioDirectoryId;
    }

    @Override
    public List<Error> verify() {
        List<Error> errors = new ArrayList<>();

        if(audioDirectoryId == null) {
            errors.add(REQUIRED_FIELD);
            return errors;
        }

        try {
            drive.children().list(audioDirectoryId).execute();
        } catch(GoogleJsonResponseException e) {
            errors.add(INVALID_AUDIO_DIRECTORY_ID);
        } catch(IOException e) {
            errors.add(DocumentId.FATAL_DRIVE_ERROR);
        }
        return errors;
    }
}
