package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.server.Session;
import bg.sofia.uni.fmi.mjt.server.vault.UserVault;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RemovePasswordServerCommandTest {

    private static String socketAddress;
    private static ServerCommand command;
    private static ClientRequest mockRequest;
    private static Session mockSession;
    private static UserVault mockUserVault;

    @BeforeAll
    static void setUpAll() {
        socketAddress = "socket";
        mockRequest = mock();
        mockSession = mock();
        mockUserVault = mock();
        Map<String, Session> sessions = Map.of("test", mockSession);
        Map<String, UserVault> userVaults = Map.of("test", mockUserVault);
        command = new RemovePasswordServerCommand(sessions, userVaults, socketAddress);
    }

    @Test
    void testExecuteWithValidRequestRemovesPassword() {
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getWebsite()).thenReturn("facebook.com");
        when(mockRequest.getWebsiteUsername()).thenReturn("test@mail.com");

        when(mockSession.isAuthenticated()).thenReturn(true);
        when(mockSession.getSocketAddress()).thenReturn(socketAddress);

        when(mockUserVault.retrieveCredentials("facebook.com", "test@mail.com")).thenReturn(
            Optional.of("password"));

        ServerResponse response;
        try {
            response = command.execute(mockRequest);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of RemovePasswordServerCommand with valid request", e);
        }

        assertTrue(response.isSuccessful(), "Response isSuccessful should return true");
        assertEquals("Password for test@mail.com on facebook.com successfully removed", response.message(), "Response message should contain message for successful password removal");
        verify(mockUserVault).removePassword("facebook.com", "test@mail.com");
    }

    @Test
    void testExecuteWithNoPasswordFoundReturnsErrorMessage() {
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getWebsite()).thenReturn("facebook.com");
        when(mockRequest.getWebsiteUsername()).thenReturn("test@mail.com");
        when(mockSession.isAuthenticated()).thenReturn(true);
        when(mockSession.getSocketAddress()).thenReturn(socketAddress);
        when(mockUserVault.retrieveCredentials("facebook.com", "test@mail.com")).thenReturn(Optional.empty());

        ServerResponse response;
        try {
            response = command.execute(mockRequest);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of RemovePasswordServerCommand with no password found", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false");
        assertEquals("Password for test@mail.com on facebook.com does not exist", response.message(), "Response message should contain message for non-existing password");
    }

    @Test
    void testExecuteWithUnauthenticatedSessionReturnsErrorMessage() {
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getWebsite()).thenReturn("facebook.com");
        when(mockRequest.getWebsiteUsername()).thenReturn("test@mail.com");
        when(mockSession.isAuthenticated()).thenReturn(false);

        ServerResponse response;
        try {
            response = command.execute(mockRequest);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of RemovePasswordServerCommand with unauthenticated session", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false when session is not authenticated");
        assertEquals("You must be logged in to execute remove-password command", response.message(), "Response message should contain message for non logged in");
    }

    @Test
    void testExecuteWithUserNotInSessionsReturnsErrorMessage() {
        when(mockRequest.getUsername()).thenReturn("user");
        when(mockRequest.getWebsite()).thenReturn("facebook.com");
        when(mockRequest.getWebsiteUsername()).thenReturn("test@mail.com");
        when(mockSession.isAuthenticated()).thenReturn(true);

        ServerResponse response;
        try {
            response = command.execute(mockRequest);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of RemovePasswordServerCommand with user not in sessions", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false");
        assertEquals("You must be logged in to execute remove-password command", response.message(),
            "Response message should contain the right error message when the user is not in sessions");
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
            throw new RuntimeException("Exception occurred while testing execute method of RemovePasswordServerCommand with wrong socket address", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false");
        assertEquals("You must be logged in to execute remove-password command", response.message(),
            "Response message should contain the right error message when the socket address is wrong");
    }

    @Test
    void testExecuteWithNullRequestThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null), "execute with null request should throw IllegalArgumentException");
    }

    @Test
    void testExecuteWithNullUsernameThrowsIllegalArgumentException() {
        when(mockRequest.getUsername()).thenReturn(null);
        when(mockRequest.getWebsite()).thenReturn("facebook.com");
        when(mockRequest.getWebsiteUsername()).thenReturn("test@mail.com");

        assertThrows(IllegalArgumentException.class, () -> command.execute(mockRequest), "execute with null username should throw IllegalArgumentException");
    }

    @Test
    void testExecuteWithNullWebsiteThrowsIllegalArgumentException() {
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getWebsite()).thenReturn(null);
        when(mockRequest.getWebsiteUsername()).thenReturn("test@mail.com");

        assertThrows(IllegalArgumentException.class, () -> command.execute(mockRequest), "execute with null website should throw IllegalArgumentException");
    }

    @Test
    void testExecuteWithNullWebsiteUsernameThrowsIllegalArgumentException() {
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getWebsite()).thenReturn("facebook.com");
        when(mockRequest.getWebsiteUsername()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> command.execute(mockRequest), "execute with null website username should throw IllegalArgumentException");
    }
}
