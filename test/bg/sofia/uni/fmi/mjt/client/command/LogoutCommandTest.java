package bg.sofia.uni.fmi.mjt.client.command;

import bg.sofia.uni.fmi.mjt.client.NetworkManager;
import bg.sofia.uni.fmi.mjt.client.StateManager;
import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LogoutCommandTest {
    private static StateManager stateManager;
    private static NetworkManager networkManager;
    private static LogoutCommand logoutCommand;

    @BeforeEach
    void setUp() {
        stateManager = mock();
        networkManager = mock();
        logoutCommand = new LogoutCommand(stateManager, networkManager);
    }

    @Test
    void testExecuteReturnsSuccessfulResponseWhenLogoutIsSuccessful() {
        when(stateManager.isLoggedIn()).thenReturn(true);
        when(stateManager.getUsername()).thenReturn(Optional.of("user"));

        ServerResponse expectedResponse = new ServerResponse("Logout successful", true);
        try {
            when(networkManager.sendRequestToServer(any())).thenReturn(expectedResponse);
        } catch (CommunicationException e) {
            throw new RuntimeException("CommunicationException occurred while testing execute method of LogoutCommand with successful logout", e);
        }

        String[] arguments = {"logout"};
        ServerResponse actualResponse;
        try {
            actualResponse = logoutCommand.execute(arguments);
        } catch (CommandException | CommunicationException e) {
            throw new RuntimeException("Exception occurred while testing execute method of LogoutCommand with successful logout", e);
        }

        assertEquals(expectedResponse, actualResponse, "Response should match the expected response when logout is successful");
        verify(stateManager).logout();
    }

    @Test
    void testExecuteReturnsUnsuccessfulResponseWhenLogoutFails() {
        when(stateManager.isLoggedIn()).thenReturn(true);
        when(stateManager.getUsername()).thenReturn(Optional.of("user"));

        ServerResponse response = new ServerResponse( "Logout failed", false);
        try {
            when(networkManager.sendRequestToServer(any())).thenReturn(response);
        } catch (CommunicationException e) {
            throw new RuntimeException("CommunicationException occurred while testing execute method of LogoutCommand with unsuccessful logout", e);
        }

        String[] arguments = {"logout"};
        ServerResponse actualResponse;
        try {
            actualResponse = logoutCommand.execute(arguments);
        } catch (CommandException | CommunicationException e) {
            throw new RuntimeException("Exception occurred while testing execute method of LogoutCommand with unsuccessful logout", e);
        }

        assertEquals(response, actualResponse, "Response should match the expected response when logout fails");
        verify(stateManager, never()).logout();
    }

    @Test
    void testExecuteWithNullArgumentsThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> logoutCommand.execute(null), "execute with null arguments should throw IllegalArgumentException");
    }

    @Test
    void testExecuteWithInvalidNumberOfArgumentsThrowsInvalidNumberOfArgumentsException() {
        String[] arguments = {"logout", "user", "password"};
        assertThrows(InvalidNumberOfArgumentsException.class, () -> logoutCommand.execute(arguments), "execute with invalid number of arguments should throw InvalidNumberOfArgumentsException");
    }

    @Test
    void testExecuteWhenNotLoggedInThrowsNotLoggedInException() {
        when(stateManager.isLoggedIn()).thenReturn(false);
        String[] arguments = {"logout"};
        assertThrows(NotLoggedInException.class, () -> logoutCommand.execute(arguments), "execute when not logged in should throw NotLoggedInException");
    }
}
