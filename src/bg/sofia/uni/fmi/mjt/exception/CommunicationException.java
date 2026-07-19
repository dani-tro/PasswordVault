package bg.sofia.uni.fmi.mjt.exception;

public class CommunicationException extends Exception {
    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
