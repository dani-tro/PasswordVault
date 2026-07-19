package bg.sofia.uni.fmi.mjt.server.rest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class APISecretRetrieverTest {
    @Test
    void testGetInstanceReturnsSameInstance() {
        APISecretRetriever instance1 = APISecretRetriever.getInstance();
        APISecretRetriever instance2 = APISecretRetriever.getInstance();
        assertEquals(instance1, instance2, "getInstance should return the same instance every time");
    }

    @Test
    void testGetEnzoicApiSecretReturnsCorrectValue() {
        APISecretRetriever apiSecretRetriever = APISecretRetriever.getInstance();
        String expected = System.getenv("ENZOIC_API_SECRET");
        assertEquals(expected, apiSecretRetriever.getEnzoicApiSecret(),
            "getEnzoicApiSecret should return the value of the ENZOIC_API_SECRET environment variable");
    }
}
