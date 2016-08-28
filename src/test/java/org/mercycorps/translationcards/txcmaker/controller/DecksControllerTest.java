package org.mercycorps.translationcards.txcmaker.controller;

import com.google.api.services.drive.Drive;
import org.junit.Before;
import org.junit.Test;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.model.deck.Deck;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.Constraint;
import org.mercycorps.translationcards.txcmaker.model.importDeckForm.ImportDeckForm;
import org.mercycorps.translationcards.txcmaker.response.ImportDeckResponse;
import org.mercycorps.translationcards.txcmaker.response.ResponseFactory;
import org.mercycorps.translationcards.txcmaker.service.DriveService;
import org.mercycorps.translationcards.txcmaker.service.ImportDeckService;
import org.mercycorps.translationcards.txcmaker.service.TaskService;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DecksControllerTest {
    public static final String SESSION_ID = "session id";
    @Mock
    private ImportDeckService importDeckService;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private ServletContext servletContext;
    @Mock
    private ImportDeckForm importDeckForm;
    @Mock
    private Constraint constraint;
    @Mock
    private Drive drive;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpServletRequest request;
    @Mock
    private ResponseFactory responseFactory;
    @Mock
    private ImportDeckResponse importDeckResponse;
    @Mock
    private TaskService taskService;
    @Mock
    private DriveService driveService;
    private List<Constraint> constraints;
    private DecksController decksController;
    private Deck deck;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(responseFactory.newImportDeckResponse()).thenReturn(importDeckResponse);

        when(request.getSession().getId()).thenReturn(SESSION_ID);

        when(authUtils.getDriveOrOAuth(servletContext, request, null, false, SESSION_ID))
                .thenReturn(drive);

        constraints = Arrays.asList(constraint);
        when(importDeckForm.getFieldsToVerify(drive)).thenReturn(constraints);

        deck = new Deck();
        when(driveService.assembleDeck(request, SESSION_ID, importDeckForm.getDocId(), drive))
                .thenReturn(deck);

        decksController = new DecksController(importDeckService, authUtils, servletContext, responseFactory, taskService, driveService);
    }

    @Test
    public void shouldPreProcessForm() throws Exception {
        decksController.importDeck(importDeckForm, request);

        verify(importDeckService).preProcessForm(importDeckForm);
    }

    @Test
    public void shouldGetTheDriveAssociatedWithTheSession() throws Exception {
        decksController.importDeck(importDeckForm, request);

        verify(authUtils).getDriveOrOAuth(servletContext, request, null, false, SESSION_ID);
    }

    @Test
    public void shouldVerifyFormData() throws Exception {
        decksController.importDeck(importDeckForm, request);

        verify(importDeckService).verifyFormData(importDeckResponse, constraints);
    }

    @Test
    public void shouldKickOffVerifyDeckTask() throws Exception {
        decksController.importDeck(importDeckForm, request);

        verify(taskService).kickoffVerifyDeckTask(importDeckResponse, SESSION_ID, importDeckForm);
    }

    @Test
    public void shouldBuildAndReturnAResponse() throws Exception {
        ResponseEntity expectedResponse = ResponseEntity.ok().build();
        when(importDeckResponse.build()).thenReturn(expectedResponse);

        ResponseEntity actualResponse = decksController.importDeck(importDeckForm, request);

        assertThat(actualResponse, is(expectedResponse));
    }

    @Test
    public void shouldKickOffBuildDeckTask() throws Exception {
        decksController.assembleDeck(SESSION_ID);

        verify(taskService).kickoffBuildDeckTask(SESSION_ID);
    }

    @Test
    public void shouldAssembleDeckWhenImporting() throws Exception {
        decksController.importDeck(importDeckForm, request);

        verify(driveService).assembleDeck(request, SESSION_ID, importDeckForm.getDocId(), drive);

    }

    @Test
    public void shouldVerifyDeckWhenImporting() throws Exception {
        decksController.importDeck(importDeckForm, request);

        verify(importDeckService).verifyDeck(deck, importDeckResponse);
    }
}