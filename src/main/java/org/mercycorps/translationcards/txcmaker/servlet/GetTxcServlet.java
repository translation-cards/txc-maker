package org.mercycorps.translationcards.txcmaker.servlet;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mercycorps.translationcards.txcmaker.task.TxcPortingUtility;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;

public class GetTxcServlet extends HttpServlet {

  private static final String CSV_EXPORT_TYPE = "text/csv";

  private static final String SRC_HEADER_LANGUAGE = "Language";
  private static final String SRC_HEADER_LABEL = "Label";
  private static final String SRC_HEADER_TRANSLATION_TEXT = "Translation";
  private static final String SRC_HEADER_FILENAME = "Filename";

  private static final int BUFFER_SIZE = 1024;

  private byte[] buffer = new byte[BUFFER_SIZE];
  private GcsService gcsService = GcsServiceFactory.createGcsService();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    // We don't actually need the Drive service yet, but we authenticate in advance because
    // otherwise OAuth will send them back here anyway.

    String sessionId=req.getSession(true).getId();
    Drive drive = AuthUtils.getDriveOrOAuth(getServletContext(), req, resp, sessionId, true);
    if (drive == null) {
      // We've already redirected.
      return;
    }
    RequestDispatcher view = req.getRequestDispatcher("/index.html");
    view.forward(req, resp);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String sessionId=req.getSession(true).getId();
    Drive drive = AuthUtils.getDriveOrOAuth(getServletContext(), req, resp, sessionId, false);
    if (drive == null) {
      resp.getWriter().println("You haven't provided Drive authentication.");
      return;
    }

    List<String> warnings = new ArrayList<String>();
    List<String> errors = new ArrayList<String>();
    verify(sessionId, req, resp, warnings, errors);
    if (errors.size() != 0) {
      resp.getWriter().println("<p>");
      for (String error : errors) {
        resp.getWriter().println(error + "<br/>");
      }
      resp.getWriter().println("Cannot build the TXC.");
      resp.getWriter().println("</p>");
      return;
    }

    Queue queue = QueueFactory.getQueue("queue-txc-building");
    TaskOptions taskOptions = TaskOptions.Builder.withUrl("/tasks/txc-build")
        .param("sessionId", sessionId)
        .param("deckName", req.getParameter("deckName"))
        .param("publisher", req.getParameter("publisher"))
        .param("deckId", req.getParameter("deckId"))
        .param("docId", req.getParameter("docId"))
        .param("audioDirId", req.getParameter("audioDirId"))
        .param("licenceUrl", req.getParameter("licenseUrl"));
    if ((req.getParameter("locked") != null) && !req.getParameter("locked").isEmpty()) {
      taskOptions = taskOptions.param("locked", req.getParameter("locked"));
    }
    queue.add(taskOptions);
    if (warnings.size() == 0) {
      resp.getWriter().println(
          "<p>The file is being assembled and should arrive in Drive in a minute or two.</p>");
    } else {
      resp.getWriter().println("<p>");
      for (String warning : warnings) {
        resp.getWriter().println(warning + "<br/>");
      }
      resp.getWriter().println(
          "That said, the file is being assembled and should arrive in Drive in a minute or two.");
      resp.getWriter().println("</p>");
    }
  }

  private String getUserId() {
    UserService userService = UserServiceFactory.getUserService();
    return userService.getCurrentUser().getUserId();
  }

  private void verify(String sessionId, HttpServletRequest req, HttpServletResponse resp,
      List<String> warnings, List<String> errors) throws IOException {

    Drive drive = AuthUtils.getDriveOrOAuth(getServletContext(), req, resp, sessionId, false);
    String audioDirId = TxcPortingUtility.getAudioDirId(req);
    ChildList audioList = drive.children().list(audioDirId).execute();
    Map<String, String> audioFileIds = new HashMap<String, String>();
    for (ChildReference audioRef : audioList.getItems()) {
      File audioFile = drive.files().get(audioRef.getId()).execute();
      audioFileIds.put(audioFile.getOriginalFilename(), audioRef.getId());
    }
    String spreadsheetFileId = TxcPortingUtility.getSpreadsheetId(req);
    Drive.Files.Export sheetExport = drive.files().export(spreadsheetFileId, CSV_EXPORT_TYPE);
    Reader reader = new InputStreamReader(sheetExport.executeMediaAsInputStream());
    CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
    Set<String> includedAudioFiles = new HashSet<String>();
    try {
      for (CSVRecord row : parser) {
        String filename = row.get(SRC_HEADER_FILENAME);
        if (includedAudioFiles.contains(filename)) {
          warnings.add(String.format("Used %s multiple times.", filename));
          continue;
        }
        includedAudioFiles.add(filename);
        if (!audioFileIds.containsKey(filename)) {
          errors.add(String.format("Unknown file %s.", filename));
        }
      }
    } finally {
      parser.close();
      reader.close();
    }
  }
}
