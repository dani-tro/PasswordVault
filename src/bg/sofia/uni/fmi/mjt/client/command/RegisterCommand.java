package bg.sofia.uni.fmi.mjt.client.command;

import bg.sofia.uni.fmi.mjt.client.NetworkManager;
import bg.sofia.uni.fmi.mjt.client.StateManager;
import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.AlreadyLoggedInException;
import bg.sofia.uni.fmi.mjt.exception.CommandException;
import bg.sofia.uni.fmi.mjt.exception.CommunicationException;
import bg.sofia.uni.fmi.mjt.exception.InvalidNumberOfArgumentsException;
import bg.sofia.uni.fmi.mjt.exception.PasswordsDoNotMatchException;

public class RegisterCommand implements Command {
    private static final int REGISTER_COMMAND_ARGUMENTS_COUNT = 4;
    private static final int USERNAME_INDEX = 1;
    private static final int PASSWORD_INDEX = 2;
    private static final int REPEATED_PASSWORD_INDEX = 3;
    private static final String ALREADY_LOGGED_IN_MESSAGE = "You are already logged in";
    private static final String PASSWORDS_DO_NOT_MATCH_MESSAGE = "Passwords do not match";
    private static final String INVALID_NUMBER_OF_ARGUMENTS_MESSAGE = "Invalid number of arguments for register";
    private static final String REGISTER_COMMAND = "register";

    private final NetworkManager networkManager;
    private final StateManager stateManager;

    public RegisterCommand(StateManager stateManager, NetworkManager networkManager) {

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

        if (arguments.length != REGISTER_COMMAND_ARGUMENTS_COUNT) {
            throw new InvalidNumberOfArgumentsException(INVALID_NUMBER_OF_ARGUMENTS_MESSAGE);
        }

        String username = arguments[USERNAME_INDEX];
        String password = arguments[PASSWORD_INDEX];
        String repeatedPassword = arguments[REPEATED_PASSWORD_INDEX];

        if (stateManager.isLoggedIn()) {
            throw new AlreadyLoggedInException(ALREADY_LOGGED_IN_MESSAGE);
        }
        if (!password.equals(repeatedPassword)) {
            throw new PasswordsDoNotMatchException(PASSWORDS_DO_NOT_MATCH_MESSAGE);
        }

        ClientRequest request = new ClientRequest.ClientRequestBuilder()
            .setCommand(REGISTER_COMMAND)
            .setUsername(username)
            .setPassword(password)
            .build();

        return networkManager.sendRequestToServer(request);
    }
}
