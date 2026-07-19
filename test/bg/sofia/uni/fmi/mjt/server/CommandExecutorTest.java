package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class CommandExecutorTest {
    @Test
    void testGetInstanceReturnsSameInstance() {
        CommandExecutor instance1 = CommandExecutor.getInstance();
        CommandExecutor instance2 = CommandExecutor.getInstance();
        assertEquals(instance1, instance2, "getInstance should return the same instance every time");
    }

    @Test
    void testExecuteCommandThrowsExceptionWhenAnyArgumentIsNull() {
        ClientRequest request = mock();
        CommandExecutor commandExecutor = CommandExecutor.getInstance();

        assertThrows(IllegalArgumentException.class, () -> commandExecutor.executeCommand(null, "test"), "executeCommand should throw exception if the request is null");
        assertThrows(IllegalArgumentException.class, () -> commandExecutor.executeCommand(request, null), "executeCommand should throw exception if the socket address is null");

    }
}
