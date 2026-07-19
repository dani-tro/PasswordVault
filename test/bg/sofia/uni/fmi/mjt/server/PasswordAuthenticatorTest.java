package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.algorithm.HashingAlgorithm;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.server.rest.APICandidate;
import bg.sofia.uni.fmi.mjt.server.rest.APIResponse;
import bg.sofia.uni.fmi.mjt.server.rest.EnzoicClient;
import bg.sofia.uni.fmi.mjt.server.rest.EnzoicResponseProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PasswordAuthenticatorTest {

    private static EnzoicClient mockClient;
    private static EnzoicResponseProcessor mockProcessor;
    private static HashingAlgorithm mockHashingAlgorithm;
    private static PasswordAuthenticator passwordAuthenticator;

    @BeforeAll
    static void setUp() {
        mockClient = mock();
        mockProcessor = mock();
        mockHashingAlgorithm = mock();

        passwordAuthenticator = new PasswordAuthenticator(mockClient, mockProcessor, mockHashingAlgorithm);
    }

    @Test
    void testIsPasswordStrongShouldThrowExceptionIfPasswordIsNull() {
        assertThrows(IllegalArgumentException.class, () -> passwordAuthenticator.isPasswordStrong(null), "isPasswordStrong should throw exception if the password is null");
    }

    @Test
    void testIsPasswordStrongShouldReturnFalseIfPasswordIsFoundInAPIResponse() {
        String password = "weakPassword";
        try {
            when(mockHashingAlgorithm.hash(password)).thenReturn("hash1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while hashing the password", e);
        }
        String response = "mockResponse";
        APIResponse apiResponse = new APIResponse(new APICandidate[] { new APICandidate("hash1", true, 123), new APICandidate("hash2", false, 5) });

        try {
            when(mockClient.sendRequest(password)).thenReturn(response);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred with sendRequest to the API", e);
        }
        when(mockProcessor.processResponse(response)).thenReturn(apiResponse);

        try {
            assertFalse(passwordAuthenticator.isPasswordStrong(password), "isPasswordStrong should return false if the password is found in the API response");
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing isPasswordStrong", e);
        }
    }

    @Test
    void testIsPasswordStrongShouldReturnTrueIfPasswordIsNotFoundInAPIResponse() {
        String password = "weakPassword";
        try {
            when(mockHashingAlgorithm.hash(password)).thenReturn("hash3");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while hashing the password", e);
        }
        String response = "mockResponse";
        APIResponse apiResponse = new APIResponse(new APICandidate[] { new APICandidate("hash1", true, 123), new APICandidate("hash2", false, 5) });

        try {
            when(mockClient.sendRequest(password)).thenReturn(response);
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred with sendRequest to the API", e);
        }
        when(mockProcessor.processResponse(response)).thenReturn(apiResponse);

        try {
            assertTrue(passwordAuthenticator.isPasswordStrong(password), "isPasswordStrong should return true if the password is not found in the API response");
        } catch (APIException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception occurred while testing isPasswordStrong", e);
        }
    }

}
