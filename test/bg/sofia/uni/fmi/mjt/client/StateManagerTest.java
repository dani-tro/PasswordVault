package bg.sofia.uni.fmi.mjt.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StateManagerTest {

    @Test
    void testIsLoggedInWhenNotLoggedIn() {
        StateManager stateManager = new StateManager();
        assertFalse(stateManager.isLoggedIn(), "isLoggedIn should return false when no login has been performed");
    }

    @Test
    void testGetUsernameWhenNotLoggedIn() {
        StateManager stateManager = new StateManager();
        assertTrue(stateManager.getUsername().isEmpty(), "getUsername should return empty Optional when no login has been performed");
    }

    @Test
    void testLogin() {
        StateManager stateManager = new StateManager();
        stateManager.login("user");
        assertTrue(stateManager.isLoggedIn(), "isLoggedIn should return true after login is performed");
        assertFalse(stateManager.getUsername().isEmpty(), "getUsername should return non-empty Optional after login is performed");
        assertEquals("user", stateManager.getUsername().get(), "getUsername should return the right username after login is performed");
    }

    @Test
    void testLoginWithNullUsernameThrowsException() {
        StateManager stateManager = new StateManager();
        assertThrows(IllegalArgumentException.class, () -> stateManager.login(null), "login should throw IllegalArgumentException when username is null");
    }

    @Test
    void testLogoutAfterLoggingIn() {
        StateManager stateManager = new StateManager();
        stateManager.login("user");
        stateManager.logout();
        assertFalse(stateManager.isLoggedIn(), "isLoggedIn should return false after logout");
        assertTrue(stateManager.getUsername().isEmpty(), "getUsername should return empty Optional after logout");
    }
}
