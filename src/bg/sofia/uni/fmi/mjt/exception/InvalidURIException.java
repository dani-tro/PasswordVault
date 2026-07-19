package bg.sofia.uni.fmi.mjt.exception;

public class InvalidURIException extends APIException {
    public InvalidURIException(String message) {
        super(message);
    }

    public InvalidURIException(String message, Throwable cause) {
        super(message, cause);
    }
}
