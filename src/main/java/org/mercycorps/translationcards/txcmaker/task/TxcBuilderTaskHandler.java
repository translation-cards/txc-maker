package org.mercycorps.translationcards.txcmaker.task;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.channels.Channels;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Card;
import org.mercycorps.translationcards.txcmaker.model.Deck;

public class TxcBuilderTaskHandler extends HttpServlet {

  private static final String CSV_EXPORT_TYPE = "text/csv";

  private static final String SRC_HEADER_LANGUAGE = "Language";
  private static final String SRC_HEADER_LABEL = "Label";
  private static final String SRC_HEADER_TRANSLATION_TEXT = "Translation";
  private static final String SRC_HEADER_FILENAME = "Filename";

  private static final int BUFFER_SIZE = 1024;
  private static final String GCS_BUCKET_NAME = "translation-cards-dev.appspot.com";

  private static final Pattern FILE_URL_MATCHER = Pattern.compile(
      "https?://docs.google.com/spreadsheets/d/(.*?)(/.*)?$");
  private static final Pattern DIR_URL_MATCHER = Pattern.compile(
      "https?://drive.google.com/corp/drive/folders/(.*)$");

  private byte[] buffer = new byte[BUFFER_SIZE];
  private GcsService gcsService = GcsServiceFactory.createGcsService();

  AuthUtils authUtils = new AuthUtils();

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    produceTxcJson(req, resp);
  }

  private void produceTxcJson(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Drive drive = authUtils.getDriveOrOAuth(
        getServletContext(), req, resp, false);

    Random random = new Random();
    GcsFilename gcsFilename = new GcsFilename(
        GCS_BUCKET_NAME, String.format("tmp-txc-%d", random.nextInt()));
    OutputStream gcsOutput = Channels.newOutputStream(
        gcsService.createOrReplace(gcsFilename, GcsFileOptions.getDefaultInstance()));

    String audioDirId = assembleTxc(req, drive, gcsOutput);

    pushTxcToDrive(req, drive, gcsFilename, audioDirId);

    resp.getWriter().println(
        "Your TXC is being assembled and the file should arrive in Drive in a minute or two.");
  }

  private String assembleTxc(HttpServletRequest req, Drive drive, OutputStream gcsOutput) throws IOException {
    Deck exportSpec = createExportSpec(req);
    String audioDirId = TxcPortingUtility.getAudioDirId(req);
    ChildList audioList = drive.children().list(audioDirId).execute();
    Map<String, String> audioFileIds = readAudioFileIdsFromCsv(drive, audioList);
    String spreadsheetFileId = TxcPortingUtility.getSpreadsheetId(req);
    Drive.Files.Export sheetExport = drive.files().export(spreadsheetFileId, CSV_EXPORT_TYPE);
    Reader reader = new InputStreamReader(sheetExport.executeMediaAsInputStream());
    CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
    Set<String> includedAudioFiles = new HashSet<String>();
    ZipOutputStream zipOutput = new ZipOutputStream(gcsOutput);
    try {
      for (CSVRecord row : parser) {
        String language = row.get(SRC_HEADER_LANGUAGE);
        String filename = row.get(SRC_HEADER_FILENAME);
        Card card = createCardSpec(row, filename);
        exportSpec.addCard(language, card);
        if (includedAudioFiles.contains(filename)) {
          continue;
        }
        includedAudioFiles.add(filename);
        zipOutput.putNextEntry(new ZipEntry(filename));
        drive.files().get(audioFileIds.get(filename)).executeMediaAndDownloadTo(zipOutput);
        zipOutput.closeEntry();
      }
      zipOutput.putNextEntry(new ZipEntry("card_deck.json"));
      zipOutput.write(TxcPortingUtility.buildTxcJson(exportSpec).getBytes());
      zipOutput.closeEntry();
    } finally {
      parser.close();
      reader.close();
      zipOutput.close();
    }
    return audioDirId;
  }

  private Card createCardSpec(CSVRecord row, String filename) {
    return new Card()
            .setLabel(row.get(SRC_HEADER_LABEL))
            .setFilename(filename)
            .setTranslationText(row.get(SRC_HEADER_TRANSLATION_TEXT));
  }

  private Deck createExportSpec(HttpServletRequest req) {
    return new Deck()
        .setDeckLabel(req.getParameter("deckName"))
        .setPublisher(req.getParameter("publisher"))
        .setDeckId(req.getParameter("deckId"))
        .setTimestamp(System.currentTimeMillis())
        .setLicenseUrl(req.getParameter("licenseUrl"))
        .setLocked(req.getParameter("locked") != null);
  }

  private Map<String, String> readAudioFileIdsFromCsv(Drive drive, ChildList audioList) throws IOException {
    Map<String, String> audioFileIds = new HashMap<String, String>();
    for (ChildReference audioRef : audioList.getItems()) {
      File audioFile = drive.files().get(audioRef.getId()).execute();
      audioFileIds.put(audioFile.getOriginalFilename(), audioRef.getId());
    }
    return audioFileIds;
  }

  private void pushTxcToDrive(HttpServletRequest req, Drive drive, GcsFilename gcsFilename, String audioDirId) throws IOException {
    File targetFileInfo = new File();
    String targetFilename = req.getParameter("deckName")
        .replaceAll(" ", "_")
        .replaceAll("[^a-zA-Z_]", "");
    targetFilename += ".txc";
    targetFileInfo.setTitle(targetFilename);
    targetFileInfo.setParents(Collections.singletonList(new ParentReference().setId(audioDirId)));
    InputStream txcContentStream = Channels.newInputStream(
        gcsService.openPrefetchingReadChannel(gcsFilename, 0, BUFFER_SIZE));
    drive.files().insert(targetFileInfo, new InputStreamContent(null, txcContentStream)).execute();
  }
}

