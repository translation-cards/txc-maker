package org.mercycorps.translationcards.txcmaker.service;

import com.google.api.services.drive.Drive;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeckServiceTest {

    DeckService deckService;

    Deck deck;

    @Mock
    AuthUtils authUtils;
    @Mock
    Drive drive;

    @Before
    public void setup() {
        initMocks(this);

        deck = Deck.stub();

        deckService = new DeckService(authUtils);
    }

    @Test
    public void shouldGetADeck() throws Exception {
        Deck deck = deckService.retrieve(1);

        assertNotNull(deck);
    }

    @Test
    public void create_shouldGetDriveCredentials() throws Exception {


    }
}