package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.server.Session;
import bg.sofia.uni.fmi.mjt.server.vault.UserVault;
import bg.sofia.uni.fmi.mjt.server.vault.Vault;
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
import static org.mockito.Mockito.when;

public class LoginServerCommandTest {

    private static ServerCommand command;
    private static ClientRequest mockRequest;
    private static Session mockSession;
    private static Vault mockVault;
    private static Map<String, Session> sessions;
    private static Map<String, UserVault> userVaults;

    @BeforeEach
    void setUpAll() {
        String socketAddress = "socket";
        mockRequest = mock();
        mockSession = mock();
        mockVault = mock();
        sessions = new HashMap<>();
        userVaults = new HashMap<>();
        command = new LoginServerCommand(sessions, userVaults, socketAddress, mockVault);
    }

    @Test
    void testExecuteWithValidCredentialsLogsInUser() {
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getPassword()).thenReturn("password");
        try {
            when(mockVault.authenticate("test", "password")).thenReturn(true);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException occurred while testing execute method of LoginServerCommand with valid credentials", e);
        }

        ServerResponse response;
        try {
            response = command.execute(mockRequest);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of LoginServerCommand with valid credentials", e);
        }

        assertTrue(response.isSuccessful(), "Response isSuccessful should return true");
        assertEquals("User test successfully logged in", response.message(),
            "Response message should contain message for successful login");
        assertTrue(sessions.containsKey("test"), "session should be added to sessions");
        assertTrue(userVaults.containsKey("test"), "userVault should be added to userVaults");
    }

    @Test
    void testExecuteWithAlreadyLoggedInUserReturnsErrorMessage() {
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getPassword()).thenReturn("password");
        sessions.put("test", mockSession);

        try {
            when(mockVault.authenticate("test", "password")).thenReturn(true);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException occurred while testing execute method of LoginServerCommand with already logged in user", e);
        }

        when(mockSession.isAuthenticated()).thenReturn(true);

        ServerResponse response;
        try {
            response = command.execute(mockRequest);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of LoginServerCommand with already logged in user", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false");
        assertEquals("User test is already logged in", response.message(), "Response message should contain error message for already logged in user");
    }

    @Test
    void testExecuteWithInvalidCredentialsReturnsErrorMessage() {
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getPassword()).thenReturn("password");
        try {
            when(mockVault.authenticate("test", "password")).thenReturn(false);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException occurred while testing execute method of LoginServerCommand with invalid credentials", e);
        }

        ServerResponse response;
        try {
            response = command.execute(mockRequest);
        } catch (NoSuchAlgorithmException | APIException e) {
            throw new RuntimeException("Exception occurred while testing execute method of LoginServerCommand with invalid credentials", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false");
        assertEquals("Invalid username or password", response.message(), "Response message should contain error message for invalid credentials");
    }

    @Test
    void testExecuteWithNullRequestThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null), "execute should throw IllegalArgumentException when request is null");
    }

    @Test
    void testExecuteWithNullUsernameThrowsIllegalArgumentException() {
        when(mockRequest.getUsername()).thenReturn(null);
        when(mockRequest.getPassword()).thenReturn("password");
        assertThrows(IllegalArgumentException.class, () -> command.execute(mockRequest), "execute should throw IllegalArgumentException when username is null");
    }

    @Test
    void testExecuteWithNullPasswordThrowsIllegalArgumentException() {
        when(mockRequest.getUsername()).thenReturn("user");
        when(mockRequest.getPassword()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> command.execute(mockRequest), "execute should throw IllegalArgumentException when password is null");
    }
}
