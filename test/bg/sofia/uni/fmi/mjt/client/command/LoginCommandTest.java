package bg.sofia.uni.fmi.mjt.client.command;

import bg.sofia.uni.fmi.mjt.client.NetworkManager;
import bg.sofia.uni.fmi.mjt.client.StateManager;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.AlreadyLoggedInException;
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

public class LoginCommandTest {
    private static StateManager stateManager;
    private static NetworkManager networkManager;
    private static LoginCommand loginCommand;

    @BeforeEach
    void setUp() {
        stateManager = mock();
        networkManager = mock();
        loginCommand = new LoginCommand(stateManager, networkManager);
    }

    @Test
    void testExecuteReturnsSuccessfulResponseWhenLoginIsSuccessful() {
        when(stateManager.isLoggedIn()).thenReturn(false);
        when(stateManager.getUsername()).thenReturn(Optional.empty());

        ServerResponse expectedResponse = new ServerResponse("Login successful", true);
        try {
            when(networkManager.sendRequestToServer(any())).thenReturn(expectedResponse);
        } catch (CommunicationException e) {
            throw new RuntimeException("CommunicationException occurred while testing execute method of LoginCommand with successful login", e);
        }

        String[] arguments = {"login", "user", "password"};
        ServerResponse actualResponse;
        try {
            actualResponse = loginCommand.execute(arguments);
        } catch (CommandException | CommunicationException e) {
            throw new RuntimeException("Exception occurred while testing execute method of LoginCommand with successful login", e);
        }

        assertEquals(expectedResponse, actualResponse, "Response should match the expected response when login is successful");
        verify(stateManager).login("user");
    }

    @Test
    void testExecuteReturnsUnsuccessfulResponseWhenLoginFails() {
        when(stateManager.isLoggedIn()).thenReturn(false);
        when(stateManager.getUsername()).thenReturn(Optional.empty());

        ServerResponse response = new ServerResponse( "Login failed", false);
        try {
            when(networkManager.sendRequestToServer(any())).thenReturn(response);
        } catch (CommunicationException e) {
            throw new RuntimeException("CommunicationException occurred while testing execute method of LoginCommand with unsuccessful login", e);
        }

        String[] arguments = {"login", "user", "password"};
        ServerResponse actualResponse;
        try {
            actualResponse = loginCommand.execute(arguments);
        } catch (CommandException | CommunicationException e) {
            throw new RuntimeException("Exception occurred while testing execute method of LoginCommand with unsuccessful login", e);
        }

        assertEquals(response, actualResponse, "Response should match the expected response when login fails");
        verify(stateManager, never()).login("user");
    }

    @Test
    void testExecuteWithNullArgumentsThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> loginCommand.execute(null), "execute with null arguments should throw IllegalArgumentException");
    }

    @Test
    void testExecuteWithInvalidNumberOfArgumentsThrowsInvalidNumberOfArgumentsException() {
        String[] arguments = {"login", "user"};
        assertThrows(InvalidNumberOfArgumentsException.class, () -> loginCommand.execute(arguments), "execute with invalid number of arguments should throw InvalidNumberOfArgumentsException");
    }

    @Test
    void testExecuteWhenLoggedInThrowsAlreadyLoggedInException() {
        when(stateManager.isLoggedIn()).thenReturn(true);
        String[] arguments = {"login", "user", "password"};
        assertThrows(AlreadyLoggedInException.class, () -> loginCommand.execute(arguments), "execute when already logged in should throw AlreadyLoggedInException");
    }
}
