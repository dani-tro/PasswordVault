package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.logging.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientRequestHandler implements Runnable {

    private static final int MINUTES_TIMEOUT = 1;
    private static final String LOGOUT_MESSAGE = "logout";
    private static final String DISCONNECT_MESSAGE = "disconnect";
    private final Socket socket;
    private final CommandExecutor commandExecutor = CommandExecutor.getInstance();
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private final String socketAddress;
    private ScheduledFuture<?> scheduledFuture;

    public ClientRequestHandler(Socket socket) {
        if (socket == null) {
            throw new IllegalArgumentException("Socket cannot be null");
        }

        this.socket = socket;
        socketAddress = socket.getRemoteSocketAddress().toString();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Client Request Handler for " + socket.getRemoteSocketAddress());
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            ClientRequest request;
            while ((request = (ClientRequest) in.readObject()) != null) {
                if (scheduledFuture != null && !scheduledFuture.isDone()) {
                    scheduledFuture.cancel(false);
                }
                if (request.getUsername() != null &&
                    !request.getCommand().equals(LOGOUT_MESSAGE) && !request.getCommand().equals(DISCONNECT_MESSAGE)) {
                    scheduledFuture = scheduleLogout(request.getUsername(), out);
                }

                out.writeObject(invokeCommandExecutor(request));
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                Logger.logException("Error occurred while reading/writing from the socket", e);
            }
        } catch (ClassNotFoundException e) {
            Logger.logException("Error occurred while reading from the socket", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                Logger.logException("Error occurred while closing the socket", e);
            }
        }
    }

    private ScheduledFuture<?> scheduleLogout(String username, ObjectOutputStream out) {

        if (username == null || out == null) {
            throw new IllegalArgumentException("Username and out cannot be null");
        }

        return executor.schedule(() -> {
            try {
                out.writeObject(new ServerResponse(LOGOUT_MESSAGE, true));
            } catch (IOException e) {
                Logger.logException("Error occurred while writing to the socket", e);
            }
            invokeCommandExecutor(ClientRequest.builder().setUsername(username).setCommand(LOGOUT_MESSAGE).build());
        },
        MINUTES_TIMEOUT,
        TimeUnit.MINUTES);
    }

    private ServerResponse invokeCommandExecutor(ClientRequest request) {
        ServerResponse response;
        try {
            response = commandExecutor.executeCommand(request, socketAddress);
        } catch (APIException e) {
            Logger.logException("API Exception occurred", e);
            response = new ServerResponse(
                "Unsuccessful command execution, cannot assure a password is strong enough right now, try again later",
                false);
        } catch (NoSuchAlgorithmException e) {
            Logger.logException("Cannot hash the password for user on socket " + socketAddress, e);
            response = new ServerResponse(
                "Unsuccessful command execution, cannot assure a password is strong enough right now, try again later",
                false);
        }
        return response;
    }
}
