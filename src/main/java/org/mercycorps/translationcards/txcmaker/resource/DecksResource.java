package org.mercycorps.translationcards.txcmaker.resource;

import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Field;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.ImportDeckForm;
import org.mercycorps.translationcards.txcmaker.service.DeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/decks")
public class DecksResource {

    private DeckService deckService;
    private AuthUtils authUtils;
    private ServletContext servletContext;
    private ResponseFactory responseFactory;

    @Autowired
    public DecksResource(DeckService deckService, AuthUtils authUtils, ServletContext servletContext, ResponseFactory responseFactory) {
        this.deckService = deckService;
        this.authUtils = authUtils;
        this.servletContext = servletContext;
        this.responseFactory = responseFactory;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity importDeck(@RequestBody ImportDeckForm importDeckForm, HttpServletRequest request) throws URISyntaxException {
        ImportDeckResponse importDeckResponse = responseFactory.newImportDeckResponse();
        Drive drive = getDrive(request, importDeckResponse);
        final String sessionId = request.getSession().getId();
        final List<Field> fieldsToVerify = importDeckForm.getFieldsToVerify(drive);
        if(drive != null) {
            deckService.verifyFormData(importDeckResponse, fieldsToVerify);
            deckService.kickoffVerifyDeckTask(importDeckResponse, sessionId, importDeckForm);
        }
        return importDeckResponse.build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public void assembleDeck(@RequestBody String sessionId) {
        deckService.kickoffBuildDeckTask(sessionId);
    }

    private Drive getDrive(HttpServletRequest request, ImportDeckResponse importDeckResponse) {
        Drive drive = null;
        try {
            drive = authUtils.getDriveOrOAuth(servletContext, request, null, false, request.getSession().getId());
        } catch(IOException e) {
            importDeckResponse.addError(new Error("", "Authorization failed"));
        }
        return drive;
    }


}
