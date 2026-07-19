package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.exception.APIException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PasswordCreatorTest {

    private static PasswordCreator passwordCreator;

    @BeforeAll
    static void setUp() {
        PasswordAuthenticator mockPasswordAuthenticator = mock();
        try {
            when(mockPasswordAuthenticator.isPasswordStrong(anyString())).thenReturn(true);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Password strength check failed", e);
        }
        passwordCreator = new PasswordCreator(mockPasswordAuthenticator);
    }

    @Test
    void testGeneratedPasswordContainsUpperCaseLetter() {
        String password;
        try {
            password = passwordCreator.generatePassword();
        } catch (APIException e) {
            throw new RuntimeException("Password generation failed because of APIException", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password generation failed because of NoSuchAlgorithmException", e);
        }
        assertTrue(password.matches(".*[A-Z].*"));
    }

    @Test
    void testGeneratedPasswordContainsLowerCaseLetter() {
        String password;
        try {
            password = passwordCreator.generatePassword();
        } catch (APIException e) {
            throw new RuntimeException("Password generation failed because of APIException", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password generation failed because of NoSuchAlgorithmException", e);
        }
        assertTrue(password.matches(".*[a-z].*"));
    }

    @Test
    void testGeneratedPasswordContainsDigit() {
        String password;
        try {
            password = passwordCreator.generatePassword();
        } catch (APIException e) {
            throw new RuntimeException("Password generation failed because of APIException", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password generation failed because of NoSuchAlgorithmException", e);
        }
        assertTrue(password.matches(".*\\d.*"));
    }

    @Test
    void testGeneratedPasswordContainsSpecialSymbol() {
        String password;
        try {
            password = passwordCreator.generatePassword();
        } catch (APIException e) {
            throw new RuntimeException("Password generation failed because of APIException", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password generation failed because of NoSuchAlgorithmException", e);
        }
        assertTrue(password.matches(".*[!@#$%^&*()_+].*"));
    }

    @Test
    void testGeneratedPasswordIsWithinLengthBounds() {
        String password;
        try {
            password = passwordCreator.generatePassword();
        } catch (APIException e) {
            throw new RuntimeException("Password generation failed because of APIException", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password generation failed because of NoSuchAlgorithmException", e);
        }
        assertTrue(password.length() >= PasswordCreator.PASSWORD_MIN_LENGTH);
        assertTrue(password.length() <= PasswordCreator.PASSWORD_MAX_LENGTH);
    }
}