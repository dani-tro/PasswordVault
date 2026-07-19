package bg.sofia.uni.fmi.mjt.server;

public class Session {
    private String socketAddress;
    private boolean isAuthenticated;

    public Session() {
        isAuthenticated = false;
    }

    public void authenticate(String socketAddress) {

        if (socketAddress == null) {
            throw new IllegalArgumentException("Socket address cannot be null");
        }

        this.socketAddress = socketAddress;
        isAuthenticated = true;
    }

    public String getSocketAddress() {
        return socketAddress;
    }

    public void logout() {
        isAuthenticated = false;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }
}
