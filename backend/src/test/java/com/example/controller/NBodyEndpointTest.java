package com.example.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.websocket.Session;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NBodyEndpointTest {
    private NBodyEndpoint endpoint;

    @Mock
    private Session mockSession; // ✅ Mockito injecte une instance

    @BeforeEach
    void setUp() {
        endpoint = new NBodyEndpoint();
        mockSession = mock(Session.class, withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS)); // ✅ Evite le problème de mock
    }

    @Test
    void testOnOpenAddsSession() {
        endpoint.onOpen(mockSession);
        assertTrue(NBodyEndpoint.sessions.contains(mockSession));
    }

    @Test
    void testOnCloseRemovesSession() {
        endpoint.onOpen(mockSession);
        endpoint.onClose(mockSession);
        assertFalse(NBodyEndpoint.sessions.contains(mockSession));
    }

    @Test
    void testOnErrorDoesNotCrash() {
        assertDoesNotThrow(() -> endpoint.onError(mockSession, new Throwable("Test error")));
    }
}
