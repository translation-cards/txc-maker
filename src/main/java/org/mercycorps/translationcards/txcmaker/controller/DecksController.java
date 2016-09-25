package org.mercycorps.translationcards.txcmaker.controller;

import com.google.api.client.util.IOUtils;
import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Constraint;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.ImportDeckForm;
import org.mercycorps.translationcards.txcmaker.response.ImportDeckResponse;
import org.mercycorps.translationcards.txcmaker.response.ResponseFactory;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
import org.mercycorps.translationcards.txcmaker.service.ImportDeckService;
import org.mercycorps.translationcards.txcmaker.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/decks")
public class DecksController {

    private ImportDeckService importDeckService;
    private DriveService driveService;
    private AuthUtils authUtils;
    private ServletContext servletContext;
    private ResponseFactory responseFactory;
    private TaskService taskService;

    @Autowired
    public DecksController(ImportDeckService importDeckService, DriveService driveService, AuthUtils authUtils, ServletContext servletContext, ResponseFactory responseFactory, TaskService taskService) {
        this.importDeckService = importDeckService;
        this.driveService = driveService;
        this.authUtils = authUtils;
        this.servletContext = servletContext;
        this.responseFactory = responseFactory;
        this.taskService = taskService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity importDeck(@RequestBody ImportDeckForm importDeckForm, HttpServletRequest request) throws URISyntaxException {
        Drive drive = null;
        ImportDeckResponse importDeckResponse = responseFactory.newImportDeckResponse();
        try {
            drive = getDrive(request);
        } catch (IOException ioe) {
            importDeckResponse.addError(new Error("Authorization failed", true));
        }
        if(drive != null) {
            importDeckService.preProcessForm(importDeckForm);
            final String sessionId = request.getSession().getId();
            final List<Constraint> fieldsToVerify = importDeckForm.getFieldsToVerify(drive);
            importDeckService.processForm(importDeckForm, request, importDeckResponse, drive, sessionId, fieldsToVerify);
            taskService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);
        }
        return importDeckResponse.build();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.POST)
    public void assembleDeck(@PathVariable String id) {
        taskService.kickoffBuildDeckTask(id);
    }

    @RequestMapping(path = "/{id}/{fileId:.+}", method = RequestMethod.GET)
    public void getAudioFile(@PathVariable String id, @PathVariable String fileId, HttpServletRequest request, HttpServletResponse response) {
        try {
            InputStream inputStream = driveService.getFileInputStream(getDrive(request), fileId);

            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private Drive getDrive(HttpServletRequest request) throws IOException {
        return authUtils.getDriveOrOAuth(servletContext, request, null, false, request.getSession().getId());
    }


}
