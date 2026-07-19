package bg.sofia.uni.fmi.mjt.client;

import java.util.Optional;

public class StateManager {
    private Optional<String> username = Optional.empty();

    public Optional<String> getUsername() {
        return username;
    }

    public synchronized boolean isLoggedIn() {
        return username.isPresent();
    }

    public synchronized void login(String username) {

        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }

        this.username = Optional.of(username);
    }

    public synchronized void logout() {
        this.username = Optional.empty();
    }
}
