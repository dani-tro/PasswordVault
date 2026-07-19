package bg.sofia.uni.fmi.mjt.server.rest;

import bg.sofia.uni.fmi.mjt.algorithm.HashingAlgorithm;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.exception.InvalidURIException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EnzoicClient {
    public static final String AUTHORIZATION = "authorization";
    public static final String BASIC = "basic ";
    public static final String CONTENT_TYPE_VALUE = "application/json";
    public static final String CONTENT_TYPE_FIELD = "Content-Type";
    public static final String PARTIAL_SHA_256 = "{\"partialSHA256\": \"";
    public static final String FIELDS_SHA_256 = "\", \"fields\": \"sha256\"}";
    private static final String API_URL = "https://api.enzoic.com/v1/passwords";
    private static final String EMPTY_BODY = "";
    private static final int SUCCESS_STATUS_CODE = 200;
    private static final int NO_CANDIDATES_FOUND_STATUS_CODE = 404;
    private static final int HASH_PREFIX_LENGTH = 10;
    private final HashingAlgorithm hashingAlgorithm;
    private final HttpClient client;
    private final APISecretRetriever apiSecretRetriever;
    private final APIKeyRetriever apiKeyRetriever;

    public EnzoicClient() {
        hashingAlgorithm = new HashingAlgorithm();
        client = HttpClient.newHttpClient();
        apiSecretRetriever = APISecretRetriever.getInstance();
        apiKeyRetriever = APIKeyRetriever.getInstance();
    }

    public EnzoicClient(HashingAlgorithm hashingAlgorithm, HttpClient client, APISecretRetriever apiSecretRetriever,
                        APIKeyRetriever apiKeyRetriever) {
        if (hashingAlgorithm == null || client == null || apiSecretRetriever == null || apiKeyRetriever == null) {
            throw new IllegalArgumentException(
                "Hashing algorithm, client, API secret retriever and API key retriever cannot be null");
        }

        this.hashingAlgorithm = hashingAlgorithm;
        this.client = client;
        this.apiSecretRetriever = apiSecretRetriever;
        this.apiKeyRetriever = apiKeyRetriever;
    }

    public String sendRequest(String password) throws APIException, NoSuchAlgorithmException {

        HttpRequest request = createRequest(password);

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new APIException("Error occurred while sending request to Enzoic API", e);
        }

        if (response.statusCode() == SUCCESS_STATUS_CODE) {
            return response.body();
        } else if (response.statusCode() == NO_CANDIDATES_FOUND_STATUS_CODE) {
            return EMPTY_BODY;
        }
        throw new APIException("Error occurred while sending request to Enzoic API");
    }

    private HttpRequest createRequest(String password) throws APIException, NoSuchAlgorithmException {
        String encodedAuth =
            Base64.getEncoder().encodeToString(
                (apiKeyRetriever.getEnzoicApiKey() + ":" + apiSecretRetriever.getEnzoicApiSecret()).getBytes(
                    StandardCharsets.UTF_8));
        String hashedPassword;
        HttpRequest request;

        try {
            hashedPassword = hashingAlgorithm.hash(password);
            request = HttpRequest.newBuilder()
                .uri(new URI(API_URL))
                .header(AUTHORIZATION, BASIC + encodedAuth)
                .header(CONTENT_TYPE_FIELD, CONTENT_TYPE_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(
                    PARTIAL_SHA_256 + hashedPassword.substring(0, HASH_PREFIX_LENGTH) +
                        FIELDS_SHA_256))
                .build();
        } catch (URISyntaxException e) {
            throw new InvalidURIException("Invalid URI for Enzoic API", e);
        }

        return request;
    }
}
