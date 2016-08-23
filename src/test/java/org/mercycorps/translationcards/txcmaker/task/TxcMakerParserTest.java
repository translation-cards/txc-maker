package org.mercycorps.translationcards.txcmaker.task;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.language.LanguageService;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.service.TxcMakerParser;
import org.mockito.Mock;

import java.io.StringReader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TxcMakerParserTest {

    public static final String STUBBED_CSV =
            "Language,Label,Translation,Filename\n" +
            "ar,ar phrase,ar translation,ar.mp3\n" +
            "ps,ps phrase,ps translation,ps.mp3\n" +
            "fa,fa phrase,fa translation,fa.mp3";
    public static final int ARABIC = 0;
    public static final int PASHTO = 1;
    public static final int FARSI = 2;
    @Mock
    private LanguageService languageService;
    private TxcMakerParser txcMakerParser;
    private CSVParser csvParser;
    private Deck deck;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        txcMakerParser = new TxcMakerParser(languageService);
        csvParser = new CSVParser(new StringReader(STUBBED_CSV), CSVFormat.DEFAULT.withHeader());
        deck = new Deck();
    }

    @Test
    public void shouldParseAudioDirectoryIdFromURL() throws Exception {
        String audioDirectoryUrl = "https://drive.google.com/drive/u/0/folders/0B0o--EJjbYvqbWRXRGgzeDZ1X0U";

        String audioDirectoryId = txcMakerParser.parseAudioDirId(audioDirectoryUrl);

        assertThat(audioDirectoryId, is("0B0o--EJjbYvqbWRXRGgzeDZ1X0U"));
    }

    @Test
    public void shouldParseAudioDirectoryIdFromId() throws Exception {
        String audioDirectoryUrl = "0B0o--EJjbYvqbWRXRGgzeDZ1X0U";

        String audioDirectoryId = txcMakerParser.parseAudioDirId(audioDirectoryUrl);

        assertThat(audioDirectoryId, is("0B0o--EJjbYvqbWRXRGgzeDZ1X0U"));
    }

    @Test
    public void shouldParseSpreadsheetIdFromUrl() throws Exception {
        String spreadsheetUrl = "https://docs.google.com/spreadsheets/d/1GKHlnVLIc_GrBra5wCAk1BCxgXIAJZLAu7tcLCLPAAw/edit#gid=0";

        String spreadsheetId = txcMakerParser.parseDocId(spreadsheetUrl);

        assertThat(spreadsheetId, is("1GKHlnVLIc_GrBra5wCAk1BCxgXIAJZLAu7tcLCLPAAw"));
    }

    @Test
    public void shouldParseSpreadsheetIdFromId() throws Exception {
        String spreadsheetUrl = "1GKHlnVLIc_GrBra5wCAk1BCxgXIAJZLAu7tcLCLPAAw";

        String spreadsheetId = txcMakerParser.parseDocId(spreadsheetUrl);

        assertThat(spreadsheetId, is("1GKHlnVLIc_GrBra5wCAk1BCxgXIAJZLAu7tcLCLPAAw"));
    }

    @Test
    public void parseCsvIntoDeck_shouldSetIsoCodesForLanguages() throws Exception {
        txcMakerParser.parseCsvIntoDeck(deck, csvParser);

        assertThat(deck.languages.get(ARABIC).iso_code, is("ar"));
        assertThat(deck.languages.get(PASHTO).iso_code, is("ps"));
        assertThat(deck.languages.get(FARSI).iso_code, is("fa"));

    }

    @Test
    public void parseCsvIntoDeck_shouldSetLanguageLabelsForLanguages() throws Exception {
        when(languageService.getLanguageDisplayName("ar")).thenReturn("Arabic");
        when(languageService.getLanguageDisplayName("ps")).thenReturn("Pashto");
        when(languageService.getLanguageDisplayName("fa")).thenReturn("Farsi");

        txcMakerParser.parseCsvIntoDeck(deck, csvParser);

        assertThat(deck.languages.get(ARABIC).language_label, is("Arabic"));
        assertThat(deck.languages.get(PASHTO).language_label, is("Pashto"));
        assertThat(deck.languages.get(FARSI).language_label, is("Farsi"));
    }

    @Test
    public void parseCsvIntoDeck_shouldSetLabelsForTranslations() throws Exception {
        txcMakerParser.parseCsvIntoDeck(deck, csvParser);

        assertThat(deck.languages.get(ARABIC).cards.get(0).card_label, is("ar phrase"));
        assertThat(deck.languages.get(PASHTO).cards.get(0).card_label, is("ps phrase"));
        assertThat(deck.languages.get(FARSI).cards.get(0).card_label, is("fa phrase"));
    }

    @Test
    public void parseCsvIntoDeck_shouldSetDestinationTextForTranslations() throws Exception {
        txcMakerParser.parseCsvIntoDeck(deck, csvParser);

        assertThat(deck.languages.get(ARABIC).cards.get(0).dest_txt, is("ar translation"));
        assertThat(deck.languages.get(PASHTO).cards.get(0).dest_txt, is("ps translation"));
        assertThat(deck.languages.get(FARSI).cards.get(0).dest_txt, is("fa translation"));
    }

    @Test
    public void parseCsvIntoDeck_shouldSetFilenamesForTranslations() throws Exception {
        txcMakerParser.parseCsvIntoDeck(deck, csvParser);

        assertThat(deck.languages.get(ARABIC).cards.get(0).dest_audio, is("ar.mp3"));
        assertThat(deck.languages.get(PASHTO).cards.get(0).dest_audio, is("ps.mp3"));
        assertThat(deck.languages.get(FARSI).cards.get(0).dest_audio, is("fa.mp3"));
    }
}