package org.mercycorps.translationcards.txcmaker.servlet;

import com.google.api.services.drive.Drive;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mercycorps.translationcards.txcmaker.service.FileVerifier;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;

public class GetTxcServlet extends HttpServlet {

  private static final int BUFFER_SIZE = 1024;

  private byte[] buffer = new byte[BUFFER_SIZE];
  private GcsService gcsService = GcsServiceFactory.createGcsService();
  private AuthUtils authUtils;
  private FileVerifier fileVerifier;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    ServletContext servletContext = config.getServletContext();
    authUtils = (AuthUtils) servletContext.getAttribute("authUtils");
    fileVerifier = (FileVerifier) servletContext.getAttribute("fileVerifier");
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    // We don't actually need the Drive service yet, but we authenticate in advance because
    // otherwise OAuth will send them back here anyway.
    Drive drive = authUtils.getDriveOrOAuth(getServletContext(), req, resp, true, req.getSession(true).getId());
    if (drive == null) {
      // We've already redirected.
      return;
    }
    RequestDispatcher view = req.getRequestDispatcher("/index.html");
    view.forward(req, resp);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Drive drive = authUtils.getDriveOrOAuth(getServletContext(), req, resp, false, req.getSession(true).getId());
    if (drive == null) {
      resp.getWriter().println("You haven't provided Drive authentication.");
      return;
    }

    fileVerifier.verify(req.getParameter("audioDirId"), req.getParameter("docId"), drive);
    if (fileVerifier.getErrors().size() != 0) {
      resp.getWriter().println("<p>");
      for (String error : fileVerifier.getErrors()) {
        resp.getWriter().println(error + "<br/>");
      }
      resp.getWriter().println("Cannot build the TXC.");
      resp.getWriter().println("</p>");
      return;
    }

    String sessionId=req.getSession(true).getId();
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
    if (fileVerifier.getWarnings().size() == 0) {
      resp.getWriter().println(
          "<p>The file is being assembled and should arrive in Drive in a minute or two.</p>");
    } else {
      resp.getWriter().println("<p>");
      for (String warning : fileVerifier.getWarnings()) {
        resp.getWriter().println(warning + "<br/>");
      }
      resp.getWriter().println(
          "That said, the file is being assembled and should arrive in Drive in a minute or two.");
      resp.getWriter().println("</p>");
    }
  }


}
