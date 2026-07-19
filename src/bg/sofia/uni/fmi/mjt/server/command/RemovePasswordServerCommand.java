package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.server.Session;
import bg.sofia.uni.fmi.mjt.server.vault.UserVault;

import java.util.Map;

public class RemovePasswordServerCommand implements ServerCommand {
    private final Map<String, Session> sessions;
    private final Map<String, UserVault> userVaults;
    private final String socketAddress;

    public RemovePasswordServerCommand(Map<String, Session> sessions, Map<String, UserVault> userVaults,
                                       String socketAddress) {

        if (sessions == null || userVaults == null || socketAddress == null) {
            throw new IllegalArgumentException("sessions, userVaults and socketAddress cannot be null");
        }

        this.sessions = sessions;
        this.userVaults = userVaults;
        this.socketAddress = socketAddress;
    }

    @Override
    public ServerResponse execute(ClientRequest request) {

        if (request == null || request.getUsername() == null || request.getWebsite() == null ||
            request.getWebsiteUsername() == null) {
            throw new IllegalArgumentException("request, username, website and websiteUsername cannot be null");
        }

        String username = request.getUsername();
        String website = request.getWebsite();
        String websiteUsername = request.getWebsiteUsername();

        Session session = sessions.get(username);
        if (session == null || !session.isAuthenticated() || !session.getSocketAddress().equals(socketAddress)) {
            return new ServerResponse("You must be logged in to execute remove-password command", false);
        }

        if (userVaults.get(username).retrieveCredentials(website, websiteUsername).isEmpty()) {
            return new ServerResponse(
                "Password for " + websiteUsername + " on " + website + " does not exist", false);
        }

        userVaults.get(username).removePassword(website, websiteUsername);
        return new ServerResponse(
            "Password for " + websiteUsername + " on " + website + " successfully removed", true);
    }
}
