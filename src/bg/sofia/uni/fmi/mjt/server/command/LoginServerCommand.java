package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.server.Session;
import bg.sofia.uni.fmi.mjt.server.vault.UserVault;
import bg.sofia.uni.fmi.mjt.server.vault.Vault;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class LoginServerCommand implements ServerCommand {

    private final Map<String, Session> sessions;
    private final Map<String, UserVault> userVaults;
    private final String socketAddress;
    private final Vault vault;

    public LoginServerCommand(Map<String, Session> sessions, Map<String, UserVault> userVaults, String socketAddress,
                              Vault vault) {

        if (sessions == null || userVaults == null || socketAddress == null || vault == null) {
            throw new IllegalArgumentException("sessions, userVaults, socketAddress and vault cannot be null");
        }

        this.sessions = sessions;
        this.userVaults = userVaults;
        this.socketAddress = socketAddress;
        this.vault = vault;
    }

    public LoginServerCommand(Map<String, Session> sessions, Map<String, UserVault> userVaults, String socketAddress) {
        this(sessions, userVaults, socketAddress, Vault.getInstance());
    }

    @Override
    public ServerResponse execute(ClientRequest request) throws NoSuchAlgorithmException {

        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("request, username and password cannot be null");
        }

        String username = request.getUsername();
        String password = request.getPassword();

        if (!vault.authenticate(username, password)) {
            return new ServerResponse("Invalid username or password", false);
        }

        Session session = sessions.get(username);
        if (session == null) {
            session = new Session();
            sessions.put(username, session);
        }
        if (session.isAuthenticated()) {
            return new ServerResponse("User " + username + " is already logged in", false);
        }

        session.authenticate(socketAddress);
        userVaults.put(username, new UserVault(username));
        return new ServerResponse("User " + username + " successfully logged in", true);
    }
}
