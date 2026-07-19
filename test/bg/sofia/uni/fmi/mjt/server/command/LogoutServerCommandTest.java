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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LogoutServerCommandTest {

    private static String socketAddress;
    private static ServerCommand command;
    private static ClientRequest mockRequest;
    private static Session mockSession;
    private static Map<String, UserVault> userVaults;

    @BeforeEach
    void setUp() {
        socketAddress = "socket";
        mockRequest = mock();
        mockSession = mock();
        UserVault mockUserVault = mock();
        Map<String, Session> sessions = new HashMap<>();
        sessions.put("test", mockSession);
        userVaults = new HashMap<>();
        userVaults.put("test", mockUserVault);
        command = new LogoutServerCommand(sessions, userVaults, socketAddress);
    }

    @Test
    void testExecuteWithValidRequestLogoutsUser() {
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getWebsite()).thenReturn("facebook.com");
        when(mockRequest.getWebsiteUsername()).thenReturn("test@mail.com");

        when(mockSession.isAuthenticated()).thenReturn(true);
        when(mockSession.getSocketAddress()).thenReturn(socketAddress);

        ServerResponse response;
        try {
            response = command.execute(mockRequest);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of LogoutServerCommand with valid request", e);
        }

        assertTrue(response.isSuccessful(), "Response isSuccessful should return true");
        assertEquals("User test successfully logged out", response.message(), "Response message should contain message for successful logout");
        verify(mockSession).logout();
        assertTrue(userVaults.isEmpty(), "UserVault should be empty after logout");
    }

    @Test
    void testExecuteWithUnauthenticatedSessionReturnsErrorMessage() {
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getWebsite()).thenReturn("facebook.com");
        when(mockRequest.getWebsiteUsername()).thenReturn("test@mail.com");

        when(mockSession.isAuthenticated()).thenReturn(false);
        when(mockSession.getSocketAddress()).thenReturn(socketAddress);

        ServerResponse response;
        try {
            response = command.execute(mockRequest);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of LogoutServerCommand with unauthenticated session", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false when session is not authenticated");
        assertEquals("You must be logged in to execute logout command", response.message(), "Response message should contain the right error message when user is not logged in");
    }

    @Test
    void testExecuteWithUserNotInSessionsReturnsErrorMessage() {
        when(mockRequest.getUsername()).thenReturn("user");
        when(mockRequest.getWebsite()).thenReturn("facebook.com");
        when(mockRequest.getWebsiteUsername()).thenReturn("test@mail.com");

        when(mockSession.isAuthenticated()).thenReturn(true);
        when(mockSession.getSocketAddress()).thenReturn(socketAddress);

        ServerResponse response;
        try {
            response = command.execute(mockRequest);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of LogoutServerCommand with user not in sessions", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false when user is not in sessions");
        assertEquals("You must be logged in to execute logout command", response.message(), "Response message should contain the right error message when the user is not in sessions");
    }

    @Test
    void testExecuteWithWrongSocketAddressReturnsErrorMessage() {
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getWebsite()).thenReturn("facebook.com");
        when(mockRequest.getWebsiteUsername()).thenReturn("test@mail.com");

        when(mockSession.isAuthenticated()).thenReturn(true);
        when(mockSession.getSocketAddress()).thenReturn("wrong");

        ServerResponse response;
        try {
            response = command.execute(mockRequest);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of LogoutServerCommand with wrong socket address", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false when socket address is wrong");
        assertEquals("You must be logged in to execute logout command", response.message(), "Response message should contain the right error message when the socket address is wrong");
    }

    @Test
    void testExecuteWithNullRequestThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null), "execute with null request should throw IllegalArgumentException");
    }

    @Test
    void testExecuteWithNullUsernameThrowsIllegalArgumentException() {
        when(mockRequest.getUsername()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> command.execute(mockRequest), "execute with null username should throw IllegalArgumentException");
    }
}
