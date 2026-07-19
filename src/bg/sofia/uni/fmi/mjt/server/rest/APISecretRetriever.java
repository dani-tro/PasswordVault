package bg.sofia.uni.fmi.mjt.server.rest;

public class APISecretRetriever {
    private static final String ENZOIC_API_SECRET_ENV = System.getenv("ENZOIC_API_SECRET");

    private static APISecretRetriever instance = null;

    private APISecretRetriever() {
    }

    public static APISecretRetriever getInstance() {
        if (instance == null) {
            instance = new APISecretRetriever();
        }
        return instance;
    }

    public String getEnzoicApiSecret() {
        return ENZOIC_API_SECRET_ENV;
    }

}
