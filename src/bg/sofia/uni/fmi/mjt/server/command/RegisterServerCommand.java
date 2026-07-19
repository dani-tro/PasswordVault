package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.server.vault.Vault;

import java.security.NoSuchAlgorithmException;

public class RegisterServerCommand implements ServerCommand {

    private final Vault vault;

    public RegisterServerCommand() {
        this.vault = Vault.getInstance();
    }

    public RegisterServerCommand(Vault vault) {

        if (vault == null) {
            throw new IllegalArgumentException("vault cannot be null");
        }

        this.vault = vault;
    }

    @Override
    public ServerResponse execute(ClientRequest request) throws NoSuchAlgorithmException {

        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("request, username and password cannot be null");
        }

        String username = request.getUsername();
        String password = request.getPassword();
        if (vault.addUser(username, password)) {
            return new ServerResponse("User " + username + " successfully registered", true);
        }
        return new ServerResponse("User " + username + " already exists", false);
    }
}
