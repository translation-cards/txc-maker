package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.model.File;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.Card;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mercycorps.translationcards.txcmaker.service.VerifyCardService.*;

public class VerifyCardServiceTest {

    private final String ARABIC_CHARACTERS = "ñéحيبε好";

    private VerifyCardService vcs = new VerifyCardService();
    private Card emptyCard = new Card(null, null, null, null);
    private Card cardWithMatchingAudioFile = new Card(null, "matchingFilename.mp3", null, null);
    private Card cardWithoutMatchingFileName = new Card(null, "fileNotInDirectory.mp3", null, null);
    private Card cardWithArabicInAudioFile = new Card(null, ARABIC_CHARACTERS, null, null);

    @Test
    public void testWhenNoRequiredValuesPopulated() {
        List<Error> actualErrors = vcs.verifyRequiredValues(emptyCard);

        assertThat(actualErrors.size(), is(3));
        assertThat(actualErrors.get(0), is(NO_LABEL));
        assertThat(actualErrors.get(1), is(NO_AUDIO));
        assertThat(actualErrors.get(2), is(NO_TEXT));
    }

    @Test
    public void testMatchingAudioFile() throws Exception {
        File matchingFile = new File();
        matchingFile.setTitle("matchingFilename.mp3");
        File notMatchingFile = new File();
        notMatchingFile.setTitle("nonMatching.mp3");

        List<File> audioFiles = newArrayList(matchingFile, notMatchingFile);
        Error error = vcs.verifyAudioFilename(cardWithMatchingAudioFile, audioFiles);
        assertThat(error, nullValue());
    }

    @Test
    public void testNoMatchingFile() throws Exception {
        File audioFile1 = new File();
        audioFile1.setTitle("audioFile1.mp3");
        File audioFile2 = new File();
        audioFile2.setTitle("audioFile2.mp3");
        List<File> audioFiles = newArrayList(audioFile1, audioFile2);

        Error error = vcs.verifyAudioFilename(cardWithoutMatchingFileName, audioFiles);
        assertThat(error.message, is(String.format(FILE_NOT_FOUND_ERROR_FORMAT, cardWithoutMatchingFileName.getAudio())));
    }

    @Test
    public void testNoFilesInDir() throws Exception {
        List<File> audioFiles = newArrayList();
        Error error = vcs.verifyAudioFilename(cardWithoutMatchingFileName, audioFiles);
        assertThat(error.message, is(String.format(FILE_NOT_FOUND_ERROR_FORMAT, cardWithoutMatchingFileName.getAudio())));
    }

    @Test
    public void testNullDirFiles() throws Exception {
        List<File> audioFiles = null;
        Error error = vcs.verifyAudioFilename(cardWithoutMatchingFileName, audioFiles);
        assertThat(error.message, is(String.format(FILE_NOT_FOUND_ERROR_FORMAT, cardWithoutMatchingFileName.getAudio())));
    }

    @Test
    public void testNonEnglishChars() throws Exception {
        String funChars = "ñéحيبε好";
        File funkyAudioFile = new File();
        funkyAudioFile.setTitle(funChars);
        List<File> audioFilenames = newArrayList(funkyAudioFile);
        Error error = vcs.verifyAudioFilename(cardWithArabicInAudioFile, audioFilenames);
        assertThat(error, nullValue());
    }

    @Test
    public void testAudioFileIdGetsSet() throws Exception {
        String fileId = "matchingFileId";

        File matchingFile = new File();
        matchingFile.setTitle("matchingFilename.mp3");
        matchingFile.setId(fileId);
        List<File> audioFiles = newArrayList(matchingFile);

        assertThat(cardWithMatchingAudioFile.audioId, nullValue());

        vcs.verifyAudioFilename(cardWithMatchingAudioFile, audioFiles);

        assertThat(cardWithMatchingAudioFile.audioId, is(fileId));
    }
}
