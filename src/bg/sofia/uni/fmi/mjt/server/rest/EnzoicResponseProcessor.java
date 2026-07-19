package bg.sofia.uni.fmi.mjt.server.rest;

import com.google.gson.Gson;

public class EnzoicResponseProcessor {
    public APIResponse processResponse(String response) {
        if (response == null || response.isBlank()) {
            return new APIResponse(new APICandidate[]{});
        }
        Gson gson = new Gson();
        return gson.fromJson(response, APIResponse.class);
    }
}
