package org.mercycorps.translationcards.txcmaker.task;

import com.google.gson.Gson;
import org.mercycorps.translationcards.txcmaker.model.Deck;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class TxcPortingUtility {

  private static final Pattern FILE_URL_MATCHER = Pattern.compile(
      "https?://docs.google.com/spreadsheets/d/(.*?)(/.*)?$");
  private static final Pattern DIR_URL_MATCHER = Pattern.compile(
      "https?://drive.google.com/corp/drive/folders/(.*)$");

  public static String buildTxcJson(Deck exportSpec) {
    Gson gson = new Gson();
    return gson.toJson(exportSpec);
  }

  public static String getSpreadsheetId(String spreadsheetFileString) {
    Matcher spreadsheetFileIdMatcher = FILE_URL_MATCHER.matcher(spreadsheetFileString);
    if (spreadsheetFileIdMatcher.matches()) {
      spreadsheetFileString = spreadsheetFileIdMatcher.group(1);
    }
    return spreadsheetFileString;
  }

  public static String parseAudioDirId(String audioDirString) {
    Matcher audioDirIdMatcher = DIR_URL_MATCHER.matcher(audioDirString);
    if (audioDirIdMatcher.matches()) {
      audioDirString = audioDirIdMatcher.group(1);
    }
    return audioDirString;
  }

}
