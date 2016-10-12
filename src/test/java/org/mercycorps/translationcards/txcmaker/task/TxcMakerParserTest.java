package org.mercycorps.translationcards.txcmaker.task;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.language.LanguageService;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.Language;
import org.mercycorps.translationcards.txcmaker.model.NewCard;
import org.mercycorps.translationcards.txcmaker.model.Translation;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mercycorps.translationcards.txcmaker.service.TxcMakerParser;
import org.mockito.Mock;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
    private List<NewCard> cards = new ArrayList<>();

    private NewCard helloInSpanish;
    private NewCard goodbyeInArabic;
    private NewCard helloInArabic;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        final Language ARABIC_LANGUAGE = new Language("ar", "Arabic");
        final Language SPANISH_LANGUAGE = new Language("es", "Spanish");

        helloInSpanish = new NewCard("Hello", "helloEs.mp3", "Hola", new ArrayList<Error>(), SPANISH_LANGUAGE);
        goodbyeInArabic = new NewCard("Goodbye", "goodbyeAr.mp3", "وداع", new ArrayList<Error>(), ARABIC_LANGUAGE);
        helloInArabic = new NewCard("Hello", "helloAr.mp3", "هتاف للترحيب", new ArrayList<Error>(), ARABIC_LANGUAGE);

        when(languageService.getLanguageDisplayName("ar"))
                .thenReturn("Arabic");
        when(languageService.getLanguageDisplayName("ps"))
                .thenReturn("Pashto");
        when(languageService.getLanguageDisplayName("fa"))
                .thenReturn("Farsi");

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
    public void shouldNotAttemptToParseNullAudioDirectoryString() throws Exception {
        String audioDirectoryString = null;

        String audioDirectoryId = txcMakerParser.parseAudioDirId(audioDirectoryString);

        assertThat(audioDirectoryId, is(""));
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
    public void shouldNotAttemptToParseNullSpreadsheetString() throws Exception {
        String spreadsheetString = null;

        String spreadsheetId = txcMakerParser.parseAudioDirId(spreadsheetString);

        assertThat(spreadsheetId, is(""));
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

    @Test
    public void parseCsvIntoDeck_shouldAddErrorsForInvalidISOCodes() throws Exception {
        String stubbedCsv =
                "Language,Label,Translation,Filename\n" +
                "abc,ar phrase,ar translation,ar.mp3\n" +
                "abc,ps phrase,ps translation,ps.mp3\n" +
                "fa,fa phrase,fa translation,fa.mp3";
        csvParser = new CSVParser(new StringReader(stubbedCsv), CSVFormat.DEFAULT.withHeader());
        when(languageService.getLanguageDisplayName("abc"))
                .thenReturn("INVALID");

        txcMakerParser.parseCsvIntoDeck(deck, csvParser);

        assertThat(deck.parseErrors.size(), is(2));
        assertThat(deck.parseErrors.get(0).message, is("2"));
        assertThat(deck.parseErrors.get(1).message, is("3"));
    }

    @Test
    public void shouldCreateOneTranslationFromOneCard() {
        cards = new ArrayList<NewCard>() {{
            add(helloInSpanish);
        }};

        List<Translation> actual = txcMakerParser.buildTranslationsFromCards(cards);

        assertThat(actual.size(), is(1));
        assertThat(actual.get(0).getSourcePhrase(), is("Hello"));
    }

    @Test
    public void shouldAddTwoSeparateTranslations() {
        cards = new ArrayList<NewCard>() {{
            add(helloInSpanish);
            add(goodbyeInArabic);
        }};

        List<Translation> actual = txcMakerParser.buildTranslationsFromCards(cards);

        assertThat(actual.size(), is(2));
    }

    @Test
    public void shouldGroupTranslationsByTheirSourcePhrase() {
        cards = new ArrayList<NewCard>() {{
            add(helloInSpanish);
            add(helloInArabic);
        }};

        List<Translation> actual = txcMakerParser.buildTranslationsFromCards(cards);

        assertThat(actual.size(), is(1));
        assertThat(actual.get(0).getCards().size(), is(2));
    }

    @Test
    public void shouldHaveOnlySpanishInDestinationLanguageNames() {
        cards = new ArrayList<NewCard>() {{
            add(helloInSpanish);
        }};

        List<String> actual = txcMakerParser.buildDestinationLanguageNames(cards);

        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is("Spanish"));
    }

    @Test
    public void shouldHaveBothArabicAndSpanishInDestinationLanguageNames() {
        cards = new ArrayList<NewCard>() {{
            add(helloInSpanish);
            add(helloInArabic);
        }};

        List<String> actual = txcMakerParser.buildDestinationLanguageNames(cards);

        assertThat(actual.size(), is(2));
        assertThat(actual.contains("Spanish"), is(true));
        assertThat(actual.contains("Arabic"), is(true));
    }
}