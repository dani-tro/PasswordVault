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

public class RetrieveCredentialsCommandTest {
    private static StateManager mockStateManager;
    private static NetworkManager mockNetworkManager;
    private static RetrieveCredentialsCommand command;

    @BeforeEach
    void setUp() {
        mockStateManager = mock();
        mockNetworkManager = mock();
        command = new RetrieveCredentialsCommand(mockStateManager, mockNetworkManager);
    }

    @Test
    void testExecuteWithValidArgumentsReturnsServerResponse() {

        String[] arguments = {"retrieve-credentials", "facebook.com", "test@mail.com"};
        when(mockStateManager.isLoggedIn()).thenReturn(true);
        when(mockStateManager.getUsername()).thenReturn(Optional.of("test"));
        ServerResponse expectedResponse = new ServerResponse("Credentials retrieved", true);

        try {
            when(mockNetworkManager.sendRequestToServer(any())).thenReturn(expectedResponse);
        } catch (CommunicationException e) {
            throw new RuntimeException("CommunicationException occurred while testing execute method of RetrieveCredentialsCommand with valid arguments", e);
        }

        ServerResponse response;
        try {
            response = command.execute(arguments);
        } catch (CommandException | CommunicationException e) {
            throw new RuntimeException("Exception occurred while testing execute method of RetrieveCredentialsCommand with valid arguments", e);
        }

        assertEquals(expectedResponse, response, "Response should match the expected response");
    }

    @Test
    void testExecuteWithNullArgumentsThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null),
            "execute should throw an IllegalArgumentException when arguments are null");
    }

    @Test
    void testExecuteWithInvalidNumberOfArgumentsThrowsInvalidNumberOfArgumentsException() {
        String[] arguments = {"retrieve-credentials", "facebook.com"};
        assertThrows(InvalidNumberOfArgumentsException.class, () -> command.execute(arguments),
            "execute should throw an InvalidNumberOfArgumentsException when the number of arguments is invalid");
    }

    @Test
    void testExecuteWhenNotLoggedInThrowsNotLoggedInException() {
        String[] arguments = {"retrieve-credentials", "facebook.com", "test@mail.com"};
        when(mockStateManager.isLoggedIn()).thenReturn(false);

        assertThrows(NotLoggedInException.class, () -> command.execute(arguments),
            "execute should throw a NotLoggedInException when the user is not logged in");
    }
}
