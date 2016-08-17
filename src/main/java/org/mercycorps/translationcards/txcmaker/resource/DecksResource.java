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
    ServletContext servletContext;

    @Autowired
    public DecksResource(DeckService deckService, AuthUtils authUtils, ServletContext servletContext) {
        this.deckService = deckService;
        this.authUtils = authUtils;
        this.servletContext = servletContext;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity importDeck(@RequestBody ImportDeckForm importDeckForm, HttpServletRequest request) throws URISyntaxException {
        CreateDeckResponse createDeckResponse = new CreateDeckResponse();
        Drive drive = getDrive(request, createDeckResponse);
        final String sessionId = request.getSession().getId();
        final List<Field> fieldsToVerify = importDeckForm.getFieldsToVerify(drive);
        if(drive != null) {
            deckService.verifyFormData(createDeckResponse, fieldsToVerify);
            deckService.kickoffVerifyDeckTask(createDeckResponse, sessionId, importDeckForm);
        }
        return createDeckResponse.build();
    }

    private Drive getDrive(HttpServletRequest request, CreateDeckResponse createDeckResponse) {
        Drive drive = null;
        try {
            drive = authUtils.getDriveOrOAuth(servletContext, request, null, false, request.getSession().getId());
        } catch(IOException e) {
            createDeckResponse.addError(new Error("", "Authorization failed"));
        }
        return drive;
    }


}
