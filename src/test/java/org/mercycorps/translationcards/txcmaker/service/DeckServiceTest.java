package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.api.response.CreateDeckResponse;
import org.mercycorps.translationcards.txcmaker.api.response.RetrieveDeckResponse;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.model.ImportDeckForm;
import org.mockito.Mock;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeckServiceTest {

    public static final String DEFAULT_DOCUMENT_ID = "A Document ID";
    DeckService deckService;

    Deck deck;

    @Mock
    AuthUtils authUtils;
    Drive drive;
    private CreateDeckResponse createDeckResponse;
    private RetrieveDeckResponse retrieveDeckResponse;

    @Before
    public void setup() {
        initMocks(this);

        deck = Deck.stub();

        drive = mock(Drive.class, RETURNS_DEEP_STUBS);
        deckService = new DeckService(authUtils);
        createDeckResponse = mock(CreateDeckResponse.class);
        retrieveDeckResponse = mock(RetrieveDeckResponse.class);
    }

    @Test
    public void shouldGetADeckWhenTheIdIsFound() throws Exception {
        deckService.retrieve(10, retrieveDeckResponse);

        verify(retrieveDeckResponse).setDeck(any(Deck.class));
    }

    @Test
    public void shouldGetAllDecks() throws Exception {
        List<Deck> decks = deckService.retrieveAll();

        assertThat(decks.size() > 0, is(true));
    }

    @Test
    public void create_shouldAssignAnIdToTheNewDeck() throws Exception {
        deckService.create(formWithoutErrors(), createDeckResponse, drive);

        verify(createDeckResponse).setId(anyInt());
    }

    @Test
    public void create_shouldAssignAnInvalidIdWhenThereAreErrors() throws Exception {
        deckService.create(formWithErrors(), createDeckResponse, drive);

        verify(createDeckResponse).setId(-1);
    }

    @Test
    public void create_shouldNotHaveInvalidIDErrorWhenCreatingAValidDocument() throws Exception {
        deckService.create(formWithoutErrors(), createDeckResponse, drive);

        verify(createDeckResponse, times(0)).addError("Invalid Document ID");
    }

    @Test
    public void create_shouldAddAnInvalidDocumentIDErrorWhenDocumentIDIsInvalid() throws Exception {
        when(drive.files().export(DEFAULT_DOCUMENT_ID, DeckService.CSV_EXPORT_TYPE).executeMediaAsInputStream())
                .thenThrow(mock(GoogleJsonResponseException.class));

        deckService.create(formWithErrors(), createDeckResponse, drive);

        verify(createDeckResponse).addError("Invalid Document ID");
    }

    private ImportDeckForm formWithErrors() {
        when(createDeckResponse.hasErrors()).thenReturn(true);

        ImportDeckForm importDeckForm = new ImportDeckForm()
                .setDeckId("1234")
                .setDeckName("deck with errors")
                .setDocId(DEFAULT_DOCUMENT_ID);

//        MultivaluedHashMap<String, String> formInput = new MultivaluedHashMap<>();
//        formInput.add("deckName", "deck with errors");
//        formInput.add("docId", DEFAULT_DOCUMENT_ID);
//        formInput.add("audioDirId", "An Audio Directory ID");
//        formInput.add("publisher", "some publisher");
//        formInput.add("licenseUrl", "some license URL");
//        formInput.add("locked", "false");
//        formInput.add("deckId", "1234");

        return importDeckForm;
    }

    private ImportDeckForm formWithoutErrors()  {
        when(createDeckResponse.hasErrors()).thenReturn(false);

        ImportDeckForm importDeckForm = new ImportDeckForm()
                .setDeckId("1234")
                .setDeckName("deck without errors")
                .setDocId(DEFAULT_DOCUMENT_ID);


        return importDeckForm;
    }
}