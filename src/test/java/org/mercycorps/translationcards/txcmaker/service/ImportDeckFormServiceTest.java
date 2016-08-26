package org.mercycorps.translationcards.txcmaker.service;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.taskqueue.Queue;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.model.Error;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Constraint;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.ImportDeckForm;
import org.mercycorps.translationcards.txcmaker.response.ImportDeckResponse;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ImportDeckFormServiceTest {

    ImportDeckFormService importDeckFormService;

    List<Constraint> constraints;

    Error error;
    @Mock
    private ChannelService channelService;
    @Mock
    private Queue taskQueue;
    @Mock
    private TxcMakerParser txcMakerParser;

    private ImportDeckResponse importDeckResponse;
    private ImportDeckForm importDeckForm;
    private Deck deck;

    @Before
    public void setup() throws IOException{
        initMocks(this);

        constraints = new ArrayList<>();
        Constraint constraint = mock(Constraint.class);
        when(constraint.verify()).thenReturn(Collections.<Error>emptyList());
        constraints.add(constraint);

        importDeckResponse = new ImportDeckResponse();
        error = new Error("some message", true);
        importDeckForm = new ImportDeckForm()
                .setDeckName("deck name")
                .setAudioDirId("audio dir id string")
                .setDocId("doc id string")
                .setPublisher("publisher");

        importDeckFormService = new ImportDeckFormService(txcMakerParser);
    }

    @Test
    public void verifyFormData_shouldAddErrorsToTheResponseWhenThereAreErrors() throws Exception {
        Constraint failedConstraint = mock(Constraint.class);
        List<Error> fieldErrors = Collections.singletonList(error);
        when(failedConstraint.verify()).thenReturn(fieldErrors);
        constraints.add(failedConstraint);
        importDeckFormService.verifyFormData(importDeckResponse, constraints);

        assertThat(importDeckResponse.getErrors(), is(fieldErrors));
    }

    @Test
    public void preProcessForm_shouldParseTheDocumentId() throws Exception {
        when(txcMakerParser.parseDocId("doc id string"))
                .thenReturn("doc id");

        importDeckFormService.preProcessForm(importDeckForm);

        assertThat(importDeckForm.getDocId(), is("doc id"));
    }

    @Test
    public void preProcessForm_shouldParseTheAudioDirectoryId() throws Exception {
        when(txcMakerParser.parseAudioDirId("audio dir id string"))
                .thenReturn("audio dir id");

        importDeckFormService.preProcessForm(importDeckForm);

        assertThat(importDeckForm.getAudioDirId(), is("audio dir id"));

    }

    @Test
    public void verifyDeck_shouldAddAnErrorForInvalidIsoCodes() throws Exception {
        deck = new Deck();
        deck.errors.addAll(
                Arrays.asList(
                        new Error("1", true),
                        new Error("4", true),
                        new Error("56", true)
                ));

        importDeckFormService.verifyDeck(deck, importDeckResponse);

        assertThat(importDeckResponse.getErrors().size(), is(1));
        assertThat(importDeckResponse.getErrors().get(0).message, is("The ISO Code on rows 1, 4, 56 are invalid. See www.translation-cards.com/iso-codes for a list of supported codes"));

    }
}