package bg.sofia.uni.fmi.mjt.server.vault;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class VaultTest {

    private Vault vault;

    @BeforeEach
    void setup() {
        vault = Vault.getInstance();
    }

    @Test
    void testAddUserThrowsExceptionWhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> vault.addUser(null, "password"), "Username cannot be null");
    }

    @Test
    void testAddUserThrowsExceptionWhenPasswordIsNull() {
        assertThrows(IllegalArgumentException.class, () -> vault.addUser("username", null), "Password cannot be null");
    }

    @Test
    void testAuthenticateThrowsExceptionWhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> vault.authenticate(null, "password"), "Username cannot be null");
    }

    @Test
    void testAuthenticateThrowsExceptionWhenPasswordIsNull() {
        assertThrows(IllegalArgumentException.class, () -> vault.authenticate("username", null), "Password cannot be null");
    }
}