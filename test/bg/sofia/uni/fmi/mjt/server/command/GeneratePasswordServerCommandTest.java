package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.server.PasswordCreator;
import bg.sofia.uni.fmi.mjt.server.Session;
import bg.sofia.uni.fmi.mjt.server.vault.UserVault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GeneratePasswordServerCommandTest {
    private static Map<String, Session> sessions;
    private static Map<String, UserVault> userVaults;
    private static PasswordCreator passwordCreator;
    private static String socketAddress;
    private static GeneratePasswordServerCommand command;
    private static ClientRequest request;

    @BeforeEach
    void setUp() {
        sessions = new HashMap<>();
        userVaults = new HashMap<>();
        passwordCreator = mock();
        request = mock(ClientRequest.class);
        socketAddress = "socket";
        command = new GeneratePasswordServerCommand(sessions, userVaults, passwordCreator, socketAddress);
    }

    @Test
    void testExecuteReturnsSuccessResponseWhenPasswordIsGenerated() throws APIException, NoSuchAlgorithmException {
        ClientRequest request = mock();
        when(request.getUsername()).thenReturn("test");
        when(request.getWebsite()).thenReturn("facebook.com");
        when(request.getWebsiteUsername()).thenReturn("test@mail.com");

        Session session = mock(Session.class);
        when(session.isAuthenticated()).thenReturn(true);
        when(session.getSocketAddress()).thenReturn(socketAddress);
        sessions.put("test", session);

        UserVault userVault = mock(UserVault.class);
        when(userVault.retrieveCredentials("facebook.com", "test@mail.com")).thenReturn(Optional.empty());
        userVaults.put("test", userVault);

        when(passwordCreator.generatePassword()).thenReturn("generatedPassword");

        ServerResponse response = command.execute(request);

        assertTrue(response.isSuccessful(), "Response isSuccessful should return true when password is generated");
        assertEquals("Password for test@mail.com on facebook.com successfully generated" + System.lineSeparator() +
            "Generated password: generatedPassword", response.message(), "Response message should contain success message for generated password");
        verify(userVault).addPassword("facebook.com", "test@mail.com", "generatedPassword");
    }

    @Test
    void testExecuteReturnsErrorResponseWhenSessionIsNull() {
        ClientRequest request = mock();
        when(request.getUsername()).thenReturn("test");
        when(request.getWebsite()).thenReturn("facebook.com");
        when(request.getWebsiteUsername()).thenReturn("test@mail.com");

        ServerResponse response;
        try {
            response = command.execute(request);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of GeneratePasswordServerCommand with null session", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false when there are no sessions");
        assertEquals("You must be logged in to execute generate-password command", response.message(), "Response message should contain error message for not logged in user");
    }

    @Test
    void testExecuteReturnsErrorResponseWhenSessionIsNotAuthenticated() {
        ClientRequest request = mock();
        when(request.getUsername()).thenReturn("test");
        when(request.getWebsite()).thenReturn("facebook.com");
        when(request.getWebsiteUsername()).thenReturn("test@mail.com");

        Session session = mock();
        when(session.isAuthenticated()).thenReturn(false);
        sessions.put("test", session);

        ServerResponse response;
        try {
            response = command.execute(request);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of GeneratePasswordServerCommand with unauthenticated session", e);
        }

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false when session is not authenticated");
        assertEquals("You must be logged in to execute generate-password command", response.message(), "Response message should contain error message for not logged in user");
    }

    @Test
    void testExecuteReturnsErrorResponseWhenPasswordAlreadyExists() throws APIException, NoSuchAlgorithmException {
        ClientRequest request = mock();
        when(request.getUsername()).thenReturn("test");
        when(request.getWebsite()).thenReturn("facebook.com");
        when(request.getWebsiteUsername()).thenReturn("test@mail.com");

        Session session = mock();
        when(session.isAuthenticated()).thenReturn(true);
        when(session.getSocketAddress()).thenReturn(socketAddress);
        sessions.put("test", session);

        UserVault userVault = mock();
        when(userVault.retrieveCredentials("facebook.com", "test@mail.com")).thenReturn(Optional.of("existingPassword"));
        userVaults.put("test", userVault);

        ServerResponse response = command.execute(request);

        assertFalse(response.isSuccessful(), "Response isSuccessful should return false when password already exists");
        assertEquals("Password for test@mail.com on facebook.com already exists", response.message(), "Response message should contain error message for already existing password");
    }


    @Test
    void testExecuteWithNullRequestThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null), "execute with null request should throw IllegalArgumentException");
    }

    @Test
    void testExecuteWithNullUsernameThrowsIllegalArgumentException() {
        when(request.getUsername()).thenReturn(null);
        when(request.getWebsite()).thenReturn("website");
        when(request.getWebsiteUsername()).thenReturn("websitetest");

        assertThrows(IllegalArgumentException.class, () -> command.execute(request), "execute with null username should throw IllegalArgumentException");
    }

    @Test
    void testExecuteWithNullWebsiteThrowsIllegalArgumentException() {
        when(request.getUsername()).thenReturn("test");
        when(request.getWebsite()).thenReturn(null);
        when(request.getWebsiteUsername()).thenReturn("websitetest");

        assertThrows(IllegalArgumentException.class, () -> command.execute(request), "execute with null website should throw IllegalArgumentException");
    }

    @Test
    void testExecuteWithNullWebsiteUsernameThrowsIllegalArgumentException() {
        when(request.getUsername()).thenReturn("test");
        when(request.getWebsite()).thenReturn("website");
        when(request.getWebsiteUsername()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> command.execute(request), "execute with null website username should throw IllegalArgumentException");
    }
}
