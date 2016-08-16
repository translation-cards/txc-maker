package org.mercycorps.translationcards.txcmaker.task;

import com.google.gson.Gson;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.service.LanguageService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class TxcPortingUtility {

  private static final Pattern FILE_URL_MATCHER = Pattern.compile(
      "https?://docs.google.com/spreadsheets/d/(.*?)(/.*)?$");
  private static final Pattern DIR_URL_MATCHER = Pattern.compile(
      "https?://drive.google.com/corp/drive/folders/(.*)$");

  private static final String SRC_HEADER_LANGUAGE = "Language";
  private static final String SRC_HEADER_LABEL = "Label";
  private static final String SRC_HEADER_TRANSLATION_TEXT = "Translation";
  private static final String SRC_HEADER_FILENAME = "Filename";

  private LanguageService languageService;
  private Gson gson;

  public TxcPortingUtility(LanguageService languageService, Gson gson) {
    this.languageService = languageService;
    this.gson = gson;
  }

  public String buildTxcJson(Deck exportSpec) {
    return gson.toJson(exportSpec);
  }

  public String getSpreadsheetId(String spreadsheetFileString) {
    Matcher spreadsheetFileIdMatcher = FILE_URL_MATCHER.matcher(spreadsheetFileString);
    if (spreadsheetFileIdMatcher.matches()) {
      spreadsheetFileString = spreadsheetFileIdMatcher.group(1);
    }
    return spreadsheetFileString;
  }

  public String parseAudioDirId(String audioDirString) {
    Matcher audioDirIdMatcher = DIR_URL_MATCHER.matcher(audioDirString);
    if (audioDirIdMatcher.matches()) {
      audioDirString = audioDirIdMatcher.group(1);
    }
    return audioDirString;
  }

  public void parseCsvIntoDeck(Deck deck, CSVParser parser) {
    for(CSVRecord row : parser) {
      String languageIso = row.get(SRC_HEADER_LANGUAGE);
      String languageLabel = languageService.getLanguageDisplayName(languageIso);
      String audioFileName = row.get(SRC_HEADER_FILENAME);
      Card card = new Card()
              .setLabel(row.get(SRC_HEADER_LABEL))
              .setFilename(audioFileName)
              .setTranslationText(row.get(SRC_HEADER_TRANSLATION_TEXT));
      deck.addCard(languageIso, languageLabel, card);
    }
  }

}
