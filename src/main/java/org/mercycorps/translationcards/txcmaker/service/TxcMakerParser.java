package org.mercycorps.translationcards.txcmaker.service;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mercycorps.translationcards.txcmaker.language.LanguageService;
import org.mercycorps.translationcards.txcmaker.model.*;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
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

    private static final String LANGUAGE_HEADER = "Language";
    private static final String SOURCE_PHRASE_HEADER = "Label";
    private static final String DESTINATION_PHRASE_HEADER = "Translation";
    private static final String AUDIO_FILENAME_HEADER = "Filename";

    private LanguageService languageService;

    @Autowired
    public TxcMakerParser(LanguageService languageService) {
        this.languageService = languageService;
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

    public NewDeck parseCsvIntoDeck(CSVParser parser, HttpServletRequest req, String sessionId) {
        int lineNumber = 1;
        List<Error> parsingErrors = new ArrayList<>();
        List<NewCard> cards = new ArrayList<>();
        for (CSVRecord row : parser) {
            lineNumber++;

            String languageIso = row.get(LANGUAGE_HEADER);
            String languageLabel = languageService.getLanguageDisplayName(languageIso);

            if("INVALID".equals(languageLabel)) {
                parsingErrors.add(new Error(lineNumber + "", true));
            }
            String audioFileName = row.get(AUDIO_FILENAME_HEADER);
            String sourcePhrase = row.get(SOURCE_PHRASE_HEADER);
            String destinationPhrase = row.get(DESTINATION_PHRASE_HEADER);
            Language language = new Language(languageIso, languageLabel);
            NewCard card = new NewCard(sourcePhrase, audioFileName, destinationPhrase, language);

            cards.add(card);
        }

        return new NewDeck("English",
                req.getParameter("deckName"),
                req.getParameter("publisher"),
                System.currentTimeMillis(),
                false,
                sessionId,
                req.getParameter("licenseUrl"),
                null,
                parsingErrors,
                buildTranslationsFromCards(cards),
                buildDestinationLanguageNames(cards));
    }

    public List<String> buildDestinationLanguageNames(List<NewCard> cards) {
        List<String> destinationLanguageNames = new ArrayList<>();
        for (NewCard card : cards) {
            if(!destinationLanguageNames.contains(card.getDestinationLanguageName())) {
                destinationLanguageNames.add(card.getDestinationLanguageName());
            }
        }
        return destinationLanguageNames;
    }

    public List<Translation> buildTranslationsFromCards(List<NewCard> cards) {
        Map<String, Translation> sourcePhraseToTranslation = new HashMap<>();
        for (NewCard card : cards) {
            String sourcePhrase = card.getSourcePhrase();
            if (sourcePhraseToTranslation.containsKey(sourcePhrase)) {
                sourcePhraseToTranslation.get(sourcePhrase).addCard(card);
            } else {
                sourcePhraseToTranslation.put(sourcePhrase, createTranslationFromCard(card));
            }
        }
        return new ArrayList<>(sourcePhraseToTranslation.values());
    }

    private Translation createTranslationFromCard(NewCard card) {
        List<NewCard> cardListForTranslation = new ArrayList<>();
        cardListForTranslation.add(card);
        return new Translation(cardListForTranslation);
    }
}
