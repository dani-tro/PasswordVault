package bg.sofia.uni.fmi.mjt.server.rest;

public class APIKeyRetriever {

    private static final String ENZOIC_API_KEY_ENV = System.getenv("ENZOIC_API_KEY");

    private static APIKeyRetriever instance = null;

    private APIKeyRetriever() {
    }

    public static APIKeyRetriever getInstance() {
        if (instance == null) {
            instance = new APIKeyRetriever();
        }
        return instance;
    }

    public String getEnzoicApiKey() {
        return ENZOIC_API_KEY_ENV;
    }
}
