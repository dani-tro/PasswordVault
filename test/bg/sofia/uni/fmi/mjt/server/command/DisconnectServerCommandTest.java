package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.server.Session;
import bg.sofia.uni.fmi.mjt.server.vault.UserVault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DisconnectServerCommandTest {
    private static Map<String, Session> sessions;
    private static DisconnectServerCommand command;

    @BeforeEach
    void setUp() {
        sessions = new HashMap<>();
        Map<String, UserVault> userVaults = new HashMap<>();
        String socketAddress = "socket";
        command = new DisconnectServerCommand(sessions, userVaults, socketAddress);
    }

    @Test
    void testExecuteReturnsSuccessfulResponseWhenThereAreNoSessions() {
        ClientRequest request = mock();
        when(request.getUsername()).thenReturn("test");

        ServerResponse response;
        try {
            response = command.execute(request);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of DisconnectServerCommand with no sessions", e);
        }

        assertTrue(response.isSuccessful(), "response isSuccessful should return true when there are no sessions");
        assertEquals("Successfully disconnected", response.message(), "execute should return a successful response when there are no sessions");
    }

    @Test
    void testExecuteReturnsSuccessfulResponseWhenSessionIsNotAuthenticated() {
        ClientRequest request = mock();
        when(request.getUsername()).thenReturn("test");

        Session mockSession = mock();
        when(mockSession.isAuthenticated()).thenReturn(false);
        sessions.put("test", mockSession);

        ServerResponse response;
        try {
            response = command.execute(request);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of DisconnectServerCommand with not authenticated session", e);
        }

        assertTrue(response.isSuccessful(), "response isSuccessful should return true when session is not authenticated");
        assertEquals("Successfully disconnected", response.message(), "execute should return a successful response when session is not authenticated");
    }

    @Test
    void testExecuteReturnsSuccessfulResponseWhenSocketAddressDoesNotMatch() {
        ClientRequest request = mock();
        when(request.getUsername()).thenReturn("test");

        Session session = mock(Session.class);
        when(session.isAuthenticated()).thenReturn(true);
        when(session.getSocketAddress()).thenReturn("wrong");
        sessions.put("test", session);

        ServerResponse response;
        try {
            response = command.execute(request);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of DisconnectServerCommand with wrong socketAddress", e);
        }

        assertTrue(response.isSuccessful(), "response isSuccessful should return true when socketAddress does not match");
        assertEquals("Successfully disconnected", response.message(), "execute should return a successful response when socketAddress does not match");
    }

    @Test
    void testExecuteThrowsIllegalArgumentExceptionWhenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null), "execute should throw IllegalArgumentException when request is null");
    }
}
