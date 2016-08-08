package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.Drive;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.api.response.CreateDeckResponse;
import org.mercycorps.translationcards.txcmaker.api.response.RetrieveDeckResponse;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.model.ImportDeckForm;
import org.mockito.Mock;

import javax.ws.rs.core.MultivaluedHashMap;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeckServiceTest {

    DeckService deckService;

    Deck deck;

    @Mock
    AuthUtils authUtils;
    @Mock
    Drive drive;
    private CreateDeckResponse createDeckResponse;
    private RetrieveDeckResponse retrieveDeckResponse;

    @Before
    public void setup() {
        initMocks(this);

        deck = Deck.stub();

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
    public void create_shouldAddErrorsToTheResult() throws Exception {
        deckService.create(formWithErrors(), createDeckResponse);

        verify(createDeckResponse, times(4)).addError(anyString());
    }

    @Test
    public void create_shouldAddWarningsToTheResult() throws Exception {
        deckService.create(formWithErrors(), createDeckResponse);

        verify(createDeckResponse, times(2)).addWarning(anyString());
    }

    @Test
    public void create_shouldAssignAnIdToTheNewDeck() throws Exception {
        deckService.create(formWithoutErrors(), createDeckResponse);

        verify(createDeckResponse).setId(anyInt());
    }

    @Test
    public void create_shouldAssignAnInvalidIdWhenThereAreErrors() throws Exception {
        deckService.create(formWithErrors(), createDeckResponse);

        verify(createDeckResponse).setId(anyInt());
    }

    private ImportDeckForm formWithErrors() {
        MultivaluedHashMap<String, String> formInput = new MultivaluedHashMap<>();
        formInput.add("deckName", "deck with errors");
        return new ImportDeckForm(formInput);
    }

    private ImportDeckForm formWithoutErrors() {
        MultivaluedHashMap<String, String> formInput = new MultivaluedHashMap<>();
        formInput.add("deckName", "deck without errors");
        return new ImportDeckForm(formInput);
    }
}