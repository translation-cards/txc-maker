package org.mercycorps.translationcards.txcmaker.service;

import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.NewCard;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mercycorps.translationcards.txcmaker.service.VerifyCardService.*;

public class VerifyCardServiceTest {

    private final String ARABIC_CHARACTERS = "ñéحيبε好";

    private VerifyCardService vcs = new VerifyCardService();
    private NewCard emptyCard = new NewCard(null, null, null, null, null);
    private NewCard cardWithMatchingAudioFile = new NewCard(null, "matchingFilename.mp3", null, null, null);
    private NewCard cardWithoutMatchingFileName = new NewCard(null, "fileNotInDirectory.mp3", null, null, null);
    private NewCard cardWithArabicInAudioFile = new NewCard(null, ARABIC_CHARACTERS, null, null, null);

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
        List<String> audioFilenames = newArrayList("matchingFilename.mp3", "nonMatching.mp3");

        Error error = vcs.verifyAudioFilename(cardWithMatchingAudioFile, audioFilenames);

        assertThat(error, nullValue());
    }

    @Test
    public void testNoMatchingFile() throws Exception {
        List<String> audioFilenames = newArrayList("audioFile1.mp3", "audioFile2.mp3");

        Error error = vcs.verifyAudioFilename(cardWithoutMatchingFileName, audioFilenames);

        assertThat(error.message, is(String.format(FILE_NOT_FOUND_ERROR_FORMAT, cardWithoutMatchingFileName.getAudio())));
    }

    @Test
    public void testNoFilesInDir() throws Exception {
        List<String> audioFilenames = newArrayList();

        Error error = vcs.verifyAudioFilename(cardWithoutMatchingFileName, audioFilenames);

        assertThat(error.message, is(String.format(FILE_NOT_FOUND_ERROR_FORMAT, cardWithoutMatchingFileName.getAudio())));
    }

    @Test
    public void testNullDirFiles() throws Exception {
        List<String> audioFilenames = null;

        Error error = vcs.verifyAudioFilename(cardWithoutMatchingFileName, audioFilenames);

        assertThat(error.message, is(String.format(FILE_NOT_FOUND_ERROR_FORMAT, cardWithoutMatchingFileName.getAudio())));
    }

    @Test
    public void testNonEnglishChars() throws Exception {
        List<String> audioFilenames = newArrayList(ARABIC_CHARACTERS);
        Error error = vcs.verifyAudioFilename(cardWithArabicInAudioFile, audioFilenames);
        assertThat(error, nullValue());
    }
}