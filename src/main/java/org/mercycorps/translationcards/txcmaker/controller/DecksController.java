package org.mercycorps.translationcards.txcmaker.controller;

import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Constraint;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.ImportDeckForm;
import org.mercycorps.translationcards.txcmaker.response.ImportDeckResponse;
import org.mercycorps.translationcards.txcmaker.response.ResponseFactory;
import org.mercycorps.translationcards.txcmaker.service.ImportDeckFormService;
import org.mercycorps.translationcards.txcmaker.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/decks")
public class DecksController {

    private ImportDeckFormService importDeckFormService;
    private AuthUtils authUtils;
    private ServletContext servletContext;
    private ResponseFactory responseFactory;
    private TaskService taskService;

    @Autowired
    public DecksController(ImportDeckFormService importDeckFormService, AuthUtils authUtils, ServletContext servletContext, ResponseFactory responseFactory, TaskService taskService) {
        this.importDeckFormService = importDeckFormService;
        this.authUtils = authUtils;
        this.servletContext = servletContext;
        this.responseFactory = responseFactory;
        this.taskService = taskService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity importDeck(@RequestBody ImportDeckForm importDeckForm, HttpServletRequest request) throws URISyntaxException {
        importDeckFormService.preProcessForm(importDeckForm);
        ImportDeckResponse importDeckResponse = responseFactory.newImportDeckResponse();
        Drive drive = getDrive(request, importDeckResponse);
        final String sessionId = request.getSession().getId();
        final List<Constraint> fieldsToVerify = importDeckForm.getFieldsToVerify(drive);
        if(drive != null) {
            importDeckFormService.verifyFormData(importDeckResponse, fieldsToVerify);
            taskService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);
        }
        return importDeckResponse.build();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.POST)
    public void assembleDeck(@PathVariable String id) {
        taskService.kickoffBuildDeckTask(id);
    }

    private Drive getDrive(HttpServletRequest request, ImportDeckResponse importDeckResponse) {
        Drive drive = null;
        try {
            drive = authUtils.getDriveOrOAuth(servletContext, request, null, false, request.getSession().getId());
        } catch(IOException e) {
            importDeckResponse.addError(new Error("Authorization failed", true));
        }
        return drive;
    }


}
