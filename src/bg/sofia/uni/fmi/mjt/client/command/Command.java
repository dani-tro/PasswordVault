package bg.sofia.uni.fmi.mjt.client.command;

import bg.sofia.uni.fmi.mjt.client.NetworkManager;
import bg.sofia.uni.fmi.mjt.client.StateManager;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.CommandException;
import bg.sofia.uni.fmi.mjt.exception.CommunicationException;
import bg.sofia.uni.fmi.mjt.exception.InvalidCommandException;

public interface Command {
    static Command of(String commandName, StateManager stateManager, NetworkManager networkManager)
        throws InvalidCommandException {
        return switch (commandName) {
            case "register" -> new RegisterCommand(stateManager, networkManager);
            case "login" -> new LoginCommand(stateManager, networkManager);
            case "logout" -> new LogoutCommand(stateManager, networkManager);
            case "retrieve-credentials" -> new RetrieveCredentialsCommand(stateManager, networkManager);
            case "generate-password" -> new GeneratePasswordCommand(stateManager, networkManager);
            case "add-password" -> new AddPasswordCommand(stateManager, networkManager);
            case "remove-password" -> new RemovePasswordCommand(stateManager, networkManager);
            case "disconnect" -> new DisconnectCommand(stateManager, networkManager);
            case "help" -> new HelpCommand();
            default -> throw new InvalidCommandException("Unknown command: " + commandName);
        };
    }

    /**
     * Executes the command with the given arguments.
     *
     * @param arguments the arguments for the command
     * @return the result of the command execution as a Response
     * @throws CommandException if an error occurs during command execution
     * @throws CommunicationException if an error occurs during communication with the server
     */
    ServerResponse execute(String[] arguments) throws CommandException, CommunicationException;
}
