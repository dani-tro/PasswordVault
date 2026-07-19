package bg.sofia.uni.fmi.mjt.client.command;

import bg.sofia.uni.fmi.mjt.client.NetworkManager;
import bg.sofia.uni.fmi.mjt.client.StateManager;
import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.CommandException;
import bg.sofia.uni.fmi.mjt.exception.CommunicationException;
import bg.sofia.uni.fmi.mjt.exception.InvalidNumberOfArgumentsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DisconnectCommandTest {
    private static StateManager mockStateManager;
    private static NetworkManager mockNetworkManager;
    private static DisconnectCommand command;

    @BeforeEach
    void setUp() {
        mockStateManager = mock(StateManager.class);
        mockNetworkManager = mock(NetworkManager.class);
        command = new DisconnectCommand(mockStateManager, mockNetworkManager);
    }

    @Test
    void testExecuteWithValidArgumentsWhenNotLoggedInReturnsServerResponse() {
        ServerResponse expectedResponse = new ServerResponse("Successfully disconnected", true);

        when(mockStateManager.isLoggedIn()).thenReturn(false);
        try {
            when(mockNetworkManager.sendRequestToServer(any())).thenReturn(expectedResponse);
        } catch (CommunicationException e) {
            throw new RuntimeException("CommunicationException occurred while testing execute method of DisconnectCommand with valid arguments", e);
        }

        ServerResponse response;
        String[] arguments = {"disconnect"};
        try {
            response = command.execute(arguments);
        } catch (CommandException | CommunicationException e) {
            throw new RuntimeException("Exception occurred while testing execute method of DisconnectCommand with valid arguments", e);
        }

        try {
            verify(mockNetworkManager, times(1)).sendRequestToServer(any());
        } catch (CommunicationException e) {
            throw new RuntimeException("CommunicationException occurred while testing execute method of DisconnectCommand with valid arguments", e);
        }

        assertEquals(expectedResponse, response);
        verify(mockStateManager, never()).logout();
        verify(mockNetworkManager).freeResources();
    }

    @Test
    void testExecuteWithValidArgumentsWhenLoggedInReturnsServerResponseAndLogsOut() {
        ServerResponse expectedResponse = new ServerResponse("Successfully logged out and disconnected", true);

        when(mockStateManager.isLoggedIn()).thenReturn(true);
        when(mockStateManager.getUsername()).thenReturn(Optional.of("test"));
        try {
            when(mockNetworkManager.sendRequestToServer(any())).thenReturn(expectedResponse);
        } catch (CommunicationException e) {
            throw new RuntimeException("CommunicationException occurred while testing execute method of DisconnectCommand with valid arguments", e);
        }

        ServerResponse response;
        String[] arguments = {"disconnect"};
        try {
            response = command.execute(arguments);
        } catch (CommandException | CommunicationException e) {
            throw new RuntimeException("Exception occurred while testing execute method of DisconnectCommand with valid arguments", e);
        }

        try {
            verify(mockNetworkManager).sendRequestToServer(any());
        } catch (CommunicationException e) {
            throw new RuntimeException("CommunicationException occurred while testing execute method of DisconnectCommand with valid arguments", e);
        }
        assertEquals(expectedResponse, response);
        verify(mockStateManager).logout();
        verify(mockNetworkManager).freeResources();
    }

    @Test
    void testExecuteWithNullArgumentsThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null), "execute with null arguments should throw IllegalArgumentException");
    }

    @Test
    void testExecuteWithInvalidNumberOfArgumentsThrowsInvalidNumberOfArgumentsException() {
        String[] arguments = {"disconnect", "user"};

        assertThrows(InvalidNumberOfArgumentsException.class, () -> command.execute(arguments), "execute with invalid number of arguments should throw InvalidNumberOfArgumentsException");
    }
}
