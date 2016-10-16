package org.mercycorps.translationcards.txcmaker.service;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mercycorps.translationcards.txcmaker.language.LanguageService;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.ImportDeckForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TxcMakerParser {

    private static final Pattern FILE_URL_MATCHER = Pattern.compile(
            "https?://docs.google.com/spreadsheets/d/(.*?)(/.*)?$");
    private static final Pattern DIR_URL_MATCHER = Pattern.compile(
            "https?://drive.google.com/drive/(.*)folders/(.*)$");

    private static final String SRC_HEADER_LANGUAGE = "Language";
    private static final String SRC_HEADER_LABEL = "Label";
    private static final String SRC_HEADER_TRANSLATION_TEXT = "Translation";
    private static final String SRC_HEADER_FILENAME = "Filename";
    private final List<String> validHeaders;

    private LanguageService languageService;

    @Autowired
    public TxcMakerParser(LanguageService languageService) {
        this.languageService = languageService;
        validHeaders = Arrays.asList(SRC_HEADER_FILENAME, SRC_HEADER_LABEL, SRC_HEADER_LANGUAGE, SRC_HEADER_TRANSLATION_TEXT);
    }

    public String parseDocId(String documentIdString) {
        if(documentIdString == null) {
            return "";
        }
        Matcher spreadsheetIdMatcher = FILE_URL_MATCHER.matcher(documentIdString);
        if (spreadsheetIdMatcher.matches()) {
            documentIdString = spreadsheetIdMatcher.group(1);
        }
        return documentIdString;
    }

    public String parseAudioDirId(String audioDirectoryString) {
        if(audioDirectoryString == null) {
            return "";
        }
        Matcher audioDirectoryIdMatcher = DIR_URL_MATCHER.matcher(audioDirectoryString);
        if (audioDirectoryIdMatcher.matches()) {
            audioDirectoryString = audioDirectoryIdMatcher.group(2);
        }
        return audioDirectoryString;
    }

    public void parseCsvIntoDeck(Deck deck, CSVParser parser) {
        int lineNumber = 2;
        validateCSVHeaders(parser);
        for (CSVRecord row : parser) {
            String languageIso = row.get(SRC_HEADER_LANGUAGE);
            String languageLabel = languageService.getLanguageDisplayName(languageIso);
            if("INVALID".equals(languageLabel)) {
                deck.parseErrors.add(new Error(lineNumber + "", true));
            }
            String audioFileName = row.get(SRC_HEADER_FILENAME);
            Card card = new Card()
                    .setLabel(row.get(SRC_HEADER_LABEL))
                    .setFilename(audioFileName)
                    .setTranslationText(row.get(SRC_HEADER_TRANSLATION_TEXT));
            deck.addCard(languageIso, languageLabel, card);
            lineNumber++;
        }
    }

    private void validateCSVHeaders(CSVParser parser) {
        final Map<String, Integer> headerMap = parser.getHeaderMap();
        for(String header : headerMap.keySet()) {
            if(!validHeaders.contains(header)) {
                throw new IllegalArgumentException("CSV Header '" + header + "' is invalid. Expecting one of " + validHeaders.toString());
            }
        }
    }

}
