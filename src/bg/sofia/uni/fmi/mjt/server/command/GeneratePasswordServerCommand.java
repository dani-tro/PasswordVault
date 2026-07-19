package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.server.PasswordCreator;
import bg.sofia.uni.fmi.mjt.server.Session;
import bg.sofia.uni.fmi.mjt.server.vault.UserVault;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class GeneratePasswordServerCommand implements ServerCommand {

    private final Map<String, Session> sessions;
    private final Map<String, UserVault> userVaults;
    private final String socketAddress;
    private final PasswordCreator passwordCreator;

    public GeneratePasswordServerCommand(Map<String, Session> sessions, Map<String, UserVault> userVaults,
                                         PasswordCreator passwordCreator, String socketAddress) {

        if (sessions == null || userVaults == null || socketAddress == null || passwordCreator == null) {
            throw new IllegalArgumentException(
                "sessions, userVaults, passwordCreator and socketAddress cannot be null");
        }

        this.sessions = sessions;
        this.userVaults = userVaults;
        this.socketAddress = socketAddress;
        this.passwordCreator = passwordCreator;
    }

    public GeneratePasswordServerCommand(Map<String, Session> sessions, Map<String, UserVault> userVaults,
                                         String socketAddress) {
        this(sessions, userVaults, new PasswordCreator(), socketAddress);
    }

    @Override
    public ServerResponse execute(ClientRequest request) throws APIException, NoSuchAlgorithmException {

        if (request == null || request.getUsername() == null || request.getWebsite() == null ||
            request.getWebsiteUsername() == null) {
            throw new IllegalArgumentException("request, username, website and websiteUsername cannot be null");
        }

        String username = request.getUsername();
        String website = request.getWebsite();
        String websiteUsername = request.getWebsiteUsername();
        Session session = sessions.get(username);

        if (session == null || !session.isAuthenticated() || !session.getSocketAddress().equals(socketAddress)) {
            return new ServerResponse("You must be logged in to execute generate-password command", false);
        }

        if (userVaults.get(username).retrieveCredentials(website, websiteUsername).isPresent()) {
            return new ServerResponse("Password for " + websiteUsername + " on " + website + " already exists",
                false);
        }

        String password = passwordCreator.generatePassword();
        userVaults.get(username).addPassword(website, websiteUsername, password);

        return new ServerResponse(
            "Password for " + websiteUsername + " on " + website + " successfully generated" + System.lineSeparator() +
                "Generated password: " + password, true);
    }
}
