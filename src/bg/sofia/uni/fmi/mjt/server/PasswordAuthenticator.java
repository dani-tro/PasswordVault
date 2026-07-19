package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.algorithm.HashingAlgorithm;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.server.rest.APIResponse;
import bg.sofia.uni.fmi.mjt.server.rest.EnzoicClient;
import bg.sofia.uni.fmi.mjt.server.rest.EnzoicResponseProcessor;

import java.security.NoSuchAlgorithmException;

public class PasswordAuthenticator {

    private final EnzoicClient enzoicClient;

    private final EnzoicResponseProcessor enzoicResponseProcessor;

    HashingAlgorithm hashingAlgorithm;

    public PasswordAuthenticator() {
        enzoicClient = new EnzoicClient();
        enzoicResponseProcessor = new EnzoicResponseProcessor();
        hashingAlgorithm = new HashingAlgorithm();
    }

    public PasswordAuthenticator(EnzoicClient enzoicClient, EnzoicResponseProcessor enzoicResponseProcessor,
                                 HashingAlgorithm hashingAlgorithm) {

        if (enzoicClient == null || enzoicResponseProcessor == null || hashingAlgorithm == null) {
            throw new IllegalArgumentException(
                "enzoicClient, enzoicResponseProcessor and hashingAlgorithm cannot be null");
        }

        this.enzoicClient = enzoicClient;
        this.enzoicResponseProcessor = enzoicResponseProcessor;
        this.hashingAlgorithm = hashingAlgorithm;
    }

    public boolean isPasswordStrong(String password) throws APIException, NoSuchAlgorithmException {

        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        String passwordHash = hashingAlgorithm.hash(password);

        String response = enzoicClient.sendRequest(password);

        APIResponse apiResponse = enzoicResponseProcessor.processResponse(response);

        for (int i = 0; i < apiResponse.candidates().length; i++) {
            if (apiResponse.candidates()[i].sha256().equals(passwordHash)) {
                return false;
            }
        }
        return true;
    }

}
