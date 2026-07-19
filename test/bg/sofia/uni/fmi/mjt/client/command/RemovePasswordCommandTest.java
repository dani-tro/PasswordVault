package bg.sofia.uni.fmi.mjt.client.command;

import bg.sofia.uni.fmi.mjt.client.NetworkManager;
import bg.sofia.uni.fmi.mjt.client.StateManager;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.CommandException;
import bg.sofia.uni.fmi.mjt.exception.CommunicationException;
import bg.sofia.uni.fmi.mjt.exception.InvalidNumberOfArgumentsException;
import bg.sofia.uni.fmi.mjt.exception.NotLoggedInException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RemovePasswordCommandTest {
    private static StateManager mockStateManager;
    private static NetworkManager mockNetworkManager;
    private static RemovePasswordCommand command;

    @BeforeEach
    void setUp() {
        mockStateManager = mock(StateManager.class);
        mockNetworkManager = mock(NetworkManager.class);
        command = new RemovePasswordCommand(mockStateManager, mockNetworkManager);
    }

    @Test
    void testExecuteWithValidArgumentsReturnsSuccessfulResponse() {
        String[] arguments = {"remove-password", "facebook.com", "test@mail.com"};
        when(mockStateManager.isLoggedIn()).thenReturn(true);
        when(mockStateManager.getUsername()).thenReturn(Optional.of("test"));
        ServerResponse expectedResponse = new ServerResponse("Password removed successfully", true);
        try {
            when(mockNetworkManager.sendRequestToServer(any())).thenReturn(expectedResponse);
        } catch (CommunicationException e) {
            throw new RuntimeException("CommunicationException occurred while testing execute method of RemovePasswordCommand with valid arguments", e);
        }

        ServerResponse response;
        try {
            response = command.execute(arguments);
        } catch (CommandException | CommunicationException e) {
            throw new RuntimeException("Exception occurred while testing execute method of RemovePasswordCommand with valid arguments", e);
        }

        assertEquals(expectedResponse, response, "Response should match the expected response");
    }

    @Test
    void testExecuteWithNullArgumentsThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null), "execute with null arguments should throw IllegalArgumentException");
    }

    @Test
    void testExecuteWithInvalidNumberOfArgumentsThrowsInvalidNumberOfArgumentsException() {
        String[] arguments = {"remove-password", "facebook.com"};
        assertThrows(InvalidNumberOfArgumentsException.class, () -> command.execute(arguments), "execute with invalid number of arguments should throw InvalidNumberOfArgumentsException");
    }

    @Test
    void testExecuteWhenNotLoggedInThrowsNotLoggedInException() {
        String[] arguments = {"remove-password", "facebook.com", "test@mail.com"};
        when(mockStateManager.isLoggedIn()).thenReturn(false);

        assertThrows(NotLoggedInException.class, () -> command.execute(arguments), "execute when not logged in should throw NotLoggedInException");
    }

}
