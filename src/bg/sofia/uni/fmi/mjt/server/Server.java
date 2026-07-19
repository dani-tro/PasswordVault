package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.logging.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int SERVER_PORT = 7354;

    public static void main(String[] args) {

        ExecutorService executor = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            System.out.println("Server started and listening for connect requests");

            Socket clientSocket;

            while (true) {

                clientSocket = serverSocket.accept();

                System.out.println("Accepted connection request from client " + clientSocket.getRemoteSocketAddress());

                ClientRequestHandler clientHandler = new ClientRequestHandler(clientSocket);

                executor.execute(clientHandler);
            }

        } catch (IOException e) {
            Logger.logException("Error occurred with the server socket", e);
        }
    }
}
