package bg.sofia.uni.fmi.mjt.server.vault;

import bg.sofia.uni.fmi.mjt.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.server.PasswordAuthenticator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserVaultTest {

    private static final String PASSWORD_FILES_DIRECTORY = "PasswordFiles";
    private static final String PASSWORD_FILE_EXTENSION = ".txt";
    private static UserVault userVault;
    private static PasswordAuthenticator mockPasswordAuthenticator;
    private static Rijndael rijndael = new Rijndael();

    @BeforeAll
    static void setUpAll() {

        mockPasswordAuthenticator = mock();
        try {
            when(mockPasswordAuthenticator.isPasswordStrong("strongPassword")).thenReturn(true);
            when(mockPasswordAuthenticator.isPasswordStrong("weakPassword")).thenReturn(false);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while setting up mockPasswordAuthenticator", e);
        }
    }

    @BeforeEach
    void setUp() {
        try {
            Files.deleteIfExists(
                Paths.get(PASSWORD_FILES_DIRECTORY + File.separator + "test" + PASSWORD_FILE_EXTENSION));
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred while deleting test file", e);
        }
        userVault = new UserVault("test", mockPasswordAuthenticator, rijndael);
    }

    @Test
    void testUserVaultShouldCreateFileIfNotExists() {
        try {
            userVault.addPassword("facebook", "test-user@mail", "password");
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing UserVault constructor", e);
        }
        assertTrue(Files.exists(Paths.get("PasswordFiles" + File.separator + "test.txt")), "File should be created");
    }

    @Test
    void testAddPasswordShouldReturnFalseIfPasswordIsWeak() {
        try {
            assertFalse(userVault.addPassword("instagram", "test-user@mail", "weakPassword"), "addPassword should return false if password is weak");
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing addPassword with weak password", e);
        }
    }

    @Test
    void testAddPasswordShouldReturnTrueIfPasswordIsStrong() {
        try {
            assertTrue(userVault.addPassword("instagram", "test-user@mail", "strongPassword"),
                "addPassword should return true if password is strong");
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing addPassword with strong password", e);
        }
    }

    @Test
    void testRetrieveCredentialsShouldReturnEmptyIfNoMatch() {
        Optional<String> result = userVault.retrieveCredentials("non-existing", "non-existing@mail");
        assertTrue(result.isEmpty(), "No match");
    }

    @Test
    void testRetrieveCredentialsShouldReturnPasswordIfMatch() {
        try {
            userVault.addPassword("instagram", "test-user@mail", "strongPassword");
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing retrieveCredentials", e);
        }
        Optional<String> result = userVault.retrieveCredentials("instagram", "test-user@mail");
        assertTrue(result.isPresent(), "Password retrieved");
        assertEquals("strongPassword", result.get(), "Password retrieved");
    }

    @Test
    void testRemovePasswordShouldDeletePasswordIfMatch() {
        try {
            userVault.addPassword("facebook", "test-user@mail", "strongPassword");
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing removePassword", e);
        }
        userVault.removePassword("facebook", "test-user@mail");
        Optional<String> result = userVault.retrieveCredentials("facebook", "test-user@mail");
        assertTrue(result.isEmpty(), "Password removed");
    }

    @Test
    void testAddPasswordShouldReturnFalseIfPasswordExists() {
        try {
            userVault.addPassword("facebook", "test-user@mail", "strongPassword");
            assertFalse(userVault.addPassword("facebook", "test-user@mail", "strongPassword"), "Password exists");
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing addPassword when password exists", e);
        }
    }

    @Test
    void testAddPasswordShouldThrowExceptionIfAnyArgumentIsNull() {
        assertThrows(IllegalArgumentException.class, () -> userVault.addPassword(null, "test-user@mail", "password"), "Website cannot be null in addPassword");
        assertThrows(IllegalArgumentException.class, () -> userVault.addPassword("facebook", null, "password"), "User cannot be null in addPassword");
        assertThrows(IllegalArgumentException.class, () -> userVault.addPassword("facebook", "test-user@mail", null), "Password cannot be null in addPassword");
    }

    @Test
    void testRetrieveCredentialsShouldThrowExceptionIfAnyArgumentIsNull() {
        assertThrows(IllegalArgumentException.class, () -> userVault.retrieveCredentials(null, "test-user@mail"), "Website cannot be null in retrieveCredentials");
        assertThrows(IllegalArgumentException.class, () -> userVault.retrieveCredentials("facebook", null), "User cannot be null in retrieveCredentials");
    }

    @Test
    void testRemovePasswordShouldThrowExceptionIfAnyArgumentIsNull() {
        assertThrows(IllegalArgumentException.class, () -> userVault.removePassword(null, "test-user@mail"), "Website cannot be null in removePassword");
        assertThrows(IllegalArgumentException.class, () -> userVault.removePassword("facebook", null), "User cannot be null in removePassword");
    }
}
