package org.mercycorps.translationcards.txcmaker.task;

import com.google.gson.Gson;
import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.Deck;

import java.util.ArrayList;
import java.util.List;
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

  public static String getSpreadsheetId(HttpServletRequest req) {
    String spreadsheetFileId = req.getParameter("docId");
    Matcher spreadsheetFileIdMatcher = FILE_URL_MATCHER.matcher(spreadsheetFileId);
    if (spreadsheetFileIdMatcher.matches()) {
      spreadsheetFileId = spreadsheetFileIdMatcher.group(1);
    }
    return spreadsheetFileId;
  }

  public static String getAudioDirId(HttpServletRequest req) {
    String audioDirId = req.getParameter("audioDirId");
    Matcher audioDirIdMatcher = DIR_URL_MATCHER.matcher(audioDirId);
    if (audioDirIdMatcher.matches()) {
      audioDirId = audioDirIdMatcher.group(1);
    }
    return audioDirId;
  }

}
