package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.server.command.ServerCommand;
import bg.sofia.uni.fmi.mjt.server.vault.UserVault;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandExecutor {
    private static CommandExecutor instance;
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Map<String, UserVault> userVaults = new ConcurrentHashMap<>();

    private CommandExecutor() {
    }

    public static CommandExecutor getInstance() {
        if (instance == null) {
            instance = new CommandExecutor();
        }
        return instance;
    }

    public ServerResponse executeCommand(ClientRequest request, String socketAddress)
        throws APIException, NoSuchAlgorithmException {

        if (request == null || socketAddress == null) {
            throw new IllegalArgumentException("Request and socket address cannot be null");
        }

        return ServerCommand.of(request.getCommand(), sessions, userVaults, socketAddress).execute(request);
    }
}
