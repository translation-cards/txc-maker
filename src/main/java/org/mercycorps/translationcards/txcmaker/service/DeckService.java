package org.mercycorps.translationcards.txcmaker.service;


import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import org.mercycorps.translationcards.txcmaker.api.response.CreateDeckResponse;
import org.mercycorps.translationcards.txcmaker.api.response.RetrieveDeckResponse;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.model.ImportDeckForm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;


public class DeckService {

    AuthUtils authUtils;
    public static final String CSV_EXPORT_TYPE = "text/csv";


    public DeckService(AuthUtils authUtils) {
        this.authUtils = authUtils;
    }

    public void retrieve(int id, RetrieveDeckResponse retrieveDeckResponse) {
        if(id == 10) {
            retrieveDeckResponse.setDeck(Deck.stub());
        }
    }

    public void create(ImportDeckForm form, CreateDeckResponse createDeckResponse, Drive drive) {
        String spreadsheetFileId = form.getDocId();

        try {
            InputStream inputStream = drive.files().export(spreadsheetFileId, CSV_EXPORT_TYPE).executeMediaAsInputStream();
        } catch(GoogleJsonResponseException e) {
            createDeckResponse.addError("Invalid Document ID");
        } catch(IOException e) {
            //something bad happened
        }

        if (createDeckResponse.hasErrors()) {
            createDeckResponse.setId(-1);
        } else {
            createDeckResponse.setId(10);
        }
    }

    private void addFakeErrors(CreateDeckResponse createDeckResponse) {
        createDeckResponse.setId(-1);
        createDeckResponse.addError("Please provide a deck name");
        createDeckResponse.addError("Please provide a publisher");
        createDeckResponse.addError("Invalid Document ID");
        createDeckResponse.addError("Invalid Audio Directory ID");
        createDeckResponse.addWarning("Warning 1");
        createDeckResponse.addWarning("Warning 2");
    }

    public List<Deck> retrieveAll() {
        return Arrays.asList(Deck.stub());
    }
}
