package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.server.PasswordAuthenticator;
import bg.sofia.uni.fmi.mjt.server.Session;
import bg.sofia.uni.fmi.mjt.server.vault.UserVault;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class AddPasswordServerCommand implements ServerCommand {

    private final Map<String, Session> sessions;
    private final Map<String, UserVault> userVaults;
    private final String socketAddress;
    private final PasswordAuthenticator passwordAuthenticator;

    public AddPasswordServerCommand(Map<String, Session> sessions, Map<String, UserVault> userVaults,
                                    PasswordAuthenticator passwordAuthenticator, String socketAddress) {

        if (sessions == null || userVaults == null || socketAddress == null || passwordAuthenticator == null) {
            throw new IllegalArgumentException(
                "sessions, userVaults, passwordAuthenticator and socketAddress cannot be null");
        }

        this.sessions = sessions;
        this.userVaults = userVaults;
        this.socketAddress = socketAddress;
        this.passwordAuthenticator = passwordAuthenticator;
    }

    public AddPasswordServerCommand(Map<String, Session> sessions, Map<String, UserVault> userVaults,
                                    String socketAddress) {
        this(sessions, userVaults, new PasswordAuthenticator(), socketAddress);
    }

    @Override
    public ServerResponse execute(ClientRequest request) throws APIException, NoSuchAlgorithmException {

        if (request == null || request.getUsername() == null || request.getWebsite() == null ||
            request.getWebsiteUsername() == null || request.getWebsitePassword() == null) {
            throw new IllegalArgumentException(
                "request, username, website, websiteUsername and websitePassword cannot be null");
        }

        String username = request.getUsername();
        String website = request.getWebsite();
        String websiteUsername = request.getWebsiteUsername();
        String password = request.getWebsitePassword();
        Session session = sessions.get(username);

        if (session == null || !session.isAuthenticated() || !session.getSocketAddress().equals(socketAddress)) {
            return new ServerResponse("You must be logged in to execute add-password command", false);
        }
        if (userVaults.get(username).retrieveCredentials(website, websiteUsername).isPresent()) {
            return new ServerResponse("Password for " + websiteUsername + " on " + website + " already exists", false);
        }

        if (!passwordAuthenticator.isPasswordStrong(password)) {
            return new ServerResponse("Password is not strong enough", false);
        }
        if (userVaults.get(username).addPassword(website, websiteUsername, password)) {
            return new ServerResponse("Password for " + websiteUsername + " on " + website + " successfully added",
                true);
        }
        return new ServerResponse("Password for " + websiteUsername + " on " + website + " could not be added", false);
    }
}
