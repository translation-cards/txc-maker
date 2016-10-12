package org.mercycorps.translationcards.txcmaker.task;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.language.LanguageService;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.*;
import org.mercycorps.translationcards.txcmaker.service.TxcMakerParser;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
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
    @Mock
    private HttpServletRequest request;

    private TxcMakerParser txcMakerParser;
    private CSVParser csvParser;
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
        NewDeck deck = txcMakerParser.parseCsvIntoDeck(csvParser, request);

        assertThat(getDestinationLanguageForFirstCardWithPhrase(deck, "ar phrase").iso_code, is("ar"));
        assertThat(getDestinationLanguageForFirstCardWithPhrase(deck, "ps phrase").iso_code, is("ps"));
        assertThat(getDestinationLanguageForFirstCardWithPhrase(deck, "fa phrase").iso_code, is("fa"));
    }

    @Test
    public void parseCsvIntoDeck_shouldSetLanguageLabelsForLanguages() throws Exception {
        when(languageService.getLanguageDisplayName("ar")).thenReturn("Arabic");
        when(languageService.getLanguageDisplayName("ps")).thenReturn("Pashto");
        when(languageService.getLanguageDisplayName("fa")).thenReturn("Farsi");

        NewDeck deck = txcMakerParser.parseCsvIntoDeck(csvParser, request);

        assertThat(getDestinationLanguageForFirstCardWithPhrase(deck, "ar phrase").language_label, is("Arabic"));
        assertThat(getDestinationLanguageForFirstCardWithPhrase(deck, "ps phrase").language_label, is("Pashto"));
        assertThat(getDestinationLanguageForFirstCardWithPhrase(deck, "fa phrase").language_label, is("Farsi"));
    }

    @Test
    public void parseCsvIntoDeck_shouldSetLabelsForTranslations() throws Exception {
        NewDeck actual = txcMakerParser.parseCsvIntoDeck(csvParser, request);

        assertThat(actual.getTranslationForSourcePhrase("ar phrase"), is(notNullValue()));
        assertThat(actual.getTranslationForSourcePhrase("ps phrase"), is(notNullValue()));
        assertThat(actual.getTranslationForSourcePhrase("fa phrase"), is(notNullValue()));
    }

    @Test
    public void parseCsvIntoDeck_shouldSetDestinationTextForTranslations() throws Exception {
        NewDeck deck = txcMakerParser.parseCsvIntoDeck(csvParser, request);

        assertThat(getCardForSourcePhrase(deck, "ar phrase").getDestinationPhrase(), is("ar translation"));
        assertThat(getCardForSourcePhrase(deck, "ps phrase").getDestinationPhrase(), is("ps translation"));
        assertThat(getCardForSourcePhrase(deck, "fa phrase").getDestinationPhrase(), is("fa translation"));
    }

    @Test
    public void parseCsvIntoDeck_shouldSetFilenamesForTranslations() throws Exception {
        NewDeck deck = txcMakerParser.parseCsvIntoDeck(csvParser, request);

        assertThat(getCardForSourcePhrase(deck, "ar phrase").getAudio(), is("ar.mp3"));
        assertThat(getCardForSourcePhrase(deck, "ps phrase").getAudio(), is("ps.mp3"));
        assertThat(getCardForSourcePhrase(deck, "fa phrase").getAudio(), is("fa.mp3"));
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

        NewDeck deck = txcMakerParser.parseCsvIntoDeck(csvParser, request);

        assertThat(deck.getParsingErrors().size(), is(2));
        assertThat(deck.getParsingErrors().get(0).message, is("2"));
        assertThat(deck.getParsingErrors().get(1).message, is("3"));
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

    private Language getDestinationLanguageForFirstCardWithPhrase(NewDeck deck, String sourcePhrase) {
        return getCardForSourcePhrase(deck, sourcePhrase).getDestinationLanguage();
    }

    private NewCard getCardForSourcePhrase(NewDeck deck, String sourcePhrase) {
        return deck.getTranslationForSourcePhrase(sourcePhrase).getCards().get(0);
    }
}
