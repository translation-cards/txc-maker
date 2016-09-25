package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.model.File;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.Error;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mercycorps.translationcards.txcmaker.service.VerifyCardService.*;

public class VerifyCardServiceTest {

    private VerifyCardService vcs = new VerifyCardService();

    @Test
    public void testWhenNoRequiredValuesPopulated() {

        Card card = new Card();

        List<Error> actualErrors = vcs.verifyRequiredValues(card);

        assertThat(actualErrors.size(), is(3));
        assertThat(actualErrors.get(0), is(NO_LABEL));
        assertThat(actualErrors.get(1), is(NO_AUDIO));
        assertThat(actualErrors.get(2), is(NO_TEXT));
    }

    @Test
    public void testMatchingAudioFile() throws Exception {
        Card card = new Card();
        card.dest_audio = "matchingFilename.mp3";

        File matchingFile = new File();
        matchingFile.setTitle("matchingFilename.mp3");
        File notMatchingFile = new File();
        notMatchingFile.setTitle("nonMatching.mp3");

        List<File> audioFiles = newArrayList(matchingFile, notMatchingFile);
        Error error = vcs.verifyAudioFilename(card, audioFiles);
        assertThat(error, nullValue());
    }

    @Test
    public void testNoMatchingFile() throws Exception {
        File audioFile1 = new File();
        audioFile1.setTitle("audioFile1.mp3");
        File audioFile2 = new File();
        audioFile2.setTitle("audioFile2.mp3");
        List<File> audioFiles = newArrayList(audioFile1, audioFile2);

        Card card = new Card();
        card.dest_audio = "fileNotInDirectory.mp3";
        Error error = vcs.verifyAudioFilename(card, audioFiles);
        assertThat(error.message, is(String.format(FILE_NOT_FOUND_ERROR_FORMAT, card.dest_audio)));
    }

    @Test
    public void testNoFilesInDir() throws Exception {
        List<File> audioFiles = newArrayList();
        Card card = new Card();
        card.dest_audio  = "fileNotInDirectory.mp3";
        Error error = vcs.verifyAudioFilename(card, audioFiles);
        assertThat(error.message, is(String.format(FILE_NOT_FOUND_ERROR_FORMAT, card.dest_audio)));
    }

    @Test
    public void testNullDirFiles() throws Exception {
        List<File> audioFiles = null;
        Card card = new Card();
        card.dest_audio  = "fileNotInDirectory.mp3";
        Error error = vcs.verifyAudioFilename(card, audioFiles);
        assertThat(error.message, is(String.format(FILE_NOT_FOUND_ERROR_FORMAT, card.dest_audio)));
    }

    @Test
    public void testNonEnglishChars() throws Exception {
        String funChars = "ñéحيبε好";
        File funkyAudioFile = new File();
        funkyAudioFile.setTitle(funChars);
        List<File> audioFilenames = newArrayList(funkyAudioFile);
        Card card = new Card();
        card.dest_audio  = funChars;
        Error error = vcs.verifyAudioFilename(card, audioFilenames);
        assertThat(error, nullValue());
    }

    @Test
    public void testAudioFileIdGetsSet() throws Exception {
        String fileId = "matchingFileId";
        Card card = new Card();
        card.dest_audio = "matchingFilename.mp3";

        File matchingFile = new File();
        matchingFile.setTitle("matchingFilename.mp3");
        matchingFile.setId(fileId);
        List<File> audioFiles = newArrayList(matchingFile);

        assertThat(card.audio_id, nullValue());

        vcs.verifyAudioFilename(card, audioFiles);

        assertThat(card.audio_id, is(fileId));
    }
}