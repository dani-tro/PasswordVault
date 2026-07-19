package bg.sofia.uni.fmi.mjt.server.rest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class APIKeyRetrieverTest {
    @Test
    void testGetInstanceReturnsSameInstance() {
        APIKeyRetriever instance1 = APIKeyRetriever.getInstance();
        APIKeyRetriever instance2 = APIKeyRetriever.getInstance();
        assertEquals(instance1, instance2, "getInstance should return the same instance every time");
    }

    @Test
    void testGetEnzoicApiKeyReturnsCorrectValue() {
        APIKeyRetriever apiKeyRetriever = APIKeyRetriever.getInstance();
        String expected = System.getenv("ENZOIC_API_KEY");
        assertEquals(expected, apiKeyRetriever.getEnzoicApiKey(),
            "getEnzoicApiKey should return the value of the ENZOIC_API_KEY environment variable");
    }
}
