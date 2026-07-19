package bg.sofia.uni.fmi.mjt.client.command;

import bg.sofia.uni.fmi.mjt.client.NetworkManager;
import bg.sofia.uni.fmi.mjt.client.StateManager;
import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.CommandException;
import bg.sofia.uni.fmi.mjt.exception.CommunicationException;
import bg.sofia.uni.fmi.mjt.exception.InvalidNumberOfArgumentsException;
import bg.sofia.uni.fmi.mjt.exception.NotLoggedInException;

public class LogoutCommand implements Command {
    private static final int LOGOUT_COMMAND_ARGUMENTS_COUNT = 1;
    private static final String NOT_LOGGED_IN_MESSAGE = "You are not logged in";
    private static final String INVALID_NUMBER_OF_ARGUMENTS_MESSAGE = "Invalid number of arguments for logout";
    private static final String LOGOUT_COMMAND = "logout";

    private final StateManager stateManager;
    private final NetworkManager networkManager;

    public LogoutCommand(StateManager stateManager, NetworkManager networkManager) {

        if (stateManager == null || networkManager == null) {
            throw new IllegalArgumentException("stateManager and networkManager cannot be null");
        }

        this.stateManager = stateManager;
        this.networkManager = networkManager;
    }

    @Override
    public ServerResponse execute(String[] arguments) throws CommandException, CommunicationException {

        if (arguments == null) {
            throw new IllegalArgumentException("arguments cannot be null");
        }

        if (arguments.length != LOGOUT_COMMAND_ARGUMENTS_COUNT) {
            throw new InvalidNumberOfArgumentsException(INVALID_NUMBER_OF_ARGUMENTS_MESSAGE);
        }

        if (!stateManager.isLoggedIn()) {
            throw new NotLoggedInException(NOT_LOGGED_IN_MESSAGE);
        }

        ClientRequest request = ClientRequest.builder()
            .setCommand(LOGOUT_COMMAND)
            .setUsername(stateManager.getUsername().get())
            .build();

        ServerResponse response = networkManager.sendRequestToServer(request);

        if (response.isSuccessful()) {
            stateManager.logout();
        }
        return response;
    }
}
