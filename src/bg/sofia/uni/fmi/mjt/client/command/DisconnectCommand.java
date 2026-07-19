package bg.sofia.uni.fmi.mjt.client.command;

import bg.sofia.uni.fmi.mjt.client.NetworkManager;
import bg.sofia.uni.fmi.mjt.client.StateManager;
import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.CommandException;
import bg.sofia.uni.fmi.mjt.exception.CommunicationException;
import bg.sofia.uni.fmi.mjt.exception.InvalidNumberOfArgumentsException;

public class DisconnectCommand implements Command {
    private static final int DISCONNECT_COMMAND_ARGUMENTS_COUNT = 1;
    private static final String INVALID_NUMBER_OF_ARGUMENTS_MESSAGE = "Invalid number of arguments for disconnect";
    private static final String DISCONNECT_COMMAND = "disconnect";

    private final NetworkManager networkManager;
    private final StateManager stateManager;

    public DisconnectCommand(StateManager stateManager, NetworkManager networkManager) {

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

        if (arguments.length != DISCONNECT_COMMAND_ARGUMENTS_COUNT) {
            throw new InvalidNumberOfArgumentsException(INVALID_NUMBER_OF_ARGUMENTS_MESSAGE);
        }

        ClientRequest request;

        if (stateManager.isLoggedIn()) {
            request = new ClientRequest.ClientRequestBuilder()
                .setCommand(DISCONNECT_COMMAND)
                .setUsername(stateManager.getUsername().get())
                .build();
            stateManager.logout();
        } else {
            request = new ClientRequest.ClientRequestBuilder()
                .setCommand(DISCONNECT_COMMAND).build();
        }

        ServerResponse response = networkManager.sendRequestToServer(request);
        networkManager.freeResources();
        return response;
    }
}
