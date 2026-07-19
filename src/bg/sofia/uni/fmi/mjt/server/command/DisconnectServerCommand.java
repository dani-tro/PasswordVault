package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.server.Session;
import bg.sofia.uni.fmi.mjt.server.vault.UserVault;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class DisconnectServerCommand implements ServerCommand {
    private static final String LOGOUT_COMMAND = "logout";

    private final Map<String, Session> sessions;
    private final Map<String, UserVault> userVaults;
    private final String socketAddress;

    public DisconnectServerCommand(Map<String, Session> sessions, Map<String, UserVault> userVaults,
                                   String socketAddress) {

        if (sessions == null || userVaults == null || socketAddress == null) {
            throw new IllegalArgumentException("sessions, userVaults and socketAddress cannot be null");
        }

        this.sessions = sessions;
        this.userVaults = userVaults;
        this.socketAddress = socketAddress;
    }

    @Override
    public ServerResponse execute(ClientRequest request) throws APIException, NoSuchAlgorithmException {

        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }

        String username = request.getUsername();
        Session session = sessions.get(username);

        if (session != null && session.isAuthenticated() && session.getSocketAddress().equals(socketAddress)) {
            ServerResponse response =
                ServerCommand.of(LOGOUT_COMMAND, sessions, userVaults, socketAddress).execute(request);

            sessions.remove(username);
            return new ServerResponse(response.message() + " and disconnected", response.isSuccessful());
        }
        return new ServerResponse("Successfully disconnected", true);
    }
}
