package bg.sofia.uni.fmi.mjt.client.command;

import bg.sofia.uni.fmi.mjt.client.NetworkManager;
import bg.sofia.uni.fmi.mjt.client.StateManager;
import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.CommandException;
import bg.sofia.uni.fmi.mjt.exception.CommunicationException;
import bg.sofia.uni.fmi.mjt.exception.InvalidNumberOfArgumentsException;
import bg.sofia.uni.fmi.mjt.exception.NotLoggedInException;

public class RetrieveCredentialsCommand implements Command {
    private static final int RETRIEVE_CREDENTIALS_COMMAND_ARGUMENTS_COUNT = 3;
    private static final int WEBSITE_INDEX = 1;
    private static final int WEBSITE_USERNAME_INDEX = 2;
    private static final String INVALID_NUMBER_OF_ARGUMENTS_MESSAGE =
        "Invalid number of arguments for retrieve-credentials";
    private static final String NOT_LOGGED_IN_MESSAGE = "You are not logged in";
    private static final String RETRIEVE_CREDENTIALS_COMMAND = "retrieve-credentials";

    private final StateManager stateManager;
    private final NetworkManager networkManager;

    public RetrieveCredentialsCommand(StateManager stateManager, NetworkManager networkManager) {

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

        if (arguments.length != RETRIEVE_CREDENTIALS_COMMAND_ARGUMENTS_COUNT) {
            throw new InvalidNumberOfArgumentsException(INVALID_NUMBER_OF_ARGUMENTS_MESSAGE);
        }

        if (!stateManager.isLoggedIn()) {
            throw new NotLoggedInException(NOT_LOGGED_IN_MESSAGE);
        }

        String website = arguments[WEBSITE_INDEX];
        String websiteUsername = arguments[WEBSITE_USERNAME_INDEX];

        ClientRequest request = new ClientRequest.ClientRequestBuilder()
            .setCommand(RETRIEVE_CREDENTIALS_COMMAND)
            .setUsername(stateManager.getUsername().get())
            .setWebsite(website)
            .setWebsiteUsername(websiteUsername)
            .build();

        return networkManager.sendRequestToServer(request);
    }
}
