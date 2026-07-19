package bg.sofia.uni.fmi.mjt.client;

import bg.sofia.uni.fmi.mjt.exception.CommunicationException;
import bg.sofia.uni.fmi.mjt.logging.Logger;

public class Client {
    private final CLI cli;

    public Client() throws CommunicationException {
        this.cli = new CLI();
    }

    public Client(CLI cli) {
        this.cli = cli;
    }

    public static void main(String[] args) {
        Client client;
        try {
            client = new Client();
        } catch (CommunicationException e) {
            Logger.logException("Unable to connect to the server, try again later.", e);
            return;
        }
        try {
            client.start();
        } catch (CommunicationException e) {
            Logger.logException("An error occurred while communicating with the server.", e);
        }
    }

    public void start() throws CommunicationException {
        cli.start();
    }
}
