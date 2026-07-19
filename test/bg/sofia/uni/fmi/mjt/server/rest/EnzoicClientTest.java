package bg.sofia.uni.fmi.mjt.server.rest;

import bg.sofia.uni.fmi.mjt.algorithm.HashingAlgorithm;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnzoicClientTest {

    private EnzoicClient enzoicClient;
    private HashingAlgorithm mockHashingAlgorithm;
    private HttpClient mockHttpClient;
    private APISecretRetriever mockApiSecretRetriever;
    private APIKeyRetriever mockApiKeyRetriever;
    private HttpResponse<String> mockResponse;

    @BeforeEach
    void setUp() {
        mockHashingAlgorithm = mock();
        mockHttpClient = mock();
        mockApiSecretRetriever = mock();
        mockApiKeyRetriever = mock();
        mockResponse = mock();

        enzoicClient = new EnzoicClient(mockHashingAlgorithm, mockHttpClient, mockApiSecretRetriever, mockApiKeyRetriever);
    }

    @Test
    void testSendRequestWithValidPasswordReturnsResponseBody() throws Exception {
        when(mockHashingAlgorithm.hash(anyString())).thenReturn("hashedPassword");
        when(mockApiKeyRetriever.getEnzoicApiKey()).thenReturn("apiKey");
        when(mockApiSecretRetriever.getEnzoicApiSecret()).thenReturn("apiSecret");
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("responseBody");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);

        String response = enzoicClient.sendRequest("password");

        assertEquals("responseBody", response, "sendRequest should return the response body");
    }

    @Test
    void testSendRequestWithNoCandidatesFoundReturnsEmptyBody() throws Exception {
        when(mockHashingAlgorithm.hash(anyString())).thenReturn("hashedPassword");
        when(mockApiKeyRetriever.getEnzoicApiKey()).thenReturn("apiKey");
        when(mockApiSecretRetriever.getEnzoicApiSecret()).thenReturn("apiSecret");
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockResponse.body()).thenReturn("");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);

        String response = enzoicClient.sendRequest("password");

        assertEquals("", response, "sendRequest should return an empty body when no candidates are found");
    }

    @Test
    void testSendRequestThrowsAPIExceptionOnIOException() throws Exception {
        when(mockHashingAlgorithm.hash(anyString())).thenReturn("hashedPassword");
        when(mockApiKeyRetriever.getEnzoicApiKey()).thenReturn("apiKey");
        when(mockApiSecretRetriever.getEnzoicApiSecret()).thenReturn("apiSecret");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(IOException.class);

        assertThrows(APIException.class, () -> enzoicClient.sendRequest("password"), "sendRequest should throw an APIException when an IOException occurs");
    }

    @Test
    void testSendRequestThrowsAPIExceptionOnInterruptedException() throws Exception {
        when(mockHashingAlgorithm.hash(anyString())).thenReturn("hashedPassword");
        when(mockApiKeyRetriever.getEnzoicApiKey()).thenReturn("apiKey");
        when(mockApiSecretRetriever.getEnzoicApiSecret()).thenReturn("apiSecret");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(InterruptedException.class);

        assertThrows(APIException.class, () -> enzoicClient.sendRequest("password"), "sendRequest should throw an APIException when an InterruptedException occurs");
    }

    @Test
    void testSendRequestThrowsAPIExceptionOnNonSuccessStatusCode() throws Exception {
        when(mockHashingAlgorithm.hash(anyString())).thenReturn("hashedPassword");
        when(mockApiKeyRetriever.getEnzoicApiKey()).thenReturn("apiKey");
        when(mockApiSecretRetriever.getEnzoicApiSecret()).thenReturn("apiSecret");
        when(mockResponse.statusCode()).thenReturn(456);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);

        assertThrows(APIException.class, () -> enzoicClient.sendRequest("password"), "sendRequest should throw an APIException when the status code is not 200 or 404");
    }

}
