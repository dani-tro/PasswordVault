package bg.sofia.uni.fmi.mjt.client.command;

import bg.sofia.uni.fmi.mjt.client.NetworkManager;
import bg.sofia.uni.fmi.mjt.client.StateManager;
import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.AlreadyLoggedInException;
import bg.sofia.uni.fmi.mjt.exception.CommandException;
import bg.sofia.uni.fmi.mjt.exception.CommunicationException;
import bg.sofia.uni.fmi.mjt.exception.InvalidNumberOfArgumentsException;
import bg.sofia.uni.fmi.mjt.exception.PasswordsDoNotMatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegisterCommandTest {
    private static RegisterCommand command;
    private static NetworkManager mockNetworkManager;
    private static StateManager mockStateManager;

    @BeforeEach
    void setUp() {
        mockNetworkManager = mock();
        mockStateManager = mock();
        command = new RegisterCommand(mockStateManager, mockNetworkManager);
    }

    @Test
    void testExecuteWithValidArgumentsReturnsSuccessfulResponse() {
        String[] arguments = {"register", "test", "password", "password"};

        ServerResponse expectedResponse = new ServerResponse("User registered successfully", true);

        when(mockStateManager.isLoggedIn()).thenReturn(false);
        try {
            when(mockNetworkManager.sendRequestToServer(any())).thenReturn(expectedResponse);
        } catch (CommunicationException e) {
            throw new RuntimeException("CommunicationException occurred while testing execute method of RegisterCommand with valid arguments", e);
        }

        ServerResponse response;
        try {
            response = command.execute(arguments);
        } catch (CommandException | CommunicationException e) {
            throw new RuntimeException("Exception occurred while testing execute method of RegisterCommand with valid arguments", e);
        }

        assertEquals(expectedResponse, response, "Response should match the expected response");
    }

    @Test
    void testExecuteWithNullArgumentsThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null), "execute with null arguments should throw IllegalArgumentException");
    }

    @Test
    void testExecuteWithInvalidNumberOfArgumentsThrowsInvalidNumberOfArgumentsException() {
        String[] arguments = {"register", "test", "password"};
        assertThrows(InvalidNumberOfArgumentsException.class, () -> command.execute(arguments), "execute with invalid number of arguments should throw InvalidNumberOfArgumentsException");
    }

    @Test
    void testExecuteWhenAlreadyLoggedInThrowsAlreadyLoggedInException() {
        String[] arguments = {"register", "test", "password", "password"};
        when(mockStateManager.isLoggedIn()).thenReturn(true);

        assertThrows(AlreadyLoggedInException.class, () -> command.execute(arguments), "execute when already logged in should throw AlreadyLoggedInException");
    }

    @Test
    void testExecuteWithNonMatchingPasswordsThrowsPasswordsDoNotMatchException() {
        String[] arguments = {"register", "test", "password", "password1"};
        when(mockStateManager.isLoggedIn()).thenReturn(false);

        assertThrows(PasswordsDoNotMatchException.class, () -> command.execute(arguments), "execute with non-matching passwords should throw PasswordsDoNotMatchException");
    }
}

