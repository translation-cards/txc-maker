package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.Drive;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.api.CreateDeckResponse;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mockito.Mock;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeckServiceTest {

    DeckService deckService;

    Deck deck;

    @Mock
    AuthUtils authUtils;
    @Mock
    Drive drive;
    private CreateDeckResponse createDeckResponse;

    @Before
    public void setup() {
        initMocks(this);

        deck = Deck.stub();

        deckService = new DeckService(authUtils);
        createDeckResponse = mock(CreateDeckResponse.class);
    }

    @Test
    public void shouldGetADeck() throws Exception {
        Deck deck = deckService.retrieve(1);

        assertNotNull(deck);
    }

    @Test
    public void shouldGetAllDecks() throws Exception {
        List<Deck> decks = deckService.retrieveAll();

        assertThat(decks.size() > 0, is(true));
    }

    @Test
    public void create_shouldAddErrorsToTheResult() throws Exception {
        deck = deckWithErrors();

        deckService.create(deck, createDeckResponse);

        verify(createDeckResponse).addError(anyString());
    }

    @Test
    public void create_shouldAddWarningsToTheResult() throws Exception {
        deck = deckWithErrors();

        deckService.create(deck, createDeckResponse);

        verify(createDeckResponse, times(2)).addWarning(anyString());
    }

    @Test
    public void create_shouldAssignAnIdToTheNewDeck() throws Exception {
        deckService.create(deck, createDeckResponse);

        verify(createDeckResponse).setId(anyInt());
    }

    @Test
    public void create_shouldAssignAnInvalidIdWhenThereAreErrors() throws Exception {
        deck = deckWithErrors();

        deckService.create(deck, createDeckResponse);

        verify(createDeckResponse).setId(anyInt());
    }

    private Deck deckWithErrors() {
        return new Deck()
                .setDeckLabel("deck with errors");
    }
}