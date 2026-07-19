package bg.sofia.uni.fmi.mjt.client;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.CommunicationException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class NetworkManager implements AutoCloseable {

    private static final int SERVER_PORT = 7354;
    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    private final Scanner scanner;
    private boolean isRunning;
    private boolean isSendingRequest = false;

    public NetworkManager() throws CommunicationException {
        try {
            socket = new Socket("localhost", SERVER_PORT);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            scanner = new Scanner(System.in);
        } catch (IOException e) {
            throw new CommunicationException("Error connecting to the server", e);
        }
        isRunning = true;
    }

    public NetworkManager(Socket socket) throws CommunicationException {
        this.socket = socket;
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            scanner = new Scanner(System.in);
        } catch (IOException e) {
            throw new CommunicationException("Error connecting to the server", e);
        }
        isRunning = true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isSendingRequest() {
        return isSendingRequest;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public Socket getSocket() {
        return socket;
    }

    public ServerResponse sendRequestToServer(ClientRequest request) throws CommunicationException {

        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }

        isSendingRequest = true;
        try {
            outputStream.writeObject(request);
        } catch (IOException e) {
            throw new CommunicationException("Error sending request to the server", e);
        }

        try {
            return (ServerResponse) inputStream.readObject();
        } catch (IOException e) {
            throw new CommunicationException("Error receiving response from the server", e);
        } catch (ClassNotFoundException e) {
            throw new CommunicationException("Error deserializing the response from the server", e);
        } finally {
            isSendingRequest = false;
        }
    }

    @Override
    public void close() throws CommunicationException {
        sendRequestToServer(ClientRequest.builder().setCommand("logout").build());
        freeResources();
    }

    public void freeResources() {
        isRunning = false;
        scanner.close();
        try {
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            throw new IllegalStateException("Error closing the streams", e);
        }

        try {
            socket.close();
        } catch (IOException e) {
            throw new IllegalStateException("Error closing the connection", e);
        }
    }
}
