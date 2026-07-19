package bg.sofia.uni.fmi.mjt.client;

import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.logging.Logger;

import java.io.IOException;

public class Listener implements Runnable {
    private static final String LOGOUT_COMMAND = "logout";
    private static final String LOGOUT_MESSAGE = "You have been logged out by the server.";
    private static final String SERVER_MESSAGE = "Message from the server: ";

    private final NetworkManager networkManager;
    private final StateManager stateManager;

    public Listener(NetworkManager networkManager, StateManager stateManager) {

        if (networkManager == null || stateManager == null) {
            throw new IllegalArgumentException("networkManager and stateManager cannot be null");
        }

        this.networkManager = networkManager;
        this.stateManager = stateManager;
    }

    @Override
    public void run() {
        while (networkManager.isRunning()) {

            try {
                if (!networkManager.isSendingRequest() && networkManager.getSocket().getInputStream().available() > 0) {

                    ServerResponse response = (ServerResponse) networkManager.getInputStream().readObject();

                    if (response.message() != null && LOGOUT_COMMAND.equals(response.message())) {

                        if (stateManager.isLoggedIn()) {
                            stateManager.logout();
                            Logger.logMessageToConsole(LOGOUT_MESSAGE);
                        }
                    } else {
                        Logger.logMessageToConsole(SERVER_MESSAGE + response.message());
                    }
                }
            } catch (IOException e) {
                if (networkManager.isRunning()) {
                    Logger.logException("Error occurred while listening for commands", e);
                    throw new IllegalStateException("Error occurred while listening for commands", e);
                }
            } catch (ClassNotFoundException e) {
                Logger.logException("Error occurred while receiving information from the server", e);
                throw new IllegalStateException("Error occurred while reading the object from the socket", e);
            }
        }
    }
}
