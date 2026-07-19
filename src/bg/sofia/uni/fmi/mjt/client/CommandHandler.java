package bg.sofia.uni.fmi.mjt.client;

import bg.sofia.uni.fmi.mjt.client.command.Command;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.CommandException;
import bg.sofia.uni.fmi.mjt.exception.CommunicationException;
import bg.sofia.uni.fmi.mjt.exception.UnsuccessfulCommandException;

public class CommandHandler {
    private static final int COMMAND_TYPE_INDEX = 0;
    private final NetworkManager networkManager;
    private final StateManager stateManager;

    public CommandHandler() throws CommunicationException {
        this.networkManager = new NetworkManager();
        this.stateManager = new StateManager();
        new Thread(new Listener(networkManager, stateManager)).start();
    }

    public CommandHandler(NetworkManager networkManager) {

        if (networkManager == null) {
            throw new IllegalArgumentException("networkManager cannot be null");
        }

        this.networkManager = networkManager;
        this.stateManager = new StateManager();
        new Thread(new Listener(networkManager, stateManager)).start();
    }

    public CommandHandler(NetworkManager networkManager, StateManager stateManager) {

        if (networkManager == null || stateManager == null) {
            throw new IllegalArgumentException("networkManager and stateManager cannot be null");
        }

        this.networkManager = networkManager;
        this.stateManager = stateManager;
        new Thread(new Listener(networkManager, stateManager)).start();
    }

    public String handleCommand(String command) throws CommandException, CommunicationException {

        if (command == null) {
            throw new IllegalArgumentException("command cannot be null");
        }

        String[] arguments = command.split("\\s+");
        String commandType = arguments[COMMAND_TYPE_INDEX];

        Command cmd = Command.of(commandType, stateManager, networkManager);
        ServerResponse response = cmd.execute(arguments);

        if (!response.isSuccessful()) {
            throw new UnsuccessfulCommandException(response.message());
        }
        return response.message() == null ? "" : response.message();
    }

}
