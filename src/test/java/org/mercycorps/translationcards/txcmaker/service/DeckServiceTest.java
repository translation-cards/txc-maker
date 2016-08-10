package org.mercycorps.translationcards.txcmaker.service;

import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.api.response.CreateDeckResponse;
import org.mercycorps.translationcards.txcmaker.api.response.RetrieveDeckResponse;
import org.mercycorps.translationcards.txcmaker.model.Deck;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Field;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeckServiceTest {

    DeckService deckService;

    List<Field> fields;

    private CreateDeckResponse createDeckResponse;
    private RetrieveDeckResponse retrieveDeckResponse;
    Error error;
    Deck deck;

    @Before
    public void setup() throws IOException{
        deckService = new DeckService();

        fields = new ArrayList<>();
        Field field = mock(Field.class);
        when(field.verify()).thenReturn(Collections.<Error>emptyList());
        fields.add(field);

        createDeckResponse = new CreateDeckResponse();
        retrieveDeckResponse = new RetrieveDeckResponse();
        error = new Error("someField", "some message");
        deck = Deck.STUBBED_DECK;
    }

    @Test
    public void shouldGetADeckWhenTheIdIsFound() throws Exception {
        deckService.retrieve(10, retrieveDeckResponse);

        assertThat(retrieveDeckResponse.getDeck(), is(deck));
    }

    @Test
    public void shouldGetAllDecks() throws Exception {
        List<Deck> decks = deckService.retrieveAll();

        assertThat(decks.size() > 0, is(true));
    }

    @Test
    public void verifyFormData_shouldAssignAnIdToTheNewDeck() throws Exception {
        deckService.verifyFormData(createDeckResponse, fields);

        assertThat(createDeckResponse.getId(), is(10));
    }

    @Test
    public void verifyFormData_shouldAssignAnInvalidIdWhenThereAreErrors() throws Exception {
        Field failedField = mock(Field.class);
        when(failedField.verify()).thenReturn(Collections.singletonList(error));
        fields.add(failedField);
        deckService.verifyFormData(createDeckResponse, fields);

        assertThat(createDeckResponse.getId(), is(-1));
    }
}