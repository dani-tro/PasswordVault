package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.server.Session;
import bg.sofia.uni.fmi.mjt.server.vault.UserVault;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface ServerCommand {
    static ServerCommand of(String commandName, Map<String, Session> sessions, Map<String, UserVault> userVaults,
                            String socketAddress) {
        return switch (commandName) {
            case "register" -> new RegisterServerCommand();
            case "login" -> new LoginServerCommand(sessions, userVaults, socketAddress);
            case "logout" -> new LogoutServerCommand(sessions, userVaults, socketAddress);
            case "retrieve-credentials" -> new RetrieveCredentialsServerCommand(sessions, userVaults, socketAddress);
            case "generate-password" -> new GeneratePasswordServerCommand(sessions, userVaults, socketAddress);
            case "add-password" -> new AddPasswordServerCommand(sessions, userVaults, socketAddress);
            case "remove-password" -> new RemovePasswordServerCommand(sessions, userVaults, socketAddress);
            case "disconnect" -> new DisconnectServerCommand(sessions, userVaults, socketAddress);
            default -> throw new IllegalArgumentException("Unknown command: " + commandName);
        };
    }

    /**
     * Executes the command with the given arguments.
     *
     * @param request the request containing the command and its arguments
     * @return the result of the command execution as a Response
     * @throws APIException             if an error occurs during the communication with the API
     * @throws NoSuchAlgorithmException if an error occurs during the hashing of the password
     */
    ServerResponse execute(ClientRequest request) throws APIException, NoSuchAlgorithmException;
}
