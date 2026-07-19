package bg.sofia.uni.fmi.mjt.communication;

import java.io.Serializable;

public record ServerResponse(String message, boolean isSuccessful) implements Serializable  {
}
