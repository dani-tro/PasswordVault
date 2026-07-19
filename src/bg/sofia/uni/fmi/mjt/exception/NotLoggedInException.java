package bg.sofia.uni.fmi.mjt.exception;

public class NotLoggedInException extends CommandException {
    public NotLoggedInException(String message) {
        super(message);
    }

    public NotLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }
}
