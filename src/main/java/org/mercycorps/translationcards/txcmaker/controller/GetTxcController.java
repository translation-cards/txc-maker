package org.mercycorps.translationcards.txcmaker.controller;

import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/get-txc")
public class GetTxcController {

  private AuthUtils authUtils;
  private ServletContext servletContext;

  @Autowired
  public GetTxcController(AuthUtils authUtils, ServletContext servletContext) {
    this.authUtils = authUtils;
    this.servletContext = servletContext;
  }

  @RequestMapping(method = RequestMethod.GET)
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    // We don't actually need the Drive service yet, but we authenticate in advance because
    // otherwise OAuth will send them back here anyway.
    Drive drive = authUtils.getDriveOrOAuth(servletContext, req, resp, true, req.getSession(true).getId());
    if (drive == null) {
      // We've already redirected.
      return;
    }
    RequestDispatcher view = req.getRequestDispatcher("/index.html");
    view.forward(req, resp);
  }


}
