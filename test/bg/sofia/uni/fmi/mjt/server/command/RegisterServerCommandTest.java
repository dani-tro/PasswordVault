package bg.sofia.uni.fmi.mjt.server.command;


import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.server.vault.Vault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegisterServerCommandTest {
    
    private static ServerCommand command;
    private static Vault mockVault;
    private static ClientRequest mockRequest;

    @BeforeEach
    void setUpAll() {
        mockVault = mock(); 
        mockRequest = mock();
        command = new RegisterServerCommand(mockVault);
    }

    @Test
    void testExecuteWithValidRequestRegistersUser() {
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getPassword()).thenReturn("password");
        try {
            when(mockVault.addUser("test", "password")).thenReturn(true);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException occurred while testing execute method of RegisterServerCommand with valid request", e);
        }

        ServerResponse response;
        try {
            response = command.execute(mockRequest);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing execute method of RegisterServerCommand with valid request", e);
        }

        assertTrue(response.isSuccessful());
        assertEquals("User test successfully registered", response.message(), "Response message should contain the right message for successful registration");
        try {
            verify(mockVault).addUser("test", "password");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException occurred while testing execute method of RegisterServerCommand with valid request", e);
        }
    }

    @Test
    void testExecuteWithExistingUserReturnsErrorMessage() throws NoSuchAlgorithmException {
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getPassword()).thenReturn("password");
        when(mockVault.addUser("test", "password")).thenReturn(false);

        ServerResponse response;
        try {
            response = command.execute(mockRequest);
        } catch (APIException e) {
            throw new RuntimeException("APIException occurred while testing execute method of RegisterServerCommand with existing user", e);
        }

        assertFalse(response.isSuccessful());
        assertEquals("User test already exists", response.message(), "Response message should contain the right message for existing user");
    }

    @Test
    void testExecuteWithNullRequestThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null), "execute with null request should throw IllegalArgumentException");
    }

    @Test
    void testExecuteWithNullUsernameThrowsIllegalArgumentException() {
        when(mockRequest.getUsername()).thenReturn(null);
        when(mockRequest.getPassword()).thenReturn("password");

        assertThrows(IllegalArgumentException.class, () -> command.execute(mockRequest), "execute with null username should throw IllegalArgumentException");
    }

    @Test
    void testExecuteWithNullPasswordThrowsIllegalArgumentException() {
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getPassword()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> command.execute(mockRequest), "execute with null password should throw IllegalArgumentException");
    }
}
