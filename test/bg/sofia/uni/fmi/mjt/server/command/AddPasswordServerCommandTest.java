package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.server.PasswordAuthenticator;
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
import static org.mockito.Mockito.when;

public class AddPasswordServerCommandTest {
    private static Map<String, Session> sessions;
    private static Map<String, UserVault> userVaults;
    private static PasswordAuthenticator passwordAuthenticator;
    private static String socketAddress;
    private static AddPasswordServerCommand command;
    private static ClientRequest request;
    private static Session session;
    private static UserVault userVault;

    @BeforeEach
    void setUp() {
        sessions = new HashMap<>();
        userVaults = new HashMap<>();
        passwordAuthenticator = mock();
        socketAddress = "socket";
        command = new AddPasswordServerCommand(sessions, userVaults, passwordAuthenticator, socketAddress);
        request = mock();
        when(request.getUsername()).thenReturn("test");
        when(request.getWebsite()).thenReturn("facebook.com");
        when(request.getWebsiteUsername()).thenReturn("test@mail.com");
        when(request.getWebsitePassword()).thenReturn("password");
        session = mock();
        userVault = mock();
    }

    @Test
    void testExecuteSuccessfullyAddsPassword() {
        when(session.isAuthenticated()).thenReturn(true);
        when(session.getSocketAddress()).thenReturn(socketAddress);
        try {
            when(passwordAuthenticator.isPasswordStrong("password")).thenReturn(true);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of AddPasswordServerCommand with valid request", e);
        }
        try {
            when(userVault.addPassword("facebook.com", "test@mail.com", "password")).thenReturn(true);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of AddPasswordServerCommand with valid request", e);
        }

        sessions.put("test", session);
        userVaults.put("test", userVault);

        ServerResponse response;
        try {
            response = command.execute(request);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of AddPasswordServerCommand with valid request", e);
        }

        assertTrue(response.isSuccessful(), "Response isSuccessful should return true when password is added");
        assertEquals("Password for test@mail.com on facebook.com successfully added", response.message(),
            "Response message should contain success message for added password");
    }

    @Test
    void testExecuteFailsWhenUserNotLoggedIn() {

        when(session.isAuthenticated()).thenReturn(false);
        when(session.getSocketAddress()).thenReturn(socketAddress);
        ServerResponse response;
        try {
            response = command.execute(request);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of AddPasswordServerCommand with not logged in user", e);
        }

        assertFalse(response.isSuccessful(),
            "Response isSuccessful should return false when user is not authenticated");
        assertEquals("You must be logged in to execute add-password command", response.message(),
            "Response message should contain error message for not logged in user");
    }

    @Test
    void testExecuteFailsWhenUserNotInSessions() {

        when(session.isAuthenticated()).thenReturn(true);
        when(session.getSocketAddress()).thenReturn(socketAddress);
        sessions.put("user", session);

        ServerResponse response;
        try {
            response = command.execute(request);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of AddPasswordServerCommand with user not in sessions", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false when user is not in sessions");
        assertEquals("You must be logged in to execute add-password command", response.message(),
            "Response message should contain error message for not logged in user");
    }

    @Test
    void testExecuteFailsWhenWrongSocketAddress() {
        when(session.isAuthenticated()).thenReturn(true);
        when(session.getSocketAddress()).thenReturn("wrong");
        sessions.put("user", session);

        ServerResponse response;
        try {
            response = command.execute(request);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of AddPasswordServerCommand with wrong socket address", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false when socket address is wrong");
        assertEquals("You must be logged in to execute add-password command", response.message(),
            "Response message should contain error message for not logged in user");
    }

    @Test
    void testExecuteFailsWhenPasswordIsNotStrong() {
        Session session = mock();
        UserVault userVault = mock();

        when(session.isAuthenticated()).thenReturn(true);
        when(session.getSocketAddress()).thenReturn(socketAddress);

        try {
            when(passwordAuthenticator.isPasswordStrong("password")).thenReturn(false);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of AddPasswordServerCommand with weak password", e);
        }

        sessions.put("test", session);
        userVaults.put("test", userVault);

        ServerResponse response;
        try {
            response = command.execute(request);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of AddPasswordServerCommand with weaak password", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false when password is not strong");
        assertEquals("Password is not strong enough", response.message(),
            "Response message should contain error message for not strong enough password");
    }

    @Test
    void testExecuteFailsWhenPasswordAlreadyExists() {
        Session session = mock();
        UserVault userVault = mock();

        when(session.isAuthenticated()).thenReturn(true);
        when(session.getSocketAddress()).thenReturn(socketAddress);
        try {
            when(passwordAuthenticator.isPasswordStrong("password")).thenReturn(true);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of AddPasswordServerCommand with already existing password", e);
        }

        when(userVault.retrieveCredentials("facebook.com", "test@mail.com")).thenReturn(
            java.util.Optional.of("existingPassword"));

        sessions.put("test", session);
        userVaults.put("test", userVault);

        ServerResponse response;
        try {
            response = command.execute(request);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of AddPasswordServerCommand with already existing password", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false when password already exists");
        assertEquals("Password for test@mail.com on facebook.com already exists", response.message(), "Response message should contain error message for already existing password");
        }

    @Test
    void testExecuteFailsWhenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null),
            "execute with null request should throw IllegalArgumentException");
    }

    @Test
    void testExecuteFailsWhenUsernameIsNull() {
        when(request.getUsername()).thenReturn(null);
        when(request.getWebsite()).thenReturn("facebook.com");
        when(request.getWebsiteUsername()).thenReturn("test@mail.com");
        when(request.getWebsitePassword()).thenReturn("password");

        assertThrows(IllegalArgumentException.class, () -> command.execute(request),
            "execute with null username should throw IllegalArgumentException");
    }

    @Test
    void testExecuteFailsWhenWebsiteIsNull() {
        when(request.getUsername()).thenReturn("test");
        when(request.getWebsite()).thenReturn(null);
        when(request.getWebsiteUsername()).thenReturn("test@mail.com");
        when(request.getWebsitePassword()).thenReturn("password");

        assertThrows(IllegalArgumentException.class, () -> command.execute(request),
            "execute with null website should throw IllegalArgumentException");
    }

    @Test
    void testExecuteFailsWhenWebsiteUsernameIsNull() {
        when(request.getUsername()).thenReturn("test");
        when(request.getWebsite()).thenReturn("facebook.com");
        when(request.getWebsiteUsername()).thenReturn(null);
        when(request.getWebsitePassword()).thenReturn("password");

        assertThrows(IllegalArgumentException.class, () -> command.execute(request),
            "execute with null website username should throw IllegalArgumentException");
    }

    @Test
    void testExecuteFailsWhenWebsitePasswordIsNull() {
        when(request.getUsername()).thenReturn("test");
        when(request.getWebsite()).thenReturn("facebook.com");
        when(request.getWebsiteUsername()).thenReturn("test@mail.com");
        when(request.getWebsitePassword()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> command.execute(request),
            "execute with null website password should throw IllegalArgumentException");
    }
}
